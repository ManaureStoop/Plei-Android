package com.arawaney.plei.activity;

import java.io.File;
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arawaney.plei.MainActivity;
import com.arawaney.plei.R;
import com.arawaney.plei.db.provider.CategoryProvider;
import com.arawaney.plei.db.provider.PleilistProvider;
import com.arawaney.plei.db.provider.TrackProvider;
import com.arawaney.plei.listener.ParseListener;
import com.arawaney.plei.model.Category;
import com.arawaney.plei.model.Cover;
import com.arawaney.plei.model.Pleilist;
import com.arawaney.plei.model.Track;
import com.arawaney.plei.parse.ParseProvider;
import com.arawaney.plei.util.FileUtil;
import com.arawaney.plei.util.FontUtil;
import com.nhaarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter;

public class TrackActivity extends Activity implements ParseListener {

	private final String LOG_TAG = "Pleilist-PleiListActivity";

	TextView categoryTitle;
	TextView pleiListTitle;
	TextView trackTitle;
	TextView playedTIme;
	TextView totalTime;
	

	ImageView previewsButton;
	ImageView nextButton;
	ImageView pausePlayButton;
	ImageView favoriteButton;
	
	ImageView pleiListImage;

	int trackIndex;
	ProgressBar trackProgress;

	ArrayList<Track> tracks;
	AlphaInAnimationAdapter swingAnimationAdapter;
	Category category;
	Pleilist pleilist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track_play);

		getCategory();
		getPleilist();

		setActionBar();

		loadTrackList();

		loadViews();

		loadTitles();

		loadButtons();

	}

	private void loadButtons() {
		
		previewsButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				trackIndex--;
				if (trackIndex < 0 ) {
					trackIndex = tracks.size()-1;	
				}
				setTrackTitle();
			}
		});
		
		
		nextButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				trackIndex++;
				if (trackIndex == tracks.size()  ) {
					trackIndex = 0;
				}
				setTrackTitle();
			}
		});
		
		favoriteButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (pleilist.getFavorite()== Pleilist.NOT_FAVORITE) {
					favoriteButton.setImageDrawable(getResources().getDrawable(R.drawable.track_play_like_on));
					pleilist.setFavorite(Pleilist.FAVORITE);
					ParseProvider.insertFavoritePleilist(pleilist,TrackActivity.this, TrackActivity.this);
				}else {
					favoriteButton.setImageDrawable(getResources().getDrawable(R.drawable.track_play_like_off));
					pleilist.setFavorite(Pleilist.NOT_FAVORITE);
					ParseProvider.removeFavoritePleilist(TrackActivity.this, TrackActivity.this, pleilist);
				}
				
				
				
			}
		});

	}

	private void loadTitles() {
		if (pleilist != null) {
			if (pleilist.getName() != null) {
				pleiListTitle.setText(pleilist.getName().toString());
			}
		}
		if (category != null) {
			if (category.getName() != null) {
				categoryTitle.setText(category.getName().toString());
			}
		}
		trackIndex = 0;
		setTrackTitle();
		
		setFonts();

	}
	
	private void setFonts() {
		pleiListTitle.setTypeface(FontUtil.getTypeface(this,
				FontUtil.HELVETICA_NEUE_LIGHT));
		categoryTitle.setTypeface(FontUtil.getTypeface(this,
				FontUtil.HELVETICA_NEUE_LIGHT));
		trackTitle.setTypeface(FontUtil.getTypeface(this,
				FontUtil.HELVETICA_NEUE_LIGHT));
	}

	private void setTrackTitle() {
		if (tracks != null) {
			trackTitle.setText(tracks.get(trackIndex).getName().toString());
		}
	}

	private void getPleilist() {
		String pleiListId = getIntent().getStringExtra(
				MainActivity.TAG_PLEILIST_ID);
		if (pleiListId != null) {
			pleilist = PleilistProvider.readPleilist(this, pleiListId);
		}else{
			Log.d(LOG_TAG, "Pleilist Id null");
		}

	}

	private void setActionBar() {
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActionBar().setCustomView(R.layout.actionbar_category_view);
		ImageView backButton = (ImageView) findViewById(R.id.imageView_actionBar_back);
		ImageView playButton = (ImageView) findViewById(R.id.imageView_actionBar_play);
		TextView categoryTitle = (TextView) findViewById(R.id.textView_actionBar_title);

		categoryTitle.setVisibility(View.INVISIBLE);
		playButton.setVisibility(View.INVISIBLE);

		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	private void getCategory() {
		String categoryId = getIntent().getStringExtra(
				MainActivity.TAG_CATEGORY_ID);
		if (categoryId != null) {
			category = CategoryProvider.readCategory(this, categoryId);
		}
	}

	private void loadViews() {
		categoryTitle = (TextView) findViewById(R.id.textView_track_category_name);
		pleiListTitle = (TextView) findViewById(R.id.textView_track_pleilist_title);
		trackTitle = (TextView) findViewById(R.id.TextView_track_name);
		playedTIme = (TextView) findViewById(R.id.textView_track_played_time);
		totalTime = (TextView) findViewById(R.id.textView_track_total_time);

		previewsButton = (ImageView) findViewById(R.id.imageView_track_previews_button);
		nextButton = (ImageView) findViewById(R.id.imageView_track_next_button);
		pausePlayButton = (ImageView) findViewById(R.id.imageView_track_pauseplay_button);
		favoriteButton = (ImageView) findViewById(R.id.imageView_favorite_button);
		
		pleiListImage = (ImageView) findViewById(R.id.imageView_track_pleilist_image);

		trackProgress = (ProgressBar) findViewById(R.id.progressBar_track_play);
		
		if (pleilist.getImage() != null) {
			if (FileUtil.imageExists(pleilist.getImage(), this)) {
				File filePath = getFileStreamPath(pleilist.getImage());
				pleiListImage.setImageDrawable(Drawable
						.createFromPath(filePath.toString()));
			}
		}
		
		if (category == null) {
			categoryTitle.setVisibility(View.GONE);
		}
		
		
		
		if (pleilist.getFavorite()== Pleilist.FAVORITE) {
			favoriteButton.setImageDrawable(getResources().getDrawable(R.drawable.track_play_like_on));
		}else {
			favoriteButton.setImageDrawable(getResources().getDrawable(R.drawable.track_play_like_off));
		}
	}

	private void loadTrackList() {
		if (pleilist != null) {
			tracks = TrackProvider.readTracksByPleiList(this,
					pleilist.getSystem_id());
		}

	}

	@Override
	public void OnLoginResponse(boolean succes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAllCategoriesFinished(ArrayList<Category> categories) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAllCoversFinished(ArrayList<Cover> covers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAllPleilistsFinished(ArrayList<Pleilist> pleilists) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAllTracksFinished(ArrayList<Track> tracks) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSavedFAvoriteDone(boolean succes, Pleilist pleilist) {
		if (succes) {
			PleilistProvider.updatePleilist(this, pleilist);
		}
		else{
			pleilist.setFavorite(Pleilist.NOT_FAVORITE);
			favoriteButton.setImageDrawable(getResources().getDrawable(R.drawable.track_play_like_off));
			Toast.makeText(this,getResources().getString(R.string.toast_favorited_failed), Toast.LENGTH_LONG).show();
		}
			
	}

	@Override
	public void onFavoritesUpdated(boolean b) {
		
	}

	@Override
	public void onFavoritedRemoved(boolean succes, Pleilist pleilist) {
		if (succes) {
			PleilistProvider.updatePleilist(this, pleilist);
		}
		else{
			pleilist.setFavorite(Pleilist.FAVORITE
				);
			favoriteButton.setImageDrawable(getResources().getDrawable(R.drawable.track_play_like_on));
			Toast.makeText(this,getResources().getString(R.string.toast_favorited_failed), Toast.LENGTH_LONG).show();
		}
		
	}

}
