package com.ptimulus.log;

import java.util.ArrayList;
import java.util.Iterator;

import com.ptimulus.utils.DateFactory;

public class ScreenLogger implements IPtimulusLogger {

	private final int QUEUE_SIZE = 10;
    private final ArrayList<String> buffer;
	
	
	public ScreenLogger() {
		this.buffer = new ArrayList<String>(QUEUE_SIZE + 1);
	}
	
	@Override
	public void logDataEvent(LogEntryType type, String entry) {
        synchronized (buffer) {
            buffer.add(DateFactory.nowAsString() + " | " + type + ": " + entry + " ");
            if (buffer.size() > QUEUE_SIZE)
                buffer.remove(0);
        }
	}

	@Override
	public void startLogging() {
		
	}

	@Override
	public void stopLogging() {

	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

        synchronized (buffer) {
            for (Iterator<String> it = buffer.iterator(); it.hasNext(); ) {
                builder.insert(0, it.next() + "\r\n");
            }
        }
		
		return builder.toString();
	}
}
