package com.arawaney.plei.util;

import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;

public class FontUtil {
	
	public static String ROBOTO_LIGHT = "font/Roboto-Light.ttf";
	public static String ROBOTO_REGULAR = "font/Roboto-Regular.ttf";
	public static String ROBOTO_THIN = "font/Roboto-Thin.ttf";
	public static String ROBOTO_FUTURA= "font/Futura.ttc";
	
	public static Typeface getTypeface(Context context, String template) {
	     Typeface tf = Typeface.createFromAsset(context.getAssets(),
	            template);
	     return tf;
	}
}
