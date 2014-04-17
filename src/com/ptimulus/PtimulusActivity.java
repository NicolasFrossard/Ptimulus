package com.ptimulus;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ptimulus.device.LocationEventHandler;
import com.ptimulus.device.TelephonyEventHandler;
import com.ptimulus.utils.DateFactory;

public class PtimulusActivity extends Activity {

	private TextView gpsTextView;
	private TextView phoneStateTextView;
	
	private LinearLayout ll;
	private int index;

	private boolean logging;
	
	public void updateLocation(String newLocation) {
		if(!logging) return;		
		gpsTextView.setText("Last GPS received at " + DateFactory.nowAsString() + ": " + newLocation);		
	}

	public void updatePhoneState(String newState) {
		if(!logging) return;		
		phoneStateTextView.setText("State received at " + DateFactory.nowAsString() + ": " + newState);
	}
	
	/*
	public void logDataEvent(LogEntryType type, String data, long timestamp) {
		
		if(!logging) return;
		
		if (timestamp > 0)
			tv.setText("GPS Timestamp: " + timestamp + " Cell service: ");
					+ (hasService ? "yes" : "no"));
		TextView t;
		if (!dataViews.containsKey(type.toString())) {
			t = new TextView(this);
			ll.addView(t, index++);
			dataViews.put(type.toString(), t);
		} else
			t = dataViews.get(type.toString());
		t.setText(type + " " + data);
	}*/

	public PtimulusApplication getPtimulusApplication() {
		return (PtimulusApplication) getApplicationContext();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// camera = Camera.open();
		// camera.setPreviewCallback(this);

		index = 0;
		ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);

		gpsTextView = new TextView(this);
		gpsTextView.setText("No GPS fix received yet");
		ll.addView(gpsTextView, index++);
		
		View ruler = new View(this); 
		ruler.setBackgroundColor(0xFFFFFFFF);
		
		ll.addView(ruler, new ViewGroup.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, 2));
		index++;
		
		phoneStateTextView = new TextView(this);
		phoneStateTextView.setText("No phone state received yet");
		ll.addView(phoneStateTextView, index++);

		setContentView(ll);
		
		LocationEventHandler.registerActivity(this);
		TelephonyEventHandler.registerActivity(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		logging = false;
	}

	@Override
	public void onResume() {
		super.onResume();
		logging = true;
		PtimulusService.activateIfNecessary(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 0, 0, "Settings");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		startActivity(new Intent(this, PtimulusPreferenceActivity.class));
		return (true);
	}

}