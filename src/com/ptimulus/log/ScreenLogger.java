package com.ptimulus.log;

import android.content.Context;
import android.widget.Toast;

import com.ptimulus.PtimulusApplication;

public class ScreenLogger implements IPtimulusLogger {

	private boolean logging;
	
	private final Context ctx;

	public ScreenLogger(PtimulusApplication app) {
		logging = false;
		
		ctx = app.getApplicationContext();
	}

	public void logDataEvent(LogEntryType type, String data) {
		if (!logging)
			return;

		Toast.makeText(ctx, type + ": " + data, Toast.LENGTH_LONG).show();
	}

	public void startLogging() {
		logging = true;
		this.logDataEvent(LogEntryType.APP_LIFECYCLE, "Screen Logging Started");
	}

	public void stopLogging() {
		if (logging)
			return;
		logging = false;
	}
}
