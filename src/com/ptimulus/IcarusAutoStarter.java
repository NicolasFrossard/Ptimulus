package com.ptimulus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class IcarusAutoStarter extends BroadcastReceiver {

	@Override
	public void onReceive(final Context ctx, Intent intent) {
		IcarusService.activateIfNecessary(ctx);
	}
};
