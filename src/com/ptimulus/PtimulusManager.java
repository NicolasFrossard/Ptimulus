package com.ptimulus;

import java.util.HashMap;

import com.ptimulus.log.IPtimulusLogger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PtimulusManager extends Activity implements
		IPtimulusLogger {

	private TextView tv;
	private LinearLayout ll;
	private int index;
	private HashMap<String, TextView> dataViews;

	public void logDataEvent(String name, String data, long ts,
			boolean hasService) {
		if (ts > 0)
			tv.setText("GPS Timestamp: " + ts + " Cell service: "
					+ (hasService ? "yes" : "no"));
		TextView t;
		if (!dataViews.containsKey(name)) {
			t = new TextView(this);
			ll.addView(t, index++);
			dataViews.put(name, t);
		} else
			t = dataViews.get(name);
		t.setText(name + " " + data);
	}

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

	}

	@Override
	public void onPause() {
		super.onPause();
		getIcarusApplication().getDataSource().removeDataListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		getIcarusApplication().getDataSource().addDataListener(this);
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

	@Override
	public void startLogging() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopLogging() {
		// TODO Auto-generated method stub
		
	}

}