package com.ptimulus.device;

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
	private List<SensorEventAdapter> adapterlist;
	
	public SensorEventHandler(Context ctx, List<IPtimulusLogger> loggers) {
		this.loggers = loggers;
		
		// set up sensor listener
		sensors = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
		adapterlist = new LinkedList<SensorEventAdapter>();
		for (Sensor s : sensors.getSensorList(Sensor.TYPE_ALL))
			adapterlist.add(new SensorEventAdapter(s));

	}
	
	public static void registerActivity(PtimulusActivity activity) {
		ptimulusActivity = activity;
	}
	
	public void start() {
		for (SensorEventAdapter s : adapterlist) 
			s.startSensor();
	}


	public void stop() {
		for (SensorEventAdapter s : adapterlist)
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
			String sdata = "";
			for (float f : event.values)
				sdata += " " + f;

			for (IPtimulusLogger listener : loggers)
				listener.logDataEvent(LogEntryType.SENSOR, sdata);
			
			if(ptimulusActivity != null) {
				ptimulusActivity.updateSensorState(sdata);
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
