package com.ptimulus.log;

import android.content.Context;
import android.telephony.SmsManager;
import android.text.format.Time;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.ptimulus.PtimulusApplication;

public class ScreenLogger implements IPtimulusLogger {
/*
	public final String defaultDestPhoneNumber = "2096270247";
	String destPhoneNumber;
	final long sendInterval = 0;

	private SmsManager smsmanager;
	private long lastSent;
	*/
	private boolean logging;
	
	private final Context ctx;

	public ScreenLogger(PtimulusApplication app) {
		//smsmanager = SmsManager.getDefault();
		//lastSent = 0;
		logging = false;
		
		ctx = app.getApplicationContext();
		
		/*destPhoneNumber = app.getIcarusPreferences().getString(
				"targetPhoneNumber", defaultDestPhoneNumber);
		
		smsmanager.sendTextMessage(destPhoneNumber, null, "Hello beautiful", null, null);*/
	}

	public void logDataEvent(LogEntryType type, String data, long ts) {
		if (!logging)
			return;

		Toast.makeText(ctx, "ScreenLog long: " + type + ": " + data, Toast.LENGTH_LONG).show();
		
		/*
		FileOutputStream fos;
		try {
			fos = ctx.openFileOutput(logFileName, Context.MODE_PRIVATE);
		    fos.write("THIS IS A TEST STRING".getBytes());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		/*
		if (name != "gps") // only send SMS for gps coords
			return;

		// send SMS if okay to do so
		boolean sendIntervalExceeded = lastSent < (ts - sendInterval);
		if (hasService && sendIntervalExceeded) {
			smsmanager.sendTextMessage(destPhoneNumber, null, data, null, null);
			lastSent = ts;
		}*/
	}

	public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
		logging = isChecked;
		if (isChecked)
			this.startLogging();
		else
			this.stopLogging();
	}

	public void startLogging() {
		logging = true;
		this.logDataEvent(LogEntryType.APP_LIFECYCLE, "Screen Logging Started", 0);
	}

	public void stopLogging() {
		if (logging)
			return;
		logging = false;
	}

}
