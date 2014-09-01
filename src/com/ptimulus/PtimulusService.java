package com.ptimulus;

import java.util.ArrayList;
import java.util.List;

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

    public final String DEFAULT_DEST_NUMBER = "2096270247";

	private PowerManager pm;
	private PowerManager.WakeLock wl;
    private MediaPlayer player;

    private final List<IPtimulusLogger> loggers = new ArrayList<IPtimulusLogger>();
    private ScreenLogger screenLogger;

    private TimerEvent timerEvent;
    private SmsSender smsSender;
	private LocationEvent locationEvent;
    private AccelerometerEvent accelerometerEvent;
    private MagnetometerEvent magnetometerEvent;
	private TelephonyEvent telephonyEvent;

    private final IBinder binder = new PtimulusServiceBinder();

	public PtimulusService() {
		super();
	}

    public void timerTick() {
        locationEvent.tick();
        telephonyEvent.tick();
        accelerometerEvent.tick();
        magnetometerEvent.tick();
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

    public String telephonyUIData() {
        return telephonyEvent.toString();
    }
    
	public String logUIData() {
        if(isEnabled(getApplicationContext()))
		    return screenLogger.toString();
        else
            return "Logging disabled";
	}
	
    public static void activateIfNecessary(Context ctx) {
        if (isEnabled(ctx)) {
            Intent startIntent = new Intent(ctx, PtimulusService.class);
            ctx.startService(startIntent);
        }
    }

	private static boolean isEnabled(Context ctx) {
		return ((PtimulusApplication) ctx.getApplicationContext())
				.getPtimulusPreferences().getBoolean("enableLogging", false);
	}

	private Notification updateNotification(String message) {
        Context ctx = getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) ctx
				.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification n = new Notification(R.drawable.ptimuluslogo,
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

	@Override
	public void onCreate() {
		super.onCreate();

        Context ctx = getApplicationContext();
        SharedPreferences preferences = ((PtimulusApplication) ctx).getPtimulusPreferences();

        smsSender = new SmsSender(preferences.getString("targetPhoneNumber", DEFAULT_DEST_NUMBER));
		loggers.add(new FileLogger());
		screenLogger = new ScreenLogger();
		loggers.add(screenLogger);

        accelerometerEvent = new AccelerometerEvent(this, ctx);
        magnetometerEvent = new MagnetometerEvent(this, ctx);
		locationEvent = new LocationEvent(this, ctx);
		telephonyEvent = new TelephonyEvent(this, ctx);
        timerEvent = new TimerEvent(this);
				
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PtimulusMission");

        for(IPtimulusLogger logger : loggers) {
            logger.startLogging();
        }

        relayLog(LogEntryType.APP_LIFECYCLE, "Starting the service.");

        accelerometerEvent.startListening();
        magnetometerEvent.startListening();
        locationEvent.startListening();
        telephonyEvent.startListening();

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

        accelerometerEvent.stopListening();
        magnetometerEvent.stopListening();
        locationEvent.stopListening();
        telephonyEvent.stopListening();

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

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (!isEnabled(getApplicationContext()))
			stopSelf();
        smsSender.UpdateDestination(sharedPreferences.getString("targetPhoneNumber", DEFAULT_DEST_NUMBER));
	}
}
