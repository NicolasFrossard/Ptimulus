package com.ptimulus.event;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.ptimulus.PtimulusActivity;
import com.ptimulus.log.IPtimulusLogger;
import com.ptimulus.log.LogEntryType;

public class SensorEventHandler {

	private static PtimulusActivity ptimulusActivity;

	private final List<IPtimulusLogger> loggers;

	private SensorManager sensors;
	private List<SensorEventAdapter> adapterList;
	
	public SensorEventHandler(Context ctx, List<IPtimulusLogger> loggers) {
		this.loggers = loggers;
		
		// set up sensor listener
		sensors = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
		adapterList = new LinkedList<SensorEventAdapter>();
		for (Sensor s : sensors.getSensorList(Sensor.TYPE_ALL))
			adapterList.add(new SensorEventAdapter(s));
	}

	public static void registerActivity(PtimulusActivity activity) {
		ptimulusActivity = activity;
	}

	public void start() {
		for (SensorEventAdapter s : adapterList)
			s.startSensor();
	}

	public void stop() {
		for (SensorEventAdapter s : adapterList)
			s.stopSensor();
	}
	
	private class SensorEventAdapter implements SensorEventListener {
		private Sensor s;

		public SensorEventAdapter(Sensor s) {
			this.s = s;
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		public void onSensorChanged(SensorEvent event) {
            StringBuilder data = new StringBuilder();

			for (float f : event.values) {
                data.append(" ");
                data.append(f);
            }

			for (IPtimulusLogger listener : loggers)
				listener.logDataEvent(LogEntryType.SENSOR, data.toString());
			
			if(ptimulusActivity != null) {
				ptimulusActivity.updateSensorState(data.toString());
			}
		}

		public void startSensor() {
			sensors.registerListener(this, s, SensorManager.SENSOR_DELAY_UI);
		}

		public void stopSensor() {
			sensors.unregisterListener(this);
		}
	}
}
