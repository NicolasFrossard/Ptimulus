package com.ptimulus.log;

import android.telephony.SmsManager;
import android.text.format.Time;
import android.util.Log;
import android.widget.CompoundButton;

import com.ptimulus.PtimulusApplication;

public class SmsLogger implements IPtimulusLogger {

	public final String defaultDestPhoneNumber = "2096270247";
	String destPhoneNumber;
	final long sendInterval = 0;

	private SmsManager smsmanager;
	private long lastSent;
	
	private boolean logging;
	
	//private final Context ctx;

	public SmsLogger(PtimulusApplication app) {
		smsmanager = SmsManager.getDefault();
		//lastSent = 0;
		logging = false;
		
		//ctx = app.getApplicationContext();
		
		destPhoneNumber = app.getIcarusPreferences().getString(
				"targetPhoneNumber", defaultDestPhoneNumber);
		
		smsmanager.sendTextMessage(destPhoneNumber, null, "Hello beautiful", null, null);
	}

	public void logDataEvent(LogEntryType type, String data, long ts) {
		if (!logging)
			return;
		
		/*
		FileOutputStream fos;
		try {
			fos = ctx.openFileOutput(logFileName, Context.MODE_PRIVATE);
		    fos.write("THIS IS A TEST STRING".getBytes());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		if (type != LogEntryType.GPS) // only send SMS for gps coords
			return;

		// send SMS if okay to do so
		boolean sendIntervalExceeded = lastSent < (ts - sendInterval);
		if (/*hasService &&*/ sendIntervalExceeded) {
			smsmanager.sendTextMessage(destPhoneNumber, null, data, null, null);
			lastSent = ts;
		}
	}

	public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
		logging = isChecked;
		if (isChecked)
			this.startLogging();
		else
			this.stopLogging();
	}

	public void startLogging() {
		try {
			Time t = new Time();
			t.setToNow();
			
			/*
			try {
				f.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			*/
			
			//FileWriter fw = new FileWriter(f, true);
			//fw.write("kikoo");
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		logging = true;
	}

	public void stopLogging() {
		if (logging)
			return;
		logging = false;
	}

}
