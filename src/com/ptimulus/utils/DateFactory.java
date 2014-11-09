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

package com.ptimulus.utils;

import java.util.Date;

import android.text.format.DateFormat;

/**
 * 
 * @author nicolas
 *
 */
public abstract class DateFactory {

	public static String format(long time) {
		return DateFormat.format("yyyy-MM-dd kk:mm:ss", time).toString();
	}

    public static String nowForFilename() {
        return DateFormat.format("yyyy-MM-dd_kk-mm-ss", new Date().getTime()).toString();
    }
    
	public static String nowAsString() {
		return format(nowAsLong());	
	}
	
	public static Long nowAsLong() {
		return new Date().getTime();		
	}
}
