package com.ptimulus.device;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;

import com.ptimulus.PtimulusActivity;
import com.ptimulus.log.IPtimulusLogger;

public class TelephonyEventHandler {

	private static PtimulusActivity ptimulusActivity;

	List<IPtimulusLogger> loggers;
	
	private TelephonyManager telephonyManager;
	
	public TelephonyEventHandler(Context ctx, List<IPtimulusLogger> loggers) {
		this.loggers = loggers;

		// set up service state listener
		//gsmState = -1;
		telephonyManager = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(new TelephonyStateListener(this),
				PhoneStateListener.LISTEN_SERVICE_STATE);
	}
	
	public static void registerActivity(PtimulusActivity activity) {
		ptimulusActivity = activity;
	}
	

	private class TelephonyStateListener extends PhoneStateListener {
		private TelephonyEventHandler ds;

		public TelephonyStateListener(TelephonyEventHandler ds) {
			this.ds = ds;
		}
		@Override
		public void onServiceStateChanged(ServiceState serviceState) {
			
			if(ptimulusActivity != null) {
				Date d = new Date();
				ptimulusActivity.updatePhoneState(serviceState.toString(), d.getTime());
			}
		}
	}
}
