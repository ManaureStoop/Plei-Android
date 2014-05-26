package com.arawaney.plei.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.net.ConnectivityManager;

public class CalendarUtil {
	
	public static String getDateFormated(Calendar calendar, String template) {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(template,
				Locale.US);
		
		return dateFormat.format(calendar.getTime()).toString();
		
	}
	
public static String getDateFormated(Date date, String template) {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(template,
				Locale.US);
		
		return dateFormat.format(date).toString();
		
	}
}
