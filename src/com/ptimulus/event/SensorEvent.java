package com.ptimulus.event;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.ptimulus.PtimulusService;
import com.ptimulus.log.IPtimulusLogger;
import com.ptimulus.log.LogEntryType;

public class SensorEvent implements IEvent {

    private final PtimulusService ptimulusService;
	private final SensorManager sensors;
	private final List<SensorEventAdapter> adapterList;
	
	public SensorEvent(PtimulusService ptimulusService, Context ctx) {
        this.ptimulusService = ptimulusService;
		
		// set up sensor listener
		sensors = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
		adapterList = new LinkedList<SensorEventAdapter>();
		for (Sensor s : sensors.getSensorList(Sensor.TYPE_ALL))
			adapterList.add(new SensorEventAdapter(s));
	}

    /**
     * Enable the event source.
     */
    @Override
    public void startListening() {
        for (SensorEventAdapter s : adapterList)
            s.startSensor();
    }

    /**
     * Disable the event source.
     */
    @Override
    public void stopListening() {
        for (SensorEventAdapter s : adapterList)
            s.stopSensor();
    }

    /**
     * Adaptor class to listen to a sensor.
     */
    private class SensorEventAdapter implements SensorEventListener {
		private Sensor s;

		public SensorEventAdapter(Sensor s) {
			this.s = s;
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		public void onSensorChanged(android.hardware.SensorEvent event) {
            ptimulusService.sensorEvent(event);
		}

		public void startSensor() {
			sensors.registerListener(this, s, SensorManager.SENSOR_DELAY_UI);
		}

		public void stopSensor() {
			sensors.unregisterListener(this);
		}
	}
}
