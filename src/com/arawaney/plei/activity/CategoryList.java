package com.arawaney.plei.activity;

import java.util.ArrayList;

import com.arawaney.plei.MainActivity;
import com.arawaney.plei.R;
import com.arawaney.plei.adapter.AnimationLeftToRightAdapter;
import com.arawaney.plei.adapter.PleiListAdapter;
import com.arawaney.plei.db.provider.CategoryProvider;
import com.arawaney.plei.db.provider.PleilistProvider;
import com.arawaney.plei.db.provider.TrackProvider;
import com.arawaney.plei.listener.ParseListener;
import com.arawaney.plei.model.Category;
import com.arawaney.plei.model.Cover;
import com.arawaney.plei.model.Pleilist;
import com.arawaney.plei.model.Track;
import com.arawaney.plei.parse.ParseProvider;
import com.arawaney.plei.service.StreamPlayer;
import com.arawaney.plei.util.FontUtil;
import com.arawaney.plei.util.ServiceUtil;
import com.nhaarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingLeftInAnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingRightInAnimationAdapter;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path.FillType;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CategoryList extends Activity implements ParseListener  {

	private final String LOG_TAG = "Pleilist-PleiListActivity";

	ListView list;
	PleiListAdapter adapter;
	ArrayList<Pleilist> pleiLists;
	SwingRightInAnimationAdapter swingAnimationAdapter;
	Category category;
	LinearLayout playButton;
	LinearLayout backButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plei_list);

		getCategory();

		setActionBar();

		loadList();

		loadViews();

		refreshList();
		
		checkIfMusicPLaying();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		checkIfMusicPLaying();
	}

	private void setActionBar() {
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActionBar().setCustomView(R.layout.actionbar_category_view);

		backButton = (LinearLayout) findViewById(R.id.layout_actionBar_back);
		playButton = (LinearLayout) findViewById(R.id.layout_actionBar_play);
		TextView categoryTitle = (TextView) findViewById(R.id.textView_actionBar_title);
		
		categoryTitle.setTypeface(FontUtil.getTypeface(this, FontUtil.HELVETICA_NEUE_LIGHT));

		categoryTitle.setText(category.getName().toString());
		
		playButton.setVisibility(View.INVISIBLE);

		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				finish();
			}
		});

		playButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						TrackActivity.class);
				i.putExtra(TrackActivity.TAG_CALL_MODE,
						TrackActivity.MODE_OPEN_PLAYING_PLEILIST);
				startActivity(i);
			}
		});

	}
	
	
	private void checkIfMusicPLaying() {
		if (ServiceUtil.isServiceRunning(StreamPlayer.class.getName(),
				this)) {
			playButton.setVisibility(View.VISIBLE);
		} else
			playButton.setVisibility(View.INVISIBLE);
	}

	private void getCategory() {
		String categoryId = getIntent().getStringExtra(
				MainActivity.TAG_CATEGORY_ID).toString();
		if (categoryId != null) {
			category = CategoryProvider.readCategory(this, categoryId);
		}
	}

	private void refreshList() {
		if (pleiLists != null) {
			adapter = new PleiListAdapter(this, pleiLists);
//			swingAnimationAdapter = new SwingRightInAnimationAdapter(adapter);
//			swingAnimationAdapter.setAbsListView(list);
			list.setAdapter(adapter);	
		}

	}

	private void loadViews() {
		list = (ListView) findViewById(R.id.listView_plei_list);
	}

	private void loadList() {
		
		String categoryId = getIntent().getStringExtra(
				MainActivity.TAG_CATEGORY_ID).toString();
		if (categoryId != null) {
			pleiLists = PleilistProvider.readPleilistsByCategory(this,
					categoryId);
			if (pleiLists == null) {
				Log.d(LOG_TAG,"null pleilist for category : "+category.getName());
				finish();
			}
		}

	}

	@Override
	public void OnLoginResponse(boolean succes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAllCategoriesFinished(Boolean b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAllCoversFinished(Boolean b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAllPleilistsFinished(Boolean b) {
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
			ParseProvider.downloadPleilistCoverImage(pleilist, this, this);
		} else {
			pleilist.setFavorite(Pleilist.NOT_FAVORITE);
			Toast.makeText(this,
					getResources().getString(R.string.toast_favorited_failed),
					Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public void onFavoritesUpdated(boolean succes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFavoritedRemoved(boolean b, Pleilist pleilist) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onImageCoverDownloaded() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onImagePleilistDownloaded() {
		if (adapter!= null ) {
			adapter.notifyDataSetChanged();
		}
		
	}

	@Override
	public void onAllTracksByPLeilistFinished() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onPleilistCoverImageDownloaded(Pleilist pleilist) {
		// TODO Auto-generated method stub
		
	}

}
