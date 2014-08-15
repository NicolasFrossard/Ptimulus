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
	
	public LocationEvent(PtimulusService ptimulusService, Context ctx) {
        this.ptimulusService = ptimulusService;

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

	public void onLocationChanged(Location l) {
		ptimulusService.locationEvent(l);
	}

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {}

    @Override
    public void onProviderDisabled(String s) {}
}