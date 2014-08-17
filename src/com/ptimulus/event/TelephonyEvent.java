package com.ptimulus.event;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;

import com.ptimulus.PtimulusService;

public class TelephonyEvent implements IEvent {

    private final PtimulusService ptimulusService;

    private boolean listening;
    private ServiceState lastServiceState;
    private long lastSetviceStateTime;

    public TelephonyEvent(PtimulusService ptimulusService, Context ctx) {
        this.ptimulusService = ptimulusService;
        this.listening = false;
        this.lastServiceState = null;
        this.lastSetviceStateTime = 0;

        TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(new TelephonyStateAdaptor(), PhoneStateListener.LISTEN_SERVICE_STATE);
	}

    /**
     * Enable the event source.
     */
    @Override
    public void startListening() {
        listening = true;
    }

    /**
     * Disable the event source.
     */
    @Override
    public void stopListening() {
        listening = false;
    }

    /**
     * Timer tick from the service. Assumed to be 1Hz.
     */
    @Override
    public void tick() {}

    /**
     * Age of the last measure, in milliseconds.
     *
     * @return
     */
    @Override
    public long dataAge() {
        return System.currentTimeMillis() - lastSetviceStateTime;
    }

    @Override
    public String toString() {
        if(lastServiceState == null)
            return "No Telephony event yet";

        return String.format("%d sec | %s", Math.round(dataAge() / 1000f), lastServiceState.toString());
    }

    /**
     * Adaptor class to listen to the telephony states.
     */
	private class TelephonyStateAdaptor extends PhoneStateListener {

		@Override
		public void onServiceStateChanged(ServiceState serviceState) {
			lastServiceState = serviceState;
            if(listening)
                ptimulusService.telephonyEvent(serviceState);
		}
	}
}
