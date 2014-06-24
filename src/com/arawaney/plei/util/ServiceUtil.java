package com.arawaney.plei.util;

import java.util.Iterator;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;

public class ServiceUtil {
	
	public static boolean isServiceRunning(String serviceName ,Context context){
        boolean serviceRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> l = am.getRunningServices(50);
        Iterator<ActivityManager.RunningServiceInfo> i = l.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningServiceInfo runningServiceInfo = (ActivityManager.RunningServiceInfo) i
                    .next();

            if(runningServiceInfo.service.getClassName().equals(serviceName)){
                serviceRunning = true;
            }
        }
        return serviceRunning;
    }
	

}
