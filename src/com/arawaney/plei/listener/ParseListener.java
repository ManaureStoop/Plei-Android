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

	public void onAllCategoriesFinished(ArrayList<Category> categories);

	public void onAllCoversFinished(ArrayList<Cover> covers);

	public void onAllPleilistsFinished(ArrayList<Pleilist> pleilists);

	public void onAllTracksFinished(ArrayList<Track> tracks);
	

}



