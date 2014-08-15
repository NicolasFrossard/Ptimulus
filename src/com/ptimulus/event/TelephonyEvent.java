package com.ptimulus.event;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;

import com.ptimulus.PtimulusService;

public class TelephonyEvent implements IEvent {

    private final PtimulusService ptimulusService;
    private boolean listening;

    public TelephonyEvent(PtimulusService ptimulusService, Context ctx) {
        this.ptimulusService = ptimulusService;
        this.listening = false;

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
     * Adaptor class to listen to the telephony states.
     */
	private class TelephonyStateAdaptor extends PhoneStateListener {

		@Override
		public void onServiceStateChanged(ServiceState serviceState) {
            if(listening)
                ptimulusService.telephonyEvent(serviceState);
		}
	}
}
