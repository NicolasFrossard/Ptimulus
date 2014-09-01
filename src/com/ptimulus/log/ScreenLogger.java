package com.ptimulus.log;

import java.util.ArrayList;
import java.util.Iterator;

import com.ptimulus.utils.DateFactory;

public class ScreenLogger implements IPtimulusLogger {

	private final int QUEUE_SIZE = 10;
    private final ArrayList<String> buffer;
	
	private final Object lock = new Object();

    private boolean logging;
	
	public ScreenLogger() {
        this.buffer = new ArrayList<String>(QUEUE_SIZE + 1);
        logging = false;
	}
	
	@Override
	public void logDataEvent(LogEntryType type, String entry) {
        if (!logging)
            return;

		synchronized (lock) {
			buffer.add(DateFactory.nowAsString() + " | " + type + ": " + entry	+ " ");
			if(buffer.size() > QUEUE_SIZE)
                buffer.remove(0);
		}
	}

	@Override
	public void startLogging() {
        logging = true;
	}

	@Override
	public void stopLogging() {
        logging = false;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		synchronized (lock) {
            for (Iterator<String> it = buffer.iterator(); it.hasNext(); ) {
                builder.insert(0, it.next() + "\r\n");
			}
		}
		
		return builder.toString();
	}
}
