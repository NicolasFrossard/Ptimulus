package com.ptimulus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import android.content.Context;
import android.telephony.SmsManager;
import android.text.format.Time;
import android.widget.CompoundButton;

public class IcarusLogger implements DataSource.IcarusListener {

	public final String defaultDestPhoneNumber = "2096270247";
	String destPhoneNumber;
	final String logfileName = "IcarusLogTestKK";
	final long sendInterval = 0;

	private PrintWriter file;
	private SmsManager smsmanager;
	private long lastSent;
	
	private boolean logging;
	
	private final Context ctx;

	public IcarusLogger(IcarusApplication app) {
		smsmanager = SmsManager.getDefault();
		lastSent = 0;
		logging = false;
		
		ctx = app.getApplicationContext();
		
		destPhoneNumber = app.getIcarusPreferences().getString(
				"targetPhoneNumber", defaultDestPhoneNumber);
		
		smsmanager.sendTextMessage(destPhoneNumber, null, "Hello beautiful", null, null);
	}

	public void logDataEvent(String name, String data, long ts,
			boolean hasService) {
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
		
		file.println(System.currentTimeMillis() + " " + name + ": " + data
				+ " " + ts);
		file.flush();

		if (name != "gps") // only send SMS for gps coords
			return;

		// send SMS if okay to do so
		boolean sendIntervalExceeded = lastSent < (ts - sendInterval);
		if (hasService && sendIntervalExceeded) {
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
			File f = new File("/sdcard", logfileName);
			/*
			try {
				f.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			*/
			file = new PrintWriter(new FileOutputStream(f, true));
			
			//FileWriter fw = new FileWriter(f, true);
			//fw.write("kikoo");
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		logging = true;
		this.logDataEvent("log", "Logging Started", 0, false);
	}

	public void stopLogging() {
		if (logging)
			return;
		file.close();
		logging = false;
	}

}
