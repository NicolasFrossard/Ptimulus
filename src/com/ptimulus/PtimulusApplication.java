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
