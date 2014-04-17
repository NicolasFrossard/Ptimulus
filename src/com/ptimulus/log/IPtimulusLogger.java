package com.ptimulus.log;


public interface IPtimulusLogger {

	public void logDataEvent(LogEntryType type, String entry, long timestamp);
	
	public void startLogging();

	public void stopLogging();
}