package com.ptimulus.event;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import com.ptimulus.PtimulusService;

public class BatteryEvent extends BroadcastReceiver implements IEvent<BatteryEvent.BatteryState> {

    private final PtimulusService ptimulusService;
    private final Context ctx;

    private final Object lock = new Object();

    private BatteryState lastSensorEvent;
    private long lastSensorEventTime;

    public class BatteryState {
        public float temp = Float.NaN;
        public float voltage = Float.NaN;
        public float percent = Float.NaN;
    }

	public BatteryEvent(PtimulusService ptimulusService, Context ctx) {
        this.ptimulusService = ptimulusService;
        this.ctx = ctx;

        this.lastSensorEventTime = 0;
        this.lastSensorEvent = new BatteryState();
	}

    /**
     * Enable the event source.
     */
    @Override
    public void startListening() {
        ctx.registerReceiver(this, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    /**
     * Disable the event source.
     */
    @Override
    public void stopListening() {
        ctx.unregisterReceiver(this);
    }

   /**
     * Timer tick from the service. Assumed to be 1Hz.
     */
    @Override
    public void tick(int counter) {
    }

    @Override
    public long dataAge() {
    	return System.currentTimeMillis() - lastSensorEventTime;
    }

    /**
     * The last know measure.
     *
     * @return
     */
    @Override
    public BatteryState data() {
        return lastSensorEvent;
    }

    /**
     * Tell if we have a valid data already;
     *
     * @return
     */
    @Override
    public boolean hasData() {
        return lastSensorEventTime > 0;
    }

    @Override
    public String toString() {
        if(!hasData())
            return "No battery event yet";

    	synchronized (lock) {
            return String.format("%d sec | temp %.2f°C volt %.2fV char %.2f%%",
                    Math.round(dataAge() / 1000f),
                    lastSensorEvent.temp,
                    lastSensorEvent.voltage,
                    lastSensorEvent.percent);
		}
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        synchronized (lock) {
            lastSensorEventTime = System.currentTimeMillis();

            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale   = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            lastSensorEvent.percent = (level*100)/scale;

            int temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            lastSensorEvent.temp = temp / 10f;

            int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
            lastSensorEvent.voltage = voltage / 1000f;
        }

        ptimulusService.batteryEvent(lastSensorEvent);
    }
}
