package com.ptimulus.log;


public interface IPtimulusLogger {

	public void logDataEvent(String name, String data, long ts,	boolean gsmOk);
	
	public void startLogging();

	public void stopLogging();
}