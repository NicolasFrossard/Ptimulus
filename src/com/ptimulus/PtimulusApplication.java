package com.ptimulus;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PtimulusApplication extends android.app.Application {

	SharedPreferences prefs;

	static final int NOTIFY_PTIMULUS_ACTIVE = 0;

	public SharedPreferences getPtimulusPreferences() {
		if (prefs == null)
			prefs = PreferenceManager.getDefaultSharedPreferences(this);
		return prefs;
	}
}
