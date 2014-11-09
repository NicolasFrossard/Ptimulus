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

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.ptimulus.PtimulusService;
import com.ptimulus.log.LogEntryType;

import java.util.Locale;

/**
 * Retrieve the GPS/Locations event and feed them to the service.
 * @author nicolas
 *
 */
    public class LocationEvent implements LocationListener, IEvent<Location> {

    private final PtimulusService ptimulusService;
    private final LocationManager locationManager;
	
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
    public void tick(int counter) {
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

    /**
     * The last know measure.
     *
     * @return
     */
    @Override
    public Location data() {
        return lastLocation;
    }

    /**
     * Tell if we have a valid data already;
     *
     * @return
     */
    @Override
    public boolean hasData() {
        return lastLocation != null;
    }

    @Override
    public String toString() {
    	synchronized (lock) {
    		if(lastLocation == null)
                return "No GPS event yet";

            return String.format(
                    Locale.US,
                    "%d sec | %s|%s  alt %.1f %s",
                    Math.round(dataAge() / 1000f),
                    Location.convert(lastLocation.getLatitude(),
                            Location.FORMAT_MINUTES),
                    Location.convert(lastLocation.getLongitude(),
                            Location.FORMAT_MINUTES),
                    lastLocation.getAltitude(),
                    lastLocation.hasAccuracy() ? String.format(Locale.US,
                            "acc %.1f", lastLocation.getAccuracy()) : "");
    	}
    }

    public String toStringSMS() {
        return String.format(Locale.US, "%s\nhttp://maps.google.com/?q=%.6f,%.6f",
                toString(),
                lastLocation.getLatitude(),
                lastLocation.getLongitude());
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