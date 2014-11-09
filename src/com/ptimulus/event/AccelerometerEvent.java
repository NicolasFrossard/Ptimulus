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

package com.ptimulus.event;

import java.util.Locale;

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
        return String.format(Locale.US, "%d sec | %.3f G",
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
