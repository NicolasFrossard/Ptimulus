package com.ptimulus.log;

import android.telephony.SmsManager;
import android.widget.CompoundButton;

import com.ptimulus.PtimulusApplication;
import com.ptimulus.utils.DateFactory;

public class SmsLogger implements IPtimulusLogger {

	public final String defaultDestPhoneNumber = "2096270247";
	private String destPhoneNumber;
	private final long sendInterval = 0;

	private SmsManager smsmanager;
	private long lastSent;
	
	private boolean logging;


	public SmsLogger(PtimulusApplication app) {
		smsmanager = SmsManager.getDefault();

        lastSent = 0;
		logging = false;

		
		destPhoneNumber = app.getPtimulusPreferences().getString(
				"targetPhoneNumber", defaultDestPhoneNumber);
		
		smsmanager.sendTextMessage(destPhoneNumber, null, "Hello beautiful", null, null);
	}

	public void logDataEvent(LogEntryType type, String data) {
		if (!logging)
			return;

		if (type != LogEntryType.GPS) // only send SMS for gps coords
			return;

		// send SMS if okay to do so
		Long now = DateFactory.nowAsLong();

		if (lastSent + sendInterval > now) {
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
