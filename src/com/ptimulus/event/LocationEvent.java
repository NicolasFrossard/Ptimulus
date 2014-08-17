package com.ptimulus.event;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.ptimulus.PtimulusService;

/**
 * Retrieve the GPS/Locations event and feed them to the service.
 * @author nicolas
 *
 */
public class LocationEvent implements LocationListener, IEvent {

    private final PtimulusService ptimulusService;
	private final LocationManager gps;
	private final String locationProvider;

    private Location lastLocation;
    private long lastLocationTime;
	
	public LocationEvent(PtimulusService ptimulusService, Context ctx) {
        this.ptimulusService = ptimulusService;
        this.lastLocation = null;
        this.lastLocationTime = 0;

		// set up gps
		gps = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
		Criteria c = new Criteria();
		c.setAccuracy(Criteria.ACCURACY_FINE);
		c.setAltitudeRequired(true);
		c.setSpeedRequired(true);
		locationProvider = gps.getBestProvider(c, true);
	}

    /**
     * Enable the event source.
     */
    @Override
    public void startListening() {
        gps.requestLocationUpdates(locationProvider, 0, 0, this);
    }

    @Override
    public void stopListening() {
        gps.removeUpdates(this);
    }

    /**
     * Timer tick from the service. Assumed to be 1Hz.
     */
    @Override
    public void tick() {
        if(lastLocation != null)
            ptimulusService.locationEvent(lastLocation);
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
        if(lastLocation == null)
            return "No GPS event yet";

        return String.format("%d sec | %s|%s  alt %s",
        		Math.round(dataAge() / 1000f),
        		Location.convert(lastLocation.getLatitude(), Location.FORMAT_MINUTES),
        		Location.convert(lastLocation.getLongitude(), Location.FORMAT_MINUTES),
        		lastLocation.getAltitude());
    }

    public void onLocationChanged(Location l) {
        lastLocation = l;
        lastLocationTime = System.currentTimeMillis();
	}

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {}

    @Override
    public void onProviderDisabled(String s) {}
}