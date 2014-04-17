package com.ptimulus.log;


public interface IPtimulusLogger {

	public void logDataEvent(LogEntryType type, String entry);
	
	public void startLogging();

	public void stopLogging();
}