package com.arawaney.plei.listener;

import java.util.ArrayList;
import java.util.List;

import com.arawaney.plei.model.Category;
import com.arawaney.plei.model.Cover;
import com.arawaney.plei.model.Pleilist;
import com.arawaney.plei.model.Track;
import com.parse.ParseObject;



public interface MediaPlayerListener {
	
	public void OnMediaPlayerBufferUpdate( int position, int timePlayed, int bufferPosition);
	public void OnGotFile( int duration);
	public void OnSongReady( );
	
	

}



