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
		return DateFormat.format("yyyy-MM-dd HH:mm:ss", time).toString();
	}

    public static String nowForFilename() {
        return DateFormat.format("yyyy-MM-dd_HH-mm", new Date().getTime()).toString();
    }
	
    public static String nowForPhotoFilename() {
        return DateFormat.format("yyyy-MM-dd_HH-mm-ss", new Date().getTime()).toString();
    }
    
	public static String nowAsString() {
		return format(nowAsLong());	
	}
	
	public static Long nowAsLong() {
		return new Date().getTime();		
	}
}
