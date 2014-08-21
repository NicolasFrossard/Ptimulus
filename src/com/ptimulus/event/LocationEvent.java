package com.ptimulus.event;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.ptimulus.PtimulusService;
import com.ptimulus.log.LogEntryType;

/**
 * Retrieve the GPS/Locations event and feed them to the service.
 * @author nicolas
 *
 */
public class LocationEvent implements LocationListener, IEvent {

    private final PtimulusService ptimulusService;
	private final LocationManager locationManager;
	//private final String locationProvider;
	
	private final Object lock = new Object();

    private Location lastLocation;
    private long lastLocationTime;
	
	public LocationEvent(PtimulusService ptimulusService, Context ctx) {
        this.ptimulusService = ptimulusService;
        this.lastLocation = null;
        this.lastLocationTime = 0;

		locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
	}

    /**
     * Enable the event source.
     */
    @Override
    public void startListening() {
    	if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
    	{
    		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
    		ptimulusService.relayLog(LogEntryType.GPS, "Started listening GPS provider");
    	}

    	if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    	{
    		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
    		ptimulusService.relayLog(LogEntryType.GPS, "Started listening Network provider");
    	}
    		
    }

    @Override
    public void stopListening() {
        locationManager.removeUpdates(this);
    }

    /**
     * Timer tick from the service. Assumed to be 1Hz.
     */
    @Override
    public void tick() {
    	synchronized (lock) {
    		if(lastLocation != null)
                ptimulusService.locationEvent(lastLocation);
		}
    }

    /**
     * Age of the last measure, in milliseconds.
     *
     * @return
     */
    @Override
    public long dataAge() {
        return System.currentTimeMillis() - lastLocationTime;
    }

    @Override
    public String toString() {
    	synchronized (lock) {
    		if(lastLocation == null)
                return "No GPS event yet";

            return String.format("%d sec | %s|%s  alt %.1f",
            		Math.round(dataAge() / 1000f),
            		Location.convert(lastLocation.getLatitude(), Location.FORMAT_MINUTES),
            		Location.convert(lastLocation.getLongitude(), Location.FORMAT_MINUTES),
            		lastLocation.getAltitude());
    	}
    }

    public void onLocationChanged(Location l) {
    	synchronized (lock) {
    		lastLocation = l;
            lastLocationTime = System.currentTimeMillis();
    	}
	}

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {}

    @Override
    public void onProviderDisabled(String s) {}
}