package com.ptimulus;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ptimulus.utils.DateFactory;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

public class PtimulusActivity extends Activity {

    private PtimulusService service = null;

	private TextView gpsTextView;
	private TextView phoneStateTextView;
	private TextView sensorStateTextView;
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
		ruler.setBackgroundColor(0xFFFFFFFF);
		
		ll.addView(ruler, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 2));
		index++;
		
		phoneStateTextView = new TextView(this);
		phoneStateTextView.setText("No phone state received yet");
		ll.addView(phoneStateTextView, index++);

		View ruler2 = new View(this); 
		ruler2.setBackgroundColor(0xFFFFFFFF);
		
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
        
		View ruler3 = new View(this); 
		ruler3.setBackgroundColor(0xFFFFFFFF);
		
		ll.addView(ruler3, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 2));
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

        gpsTextView.setText(service.locationUIdata());
        phoneStateTextView.setText(service.telephonyUIdata());
        sensorStateTextView.setText(service.accelerometerUIdata());
        logTextView.setText(service.logUIData());
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