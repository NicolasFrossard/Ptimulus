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

package com.ptimulus;

import java.util.Timer;
import java.util.TimerTask;

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
import android.widget.LinearLayout;
import android.widget.TextView;

public class PtimulusActivity extends Activity {

    private PtimulusService service = null;

	private TextView gpsTextView;
	private TextView phoneStateTextView;
	private TextView accelStateTextView;
	private TextView magnStateTextView;
    private TextView gyroStateTextView;
    private TextView batStateTextView;
	private TextView logTextView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        int index = 0;
        setContentView(R.layout.main);
        LinearLayout ll = (LinearLayout) findViewById(R.id.mainLayout);
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

        View ruler6 = new View(this);
        ruler6.setBackgroundColor(Color.WHITE);
        ll.addView(ruler6, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 2));
        index++;

        batStateTextView = new TextView(this);
        batStateTextView.setText("No batterie state received yet");
        ll.addView(batStateTextView, index++);
		
		View ruler3 = new View(this); 
		ruler3.setBackgroundColor(Color.WHITE);
		ll.addView(ruler3, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));
		index++;
		
		logTextView = new TextView(this);
		logTextView.setText("No log received yet");
		ll.addView(logTextView, index++);
		
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
        batStateTextView.setText(service.batteryUIData());
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
                unbindService(connection);
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