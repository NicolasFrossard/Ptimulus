package com.ptimulus;

import java.util.HashMap;

import com.ptimulus.device.LocationEventHandler;
import com.ptimulus.log.IPtimulusLogger;
import com.ptimulus.log.LogEntryType;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PtimulusActivity extends Activity {

	private TextView tv;
	private LinearLayout ll;
	private int index;
	private HashMap<String, TextView> dataViews;

	private boolean logging;
	
	public void updateLocation(String newLocation, long timestamp) {
		if(!logging) return;
		
		tv.setText("Last GPS received at " + timestamp + ": " + newLocation);
		
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

	public PtimulusApplication getIcarusApplication() {
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

		tv = new TextView(this);
		tv.setText("No GPS fix received yet");
		ll.addView(tv, index++);

		setContentView(ll);

		dataViews = new HashMap<String, TextView>();
		
		LocationEventHandler.registerActivity(this);

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