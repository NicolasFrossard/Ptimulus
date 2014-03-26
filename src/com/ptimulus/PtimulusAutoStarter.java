package com.ptimulus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PtimulusAutoStarter extends BroadcastReceiver {

	@Override
	public void onReceive(final Context ctx, Intent intent) {
		PtimulusService.activateIfNecessary(ctx);
	}
};
