package com.ptimulus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ptimulus.event.LocationEventHandler;
import com.ptimulus.event.TelephonyEventHandler;
import com.ptimulus.utils.DateFactory;

public class PtimulusActivity extends Activity {

	private TextView gpsTextView;
	private TextView phoneStateTextView;
	private TextView sensorStateTextView;
	private int index;

	private boolean logging;
	
	public void updateLocation(String newLocation) {
		if(!logging)
			return;
		
		gpsTextView.setText("2 Last GPS received at " + DateFactory.nowAsString() + ": " + newLocation);		
	}

	public void updatePhoneState(String newState) {
		if(!logging)
			return;
		
		phoneStateTextView.setText("State received at " + DateFactory.nowAsString() + ": " + newState);
	}

	public void updateSensorState(String newSensorState) {
        if (!logging)
            return;

        phoneStateTextView.setText("Sensor state received at " + DateFactory.nowAsString() + ": " + newSensorState);
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        int index = 0;
        LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
       
        
		gpsTextView = new TextView(this);
		gpsTextView.setText("No GPS fix received yet");
		ll.addView(gpsTextView, index++);
		
		View ruler = new View(this); 
		ruler.setBackgroundColor(0xFFFFFFFF);
		
		View ruler2 = new View(this); 
		ruler2.setBackgroundColor(0xFFFFFFFF);
		
		ll.addView(ruler, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 2));
		index++;
		
		phoneStateTextView = new TextView(this);
		phoneStateTextView.setText("No phone state received yet");
		ll.addView(phoneStateTextView, index++);

		ll.addView(ruler2, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 2));
		index++;
		
		sensorStateTextView = new TextView(this);
		sensorStateTextView.setText("No sensor state received yet");
		ll.addView(sensorStateTextView, index++);
		
		final Button button = new Button(this);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
        		PtimulusCamera.takePicture();
            }
        });
        button.setText("Take picture");
        ll.addView(button, index++);
        
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