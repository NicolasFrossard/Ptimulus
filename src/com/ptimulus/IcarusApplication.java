package com.ptimulus;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class IcarusApplication extends android.app.Application {
	private DataSource ds;

	SharedPreferences prefs;

	static final int NOTIFY_ICARUS_ACTIVE = 0;

	public DataSource getDataSource() {
		if (ds == null)
			ds = new DataSource(this);
		return ds;
	}

	public SharedPreferences getIcarusPreferences() {
		if (prefs == null)
			prefs = PreferenceManager.getDefaultSharedPreferences(this);
		return prefs;
	}
}
