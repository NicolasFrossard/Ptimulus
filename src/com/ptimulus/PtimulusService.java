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
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;

import android.telephony.ServiceState;
import com.ptimulus.event.*;
import com.ptimulus.log.*;

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

    private boolean active;
	private PowerManager pm;
	private PowerManager.WakeLock wl;

    private final List<IPtimulusLogger> loggers = new ArrayList<IPtimulusLogger>();
    private ScreenLogger screenLogger;

    private TimerEvent timerEvent;
	private LocationEvent locationEvent;
    private AccelerometerEvent accelerometerEvent;
	private TelephonyEvent telephonyEvent;

    private final IBinder binder = new PtimulusServiceBinder();
    private PtimulusActivity activity;

	public PtimulusService() {
		super();
		active = false;
	}

    public void start(Context ctx) {
        if (active)
            return;

        accelerometerEvent.startListening();
        locationEvent.startListening();
        telephonyEvent.startListening();

        for(IPtimulusLogger logger : loggers) {
            logger.startLogging();
        }

        wl.acquire();
        active = true;

        // Play a audio file to mark the start
        MediaPlayer player = MediaPlayer.create(ctx, R.raw.ready);
        player.start();
        player.release();
    }

    public void stop() {
        if (!active)
            return;

        accelerometerEvent.stopListening();
        locationEvent.stopListening();
        telephonyEvent.stopListening();

        for(IPtimulusLogger logger : loggers) {
            logger.stopLogging();
        }

        wl.release();
        active = false;
    }

    public void timerTick() {

        locationEvent.tick();
        telephonyEvent.tick();
        accelerometerEvent.tick();
    }

    public void locationEvent(Location l) {
        String text = String.format("%s,%s %s", l.getLatitude(), l.getLongitude(), l.getAltitude());

        relayLog(LogEntryType.GPS, text);
    }

    public void sensorEvent(android.hardware.SensorEvent event) {
        StringBuilder data = new StringBuilder();

        for (float f : event.values) {
            data.append(" ");
            data.append(f);
        }

        relayLog(LogEntryType.SENSOR, data.toString());
    }

    public void telephonyEvent(ServiceState serviceState) {
        relayLog(LogEntryType.PHONE_STATE, serviceState.toString());
    }

    /**
     * Relay log entry to all registered logger
     * @param type
     * @param entry
     */
    private void relayLog(LogEntryType type, String entry) {
        for(IPtimulusLogger logger : loggers)
            logger.logDataEvent(type, entry);
    }

    public String locationUIdata() {
        return locationEvent.toString();
    }

    public String accelerometerUIdata() {
        return accelerometerEvent.toString();
    }

    public String telephonyUIdata() {
        return telephonyEvent.toString();
    }
    
	public CharSequence logUIData() {
		return screenLogger.toString();
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

	private static Notification updateNotification(Context ctx, boolean enabled) {
		NotificationManager notificationManager = (NotificationManager) ctx
				.getSystemService(Context.NOTIFICATION_SERVICE);

		if (enabled) {
			Notification n = new Notification(R.drawable.ptimuluslogo,
					"Ptimulus is Active", System.currentTimeMillis());

			n.flags |= Notification.FLAG_ONGOING_EVENT;
			Intent ni = new Intent(ctx, PtimulusActivity.class);

			PendingIntent pi = PendingIntent.getActivity(ctx, 0, ni, 0);
			n.setLatestEventInfo(ctx, "Ptimulus", "Ptimulus is active", pi);
			notificationManager.notify(PtimulusApplication.NOTIFY_PTIMULUS_ACTIVE, n);

			return n;
		} else {
			notificationManager.cancel(PtimulusApplication.NOTIFY_PTIMULUS_ACTIVE);
			return null;
		}
	}

    public class PtimulusServiceBinder extends Binder {

        public PtimulusService getService() {
            return PtimulusService.this;
        }

        public void registerActivity(PtimulusActivity activity) {
            PtimulusService.this.activity = activity;
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

		loggers.add(new SmsLogger(preferences.getString("targetPhoneNumber", DEFAULT_DEST_NUMBER)));
		loggers.add(new FileLogger());
		screenLogger = new ScreenLogger();
		loggers.add(screenLogger);

        accelerometerEvent = new AccelerometerEvent(this, ctx);
		locationEvent = new LocationEvent(this, ctx);
		telephonyEvent = new TelephonyEvent(this, ctx);
        timerEvent = new TimerEvent(this);
				
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PtimulusMission");
		
		start(this);
        preferences.registerOnSharedPreferenceChangeListener(this);

		Notification n = updateNotification(this, true);
		startForeground(PtimulusApplication.NOTIFY_PTIMULUS_ACTIVE, n);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stop();
        ((PtimulusApplication) getApplicationContext()).getPtimulusPreferences().unregisterOnSharedPreferenceChangeListener(this);
		updateNotification(this, false);
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (!isEnabled(this))
			stopSelf();
	}
}
