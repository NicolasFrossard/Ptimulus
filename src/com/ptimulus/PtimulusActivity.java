package com.ptimulus;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class PtimulusActivity extends Activity {

    private PtimulusService service = null;

	private TextView gpsTextView;
	private TextView phoneStateTextView;
	private TextView accelStateTextView;
	private TextView magnStateTextView;
    private TextView gyroStateTextView;
	private TextView logTextView;

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
		ruler.setBackgroundColor(Color.WHITE);
		ll.addView(ruler, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));
		index++;
		
		phoneStateTextView = new TextView(this);
		phoneStateTextView.setText("No phone state received yet");
		ll.addView(phoneStateTextView, index++);

		View ruler2 = new View(this); 
		ruler2.setBackgroundColor(Color.WHITE);
		ll.addView(ruler2, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));
		index++;
		
		accelStateTextView = new TextView(this);
		accelStateTextView.setText("No accelerometer state received yet");
		ll.addView(accelStateTextView, index++);
		
		View ruler4 = new View(this); 
		ruler4.setBackgroundColor(Color.WHITE);
		ll.addView(ruler4, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 2));
		index++;
		
		magnStateTextView = new TextView(this);
		magnStateTextView.setText("No magnetometer state received yet");
		ll.addView(magnStateTextView, index++);

        View ruler5 = new View(this);
        ruler5.setBackgroundColor(Color.WHITE);
        ll.addView(ruler5, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 2));
        index++;

        gyroStateTextView = new TextView(this);
        gyroStateTextView.setText("No gyroscope state received yet");
        ll.addView(gyroStateTextView, index++);
		
		final Button button = new Button(this);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
        		PtimulusCamera.takePicture(getApplicationContext());
            }
        });
        button.setText("Let's take a little picture");
        ll.addView(button, index++);
        
		View ruler3 = new View(this); 
		ruler3.setBackgroundColor(Color.WHITE);
		ll.addView(ruler3, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));
		index++;
		
		logTextView = new TextView(this);
		logTextView.setText("No log received yet");
		ll.addView(logTextView, index++);
		
		setContentView(ll);

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateGUI();
                    }
                });
            }
        }, 0, 1000);
	}

    private void updateGUI() {
        if(service == null)
            return;

        gpsTextView.setText(service.locationUIData());
        phoneStateTextView.setText(service.telephonyUIData());
        accelStateTextView.setText(service.accelerometerUIData());
        magnStateTextView.setText(service.magnetometerUIData());
        gyroStateTextView.setText(service.gyroscopeUIData());
        logTextView.setText(service.logUIData());
    }

    @Override
    public void onStart() {
        super.onStart();

        Intent intent = new Intent(this, PtimulusService.class);
        startService(intent);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();

        if(service != null)
            unbindService(connection);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, 0, Menu.NONE, "Settings");
        menu.add(Menu.NONE, 1, Menu.NONE, "Exit the service");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case 0:
                startActivity(new Intent(this, PtimulusPreferenceActivity.class));
                break;
            case 1:
                Intent intent = new Intent(this, PtimulusService.class);
                stopService(intent);
                break;
        }

		return (true);
	}

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PtimulusService.PtimulusServiceBinder binder = (PtimulusService.PtimulusServiceBinder) iBinder;
            service = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            service = null;
        }
    };
}