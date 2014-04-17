package com.ptimulus;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.ptimulus.log.IPtimulusLogger;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * Has a list of loggers and log events for GPS and Sensors
 * @author nicolas
 *
 */
public class DataSource implements LocationListener {
	
	LinkedList<IPtimulusLogger> loggers;

	private class SensorEventAdapter implements SensorEventListener {
		private DataSource ds;
		private Sensor s;

		public SensorEventAdapter(Sensor s, DataSource ds) {
			this.s = s;
			this.ds = ds;
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		public void onSensorChanged(SensorEvent event) {
			String sdata = "";
			for (float f : event.values)
				sdata += " " + f;
			this.ds.logDataEvent(event.sensor.getName(), sdata, 0);
		}

		public void startSensor() {
			sensors.registerListener(this, s, SensorManager.SENSOR_DELAY_UI);
		}

		public void stopSensor() {
			sensors.unregisterListener(this);
		}
	}

	private class StateListener extends PhoneStateListener {
		private DataSource ds;

		public StateListener(DataSource ds) {
			this.ds = ds;
		}

		@Override
		public void onServiceStateChanged(ServiceState serviceState) {
			int newGsmState = serviceState.getState();
			this.ds.gsmState = newGsmState;
			this.ds.logDataEvent("gsmState changed", "" + gsmState, 0);
		}
	}

	public int gsmState;

	private SensorManager sensors;
	private List<SensorEventAdapter> adapterlist;

	private LocationManager gps;
	private TelephonyManager telephonyManager;
	private String locationProvider;

	public void addLogger(IPtimulusLogger logger) {
		loggers.add(logger);
	}

	public void removeLogger(IPtimulusLogger logger) {
		loggers.remove(logger);
	}

	private final Context ctx;
	
	public DataSource(Context ctx) {
		loggers = new LinkedList<IPtimulusLogger>();

		this.ctx = ctx; 
		
		// set up service state listener
		gsmState = -1;
		telephonyManager = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(new StateListener(this),
				PhoneStateListener.LISTEN_SERVICE_STATE);

		// set up sensor listener
		sensors = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
		adapterlist = new LinkedList<SensorEventAdapter>();
		for (Sensor s : sensors.getSensorList(Sensor.TYPE_ALL))
			adapterlist.add(new SensorEventAdapter(s, this));

		// set up gps
		gps = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
		Criteria c = new Criteria();
		c.setAccuracy(Criteria.ACCURACY_FINE);
		c.setAltitudeRequired(true);
		c.setSpeedRequired(true);
		locationProvider = gps.getBestProvider(c, true);

		Toast.makeText(ctx, "DS just created", Toast.LENGTH_LONG).show();
	}

	public void start() {
		gps.requestLocationUpdates(locationProvider, 0, 0, this);
		for (SensorEventAdapter s : adapterlist) 
			s.startSensor();
	}

	public void logDataEvent(String name, String data, long ts) {
		for (IPtimulusLogger listener : loggers)
			listener.logDataEvent(name, data, ts,
					this.gsmState == ServiceState.STATE_IN_SERVICE);
	}

	public void onLocationChanged(Location l) {		
		Date d = new Date(l.getTime());
		
		logDataEvent("gps", l.getLatitude() + "," + l.getLongitude() + " "
				+ l.getAltitude(), l.getTime());
	}

	public void onProviderDisabled(String providezr) {
	}

	public void onProviderEnabled(String providezr) {
	}

	public void onStatusChanged(String providezr, int status, Bundle extras) {
	}

	public void stop() {
		gps.removeUpdates(this);
		for (SensorEventAdapter s : adapterlist)
			s.stopSensor();
	}
}