
package com.arawaney.plei.parse;

import android.content.Context;

import com.arawaney.plei.listener.ParseListener;

public class DataUpdater {
	
static public void UpdateAllData(ParseListener listener, Context context){
	
	
	ParseProvider.updateCovers(context, listener);
	ParseProvider.updateCategories(context, listener);
//	ParseProvider.updatePleilists(context, listener);
//	ParseProvider.updateTracks(context, listener);
}

static public void UpdateFavorites(ParseListener listener, Context context){

	ParseProvider.updateFavorites(context, listener);

}

}
