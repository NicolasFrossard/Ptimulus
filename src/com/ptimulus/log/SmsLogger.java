package com.ptimulus.log;

import android.telephony.SmsManager;

import com.ptimulus.PtimulusApplication;
import com.ptimulus.utils.DateFactory;

public class SmsLogger implements IPtimulusLogger {

	public final String DEFAULT_DEST_NUMBER = "2096270247";
	private final long SEND_INTERVAL = 0;

    private String destPhoneNumber;

	private SmsManager smsmanager;
	private long lastSent;
	
	private boolean logging;


	public SmsLogger(PtimulusApplication app) {
		smsmanager = SmsManager.getDefault();

        lastSent = 0;
		logging = false;

		destPhoneNumber = app.getPtimulusPreferences().getString(
				"targetPhoneNumber", DEFAULT_DEST_NUMBER);
		
		smsmanager.sendTextMessage(destPhoneNumber, null, "Hello beautiful", null, null);
	}

	public void logDataEvent(LogEntryType type, String data) {
		if (!logging)
			return;

		if (type != LogEntryType.GPS) // only send SMS for gps coords
			return;

		// send SMS if okay to do so
		Long now = DateFactory.nowAsLong();

		if (lastSent + SEND_INTERVAL > now) {
			smsmanager.sendTextMessage(destPhoneNumber, null, data, null, null);
			lastSent = now;
		}
	}

	public void startLogging() {
		logging = true;
	}

	public void stopLogging() {
		logging = false;
	}

}
