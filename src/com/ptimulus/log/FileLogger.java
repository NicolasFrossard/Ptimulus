package com.ptimulus.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import com.ptimulus.utils.DateFactory;

public class FileLogger implements IPtimulusLogger {

	final String logfileName = "ptimulus.log";
	private PrintWriter file;
	
	private boolean logging;

	public FileLogger() {
		logging = false;
	}

	public void logDataEvent(LogEntryType type, String data) {
		if (!logging)
			return;
		
		file.println(DateFactory.nowAsString() + " | " + type + ": " + data	+ " ");
		file.flush();

	}

	public void startLogging() {
		try {
			File f = new File("/sdcard", logfileName);
			file = new PrintWriter(new FileOutputStream(f, true));
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		logging = true;
		this.logDataEvent(LogEntryType.APP_LIFECYCLE, "File logging Started");
	}

	public void stopLogging() {
		if (logging)
			return;
		file.close();
		logging = false;
	}

}
