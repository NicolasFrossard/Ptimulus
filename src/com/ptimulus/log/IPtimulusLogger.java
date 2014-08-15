package com.ptimulus.log;

/**
 * Interface of a logger.
 */
public interface IPtimulusLogger {

    /**
     * Record a new event.
     * @param type type of log entry
     * @param entry text of log entry
     */
	public void logDataEvent(LogEntryType type, String entry);

    /**
     * Enable the logging.
     */
	public void startLogging();

    /**
     * Disable the logging.
     */
	public void stopLogging();
}