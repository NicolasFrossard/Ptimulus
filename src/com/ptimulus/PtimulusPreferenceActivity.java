package com.ptimulus;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class PtimulusPreferenceActivity extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.ptimulusprefs);
	}
}
