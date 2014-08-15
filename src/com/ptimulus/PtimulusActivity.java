package com.ptimulus;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ptimulus.utils.DateFactory;

public class PtimulusActivity extends Activity {

    private PtimulusService service = null;

	private TextView gpsTextView;
	private TextView phoneStateTextView;
	private TextView sensorStateTextView;

	public void updateLocation(String newLocation) {
        gpsTextView.setText("Last GPS received at " + DateFactory.nowAsString() + ": " + newLocation);
	}

	public void updatePhoneState(String newState) {
		phoneStateTextView.setText("State received at " + DateFactory.nowAsString() + ": " + newState);
	}

	public void updateSensorState(String newSensorState) {
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
        		PtimulusCamera.takePicture(getApplicationContext());
            }
        });
        button.setText("Let's take a little picture");
        ll.addView(button, index++);
        
		setContentView(ll);
	}

    @Override
    public void onStart() {
        super.onStart();

        Intent intent = new Intent(this, PtimulusService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();

        if(service != null)
            unbindService(connection);
    }

	@Override
	public void onResume() {
		super.onResume();
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

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PtimulusService.PtimulusServiceBinder binder = (PtimulusService.PtimulusServiceBinder) iBinder;
            service = binder.getService();
            binder.registerActivity(PtimulusActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            service = null;
        }
    };
}