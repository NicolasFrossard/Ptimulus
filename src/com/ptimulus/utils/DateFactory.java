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
		return DateFormat.format("yyyy-MM-dd hh:mm:ss", time).toString();
	}
	
	public static String nowAsString() {
		return format(nowAsLong());	
	}
	
	public static Long nowAsLong() {
		return new Date().getTime();		
	}
}
