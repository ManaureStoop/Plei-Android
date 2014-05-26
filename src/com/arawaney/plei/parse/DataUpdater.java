
package com.arawaney.plei.parse;

import android.content.Context;

import com.arawaney.plei.listener.ParseListener;

public class DataUpdater {
	
static public void UpdateAllData(ParseListener listener, Context context){
	
	ParseProvider.updateCategories(context, listener);
	ParseProvider.updateCovers(context, listener);
	ParseProvider.updatePleilists(context, listener);
	ParseProvider.updateTracks(context, listener);

}

}
