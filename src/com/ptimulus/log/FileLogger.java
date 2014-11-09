/*
 * Copyright (C) 2014 Ptimulus
 * http://www.ptimulus.eu
 * 
 * This file is part of Ptimulus.
 * 
 * Ptimulus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Ptimulus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Ptimulus.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

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
