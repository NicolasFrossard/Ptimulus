package com.ptimulus.event;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.ptimulus.PtimulusService;

public class AccelerometerEvent implements SensorEventListener, IEvent<SensorEvent> {

    private final PtimulusService ptimulusService;
	private final SensorManager sensorManager;
    private final Sensor accelerometer;
    
    private final Object lock = new Object();
    
	private SensorEvent lastSensorEvent;
    private long lastSensorEventTime;

	public AccelerometerEvent(PtimulusService ptimulusService, Context ctx) {
        this.ptimulusService = ptimulusService;
        this.lastSensorEvent = null;
        this.lastSensorEventTime = 0;
		
		// Find the accelerometer
		sensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	}

    /**
     * Enable the event source.
     */
    @Override
    public void startListening() {
        boolean accelSupported = sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        if (!accelSupported) {
            sensorManager.unregisterListener(this, accelerometer);
        }
    }

    /**
     * Disable the event source.
     */
    @Override
    public void stopListening() {
        sensorManager.unregisterListener(this);
    }

   /**
     * Timer tick from the service. Assumed to be 1Hz.
     */
    @Override
    public void tick(int counter) {
    	synchronized (lock) {
    		if(lastSensorEvent != null)
                ptimulusService.accelerometerEvent(lastSensorEvent);
		}
    }

    @Override
    public long dataAge() {
    	return System.currentTimeMillis() - lastSensorEventTime;
    }

    /**
     * The last know measure.
     *
     * @return
     */
    @Override
    public SensorEvent data() {
        return lastSensorEvent;
    }

    /**
     * Tell if we have a valid data already;
     *
     * @return
     */
    @Override
    public boolean hasData() {
        return lastSensorEvent != null;
    }

    @Override
    public String toString() {
    	float x,y,z;
    	
    	synchronized (lock) {
            if(lastSensorEvent == null)
                return "No accelerometer event yet";
            
            x = lastSensorEvent.values[0];
            y = lastSensorEvent.values[1];
            z = lastSensorEvent.values[2];
		}
       
        double magn = Math.sqrt(x*x + y*y + z*z) / 9.81d; 
        return String.format("%d sec | %.3f G",
        		Math.round(dataAge() / 1000f),
        		magn);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
    	synchronized (lock) {
    		lastSensorEventTime = System.currentTimeMillis();
            lastSensorEvent = sensorEvent;
		}
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
