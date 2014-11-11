/*
 * Copyright (C) 2014 Ptimulus
 * http://www.ptimulus.eu
 * 
 * This file is part of Ptimulus.
 * 
 * Ptimulus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Ptimulus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Ptimulus.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package com.ptimulus;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.hardware.SensorEvent;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.telephony.ServiceState;

import com.ptimulus.event.*;
import com.ptimulus.log.*;
import com.ptimulus.utils.SmsSender;

/*
Done using PlantUML: http://plantuml.sourceforge.net/
Live edit and Ascii graph: http://www.plantuml.com/plantuml/

@startuml

PreFlight : Waiting for launch
TakeOff : Video for 2 min
Ascent : Picture each 30 seconds, Video each 10 min for 20sec
DescentStart : Video for 2 min
Descent : Picture each 1 min, Video each 2 min, send coords as soon as possible x 3
Upload : send coords as soon as possible x 3, then upload data

[*] --> PreFlight
PreFlight --> TakeOff : User input
TakeOff --> Ascent : 2 min
Ascent --> DescentStart : Detection with GPS/accel
DescentStart --> Descent : 2 min
Descent --> Upload

@enduml


                                 ,------.
                                 |*start|
                                 |------|
                                 |------|
                                 `------'
                                     |
                                     |
                           ,------------------.
                           |PreFlight         |
                           |------------------|
                           |Waiting for launch|
                           |------------------|
                           `------------------'
                                     |
                            ,---------------.
                            |TakeOff        |
                            |---------------|
                            |Video for 2 min|
                            |---------------|
                            `---------------'
                                     |

          ,----------------------------------------------------.
          |Ascent                                              |
          |----------------------------------------------------|
          |Picture each 30 seconds, Video each 10 min for 20sec|
          |----------------------------------------------------|
          `----------------------------------------------------'
                                     |
                            ,---------------.
                            |DescentStart   |
                            |---------------|
                            |Video for 2 min|
                            |---------------|
                            `---------------'
                                     |
,-------------------------------------------------------------------------.
|Descent                                                                  |
|-------------------------------------------------------------------------|
|Picture each 1 min, Video each 2 min, send coords as soon as possible x 3|
|-------------------------------------------------------------------------|
`-------------------------------------------------------------------------'
                                     |
                                     |
         ,-----------------------------------------------------.
         |Upload                                               |
         |-----------------------------------------------------|
         |send coords as soon as possible x 3, then upload data|
         |-----------------------------------------------------|
         `-----------------------------------------------------'
 */

public class PtimulusService extends Service implements OnSharedPreferenceChangeListener {

    private PowerManager                         pm;
    private PowerManager.WakeLock                wl;
    private MediaPlayer                          player;

    private final List<IPtimulusLogger>          loggers               = new ArrayList<IPtimulusLogger>();
    private ScreenLogger                         screenLogger;

    private final List<IEvent<? extends Object>> events                = new ArrayList<IEvent<? extends Object>>();
    @SuppressWarnings("unused")
    private TimerEvent                           timerEvent;
    private SmsSender                            smsSender;
    private LocationEvent                        locationEvent;
    private AccelerometerEvent                   accelerometerEvent;
    private MagnetometerEvent                    magnetometerEvent;
    private GyroscopeEvent                       gyroscopeEvent;
    private BatteryEvent                         batteryEvent;
    private TelephonyEvent                       telephonyEvent;
    private PtimulusCamera                       mCamera               = null;

    private final IBinder                        binder                = new PtimulusServiceBinder();

    private long                                 locationSentTime      = -1000;

    private int                                  TAKE_PICTURE_INTERVAL = 120;
    private long                                 pictureTakenTime      = -TAKE_PICTURE_INTERVAL;
    
	public PtimulusService() {
		super();
	}

    @Override
    public void onCreate() {
        super.onCreate();

        Context ctx = getApplicationContext();
        SharedPreferences preferences = ((PtimulusApplication) ctx).getPtimulusPreferences();

        smsSender = new SmsSender(this,
                preferences.getString("targetPhoneNumber1", ""),
                preferences.getString("targetPhoneNumber2", ""),
                preferences.getString("targetPhoneNumber3", ""));

        loggers.add(new FileLogger());
        loggers.add(screenLogger = new ScreenLogger());

        events.add(accelerometerEvent = new AccelerometerEvent(this, ctx));
        events.add(magnetometerEvent = new MagnetometerEvent(this, ctx));
        events.add(gyroscopeEvent = new GyroscopeEvent(this, ctx));
        events.add(batteryEvent = new BatteryEvent(this, ctx));
        events.add(locationEvent = new LocationEvent(this, ctx));
        events.add(telephonyEvent = new TelephonyEvent(this, ctx));

        mCamera = PtimulusCamera.createCamera();
        
        timerEvent = new TimerEvent(this);

        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PtimulusMission");

        for(IPtimulusLogger logger : loggers) {
            logger.startLogging();
        }

        relayLog(LogEntryType.APP_LIFECYCLE, "Starting the service.");
        smsSender.SendSMS("Ptimulus service started.");


        for(IEvent<?> event : events) {
            event.startListening();
        }

        wl.acquire();

        // Play a audio file to mark the start
        player = MediaPlayer.create(ctx, R.raw.ready);
        player.start();

        preferences.registerOnSharedPreferenceChangeListener(this);

        Notification n = updateNotification("Ptimulus is active");
        startForeground(PtimulusApplication.NOTIFY_PTIMULUS_ACTIVE, n);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        for(IEvent<?> event : events) {
            event.stopListening();
        }

        relayLog(LogEntryType.APP_LIFECYCLE, "Stopping the service.");

        for(IPtimulusLogger logger : loggers) {
            logger.stopLogging();
        }

        player.reset();
        player.release();

        wl.release();

        ((PtimulusApplication) getApplicationContext()).getPtimulusPreferences().unregisterOnSharedPreferenceChangeListener(this);
        StopNotification();
    }

    public void timerTick(int counter) {
        for(IEvent<?> event : events) {
            event.tick(counter);
        }

        if(telephonyEvent.hasTelephonyNetwork() &&
                locationEvent.hasData() &&
                counter - locationSentTime > 30)
        {
            locationSentTime = counter;
            smsSender.SendSMS(locationEvent.toStringSMS());
            relayLog(LogEntryType.APP_LIFECYCLE, "Location Sent");
        }
        
        if(counter - pictureTakenTime >= TAKE_PICTURE_INTERVAL)
        {
        	pictureTakenTime = counter;
            mCamera.takePicture(this);	
        }
        
    }

    public void locationEvent(Location l) {
        String text = String.format("%s,%s %s %s", l.getLatitude(), l.getLongitude(), l.getAltitude(), l.getAccuracy());

        relayLog(LogEntryType.GPS, text);
    }

    public void accelerometerEvent(SensorEvent event) {
        StringBuilder data = new StringBuilder();

        for (float f : event.values) {
            data.append(" ");
            data.append(f);
        }

        relayLog(LogEntryType.ACCEL, data.toString());
    }
    
    public void magnetometerEvent(SensorEvent event) {
    	StringBuilder data = new StringBuilder();

        for (float f : event.values) {
            data.append(" ");
            data.append(f);
        }

        relayLog(LogEntryType.MAGN, data.toString());
	}

    public void gyroscopeEvent(SensorEvent event) {
        StringBuilder data = new StringBuilder();

        for (float f : event.values) {
            data.append(" ");
            data.append(f);
        }

        relayLog(LogEntryType.GYRO, data.toString());
    }

    public void batteryEvent(BatteryEvent.BatteryState event) {
        String text = String.format(Locale.US,"%f %f %f", event.temp, event.voltage, event.percent);

        relayLog(LogEntryType.BAT, text);
    }


    public void telephonyEvent(ServiceState serviceState) {
        relayLog(LogEntryType.PHONE_STATE, serviceState.toString());
    }

    /**
     * Relay log entry to all registered logger
     * @param type
     * @param entry
     */
    public void relayLog(LogEntryType type, String entry) {
        for(IPtimulusLogger logger : loggers)
            logger.logDataEvent(type, entry);
    }

    public String locationUIData() {
        return locationEvent.toString();
    }

    public String accelerometerUIData() {
        return accelerometerEvent.toString();
    }
    
    public String magnetometerUIData() {
		return magnetometerEvent.toString();
	}

    public String gyroscopeUIData() {
        return gyroscopeEvent.toString();
    }

    public String batteryUIData() {
        return batteryEvent.toString();
    }

    public String telephonyUIData() {
        return telephonyEvent.toString();
    }
    
	public String logUIData() {
		return screenLogger.toString();
	}

	private Notification updateNotification(String message) {
        Context ctx = getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) ctx
				.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification n = new Notification(R.drawable.ic_status_ptimulus,
                message, System.currentTimeMillis());

        n.flags |= Notification.FLAG_ONGOING_EVENT;
        Intent ni = new Intent(ctx, PtimulusActivity.class);

        PendingIntent pi = PendingIntent.getActivity(ctx, 0, ni, 0);
        n.setLatestEventInfo(ctx, "Ptimulus", message, pi);
        notificationManager.notify(PtimulusApplication.NOTIFY_PTIMULUS_ACTIVE, n);

        return n;
	}

    private void StopNotification() {
        Context ctx = getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) ctx
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(PtimulusApplication.NOTIFY_PTIMULUS_ACTIVE);
    }

    public class PtimulusServiceBinder extends Binder {

        public PtimulusService getService() {
            return PtimulusService.this;
        }
    }

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        smsSender.UpdateDestination1(sharedPreferences.getString("targetPhoneNumber1", ""));
        smsSender.UpdateDestination2(sharedPreferences.getString("targetPhoneNumber2", ""));
        smsSender.UpdateDestination3(sharedPreferences.getString("targetPhoneNumber3", ""));
	}
}
