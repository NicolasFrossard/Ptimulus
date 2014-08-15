package com.ptimulus.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import android.os.Environment;
import com.ptimulus.utils.DateFactory;

public class FileLogger implements IPtimulusLogger {

	private final String LOG_FILENAME = "ptimulus%s.log";
	private PrintWriter file;
	
	private boolean logging;

	public FileLogger() {
		logging = false;
	}

    /**
     * Record a new event.
     * @param type type of log entry
     * @param entry text of log entry
     */
	public void logDataEvent(LogEntryType type, String entry) {
		if (!logging)
			return;
		
		file.println(DateFactory.nowAsString() + " | " + type + ": " + entry	+ " ");
		file.flush();
	}

    /**
     * Enable the logging.
     */
	public void startLogging() {
        if(logging)
            return;

        String filename = String.format(LOG_FILENAME, DateFactory.nowForFilename());

		try {
			File f = new File(Environment.getExternalStorageDirectory().getPath(), filename);

            file = new PrintWriter(new FileOutputStream(f, true));
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		logging = true;
		this.logDataEvent(LogEntryType.APP_LIFECYCLE, "File logging Started");
	}

    /**
     * Disable the logging.
     */
	public void stopLogging() {
		if (!logging)
			return;

		file.close();
		logging = false;
	}

}
