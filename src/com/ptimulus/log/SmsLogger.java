package com.ptimulus.log;

import android.telephony.SmsManager;

import com.ptimulus.PtimulusApplication;
import com.ptimulus.utils.DateFactory;

public class SmsLogger implements IPtimulusLogger {

	private final long SEND_INTERVAL = 0;

    private final String destPhoneNumber;
	private final SmsManager smsmanager;

    private long lastSent;
	private boolean logging;

	public SmsLogger(String destPhoneNumber) {
        this.destPhoneNumber = destPhoneNumber;
		this.smsmanager = SmsManager.getDefault();

        this.lastSent = 0;
		this.logging = false;
		
		smsmanager.sendTextMessage(destPhoneNumber, null, "Ptimulus started", null, null);
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
