package com.arawaney.plei.util;

import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;

public class FontUtil {
	
	public static String HELVETICA_NEUE_LIGHT = "fonts/HelveticaNeue-Light.otf";

	
	public static Typeface getTypeface(Context context, String template) {
	     Typeface tf = Typeface.createFromAsset(context.getAssets(),
	            template);
	     return tf;
	}
}
