package com.ptimulus.event;

/**
 * Interface of a event source class. Allow to enable and disable the event source.
 */
public interface IEvent<DataType> {

    /**
     * Enable the event source.
     */
    public void startListening();

    /**
     * Disable the event source.
     */
    public void stopListening();

    /**
     * Timer tick from the service. Assumed to be 1Hz.
     */
    public void tick();

    /**
     * Age of the last measure, in milliseconds.
     * @return
     */
    public long dataAge();

    /**
     * The last know measure.
     * @return
     */
    public DataType data();

    /**
     * Tell if we have a valid data already;
     * @return
     */
    public boolean hasData();
}
