
package com.ptimulus.log;

import java.util.ArrayList;
import com.ptimulus.utils.DateFactory;

public class ScreenLogger implements IPtimulusLogger {

	private final int QUEUE_SIZE = 10;
	private final ArrayList<String> buffer;
	private int nextIdx;
	
	
	public ScreenLogger() {
		this.buffer = new ArrayList<String>(QUEUE_SIZE);
		this.nextIdx = 0;
	}
	
	@Override
	public void logDataEvent(LogEntryType type, String entry) {
        synchronized (buffer) {
        	buffer.remove(nextIdx);
            buffer.add(nextIdx, DateFactory.nowAsString() + " | " + type + ": " + entry + " ");
            this.nextIdx++;
            if(this.nextIdx >= QUEUE_SIZE){
            	this.nextIdx = 0;
            }
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
		int i;
		int j;

		j = this.nextIdx;
        
        for(i=0; i<QUEUE_SIZE; i++) {
        	if(j >= QUEUE_SIZE) {
        		j = 0;
        	}
        	
        	synchronized (buffer) {
        		builder.append(buffer.get(j));
        	}
        	
        	j++;
        }
	
		return builder.toString();
	}
}
