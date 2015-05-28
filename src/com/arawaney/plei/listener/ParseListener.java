package com.arawaney.plei.listener;

import java.util.ArrayList;
import java.util.List;

import com.arawaney.plei.model.Category;
import com.arawaney.plei.model.Cover;
import com.arawaney.plei.model.Pleilist;
import com.arawaney.plei.model.Track;
import com.parse.ParseObject;



public interface ParseListener {
	
	public void OnLoginResponse(boolean succes);

	public void onAllCategoriesFinished(Boolean b);

	public void onAllCoversFinished(Boolean b);

	public void onAllPleilistsFinished(Boolean b);

	public void onAllTracksFinished(ArrayList<Track> tracks);
	
	public void onSavedFAvoriteDone(boolean succes, Pleilist pleilist);

	public void onFavoritesUpdated(boolean succes);

	public void onFavoritedRemoved(boolean b, Pleilist pleilist);

	public void onImageCoverDownloaded();

	public void onImagePleilistDownloaded();

	public void onAllTracksByPLeilistFinished();

	public void onPleilistCoverImageDownloaded(Pleilist pleilist);
	

}



