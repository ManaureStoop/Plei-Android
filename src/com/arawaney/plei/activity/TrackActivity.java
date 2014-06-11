package com.arawaney.plei.activity;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Currency;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.arawaney.plei.MainActivity;
import com.arawaney.plei.R;
import com.arawaney.plei.db.provider.CategoryProvider;
import com.arawaney.plei.db.provider.PleilistProvider;
import com.arawaney.plei.db.provider.TrackProvider;
import com.arawaney.plei.listener.MediaPlayerListener;
import com.arawaney.plei.listener.ParseListener;
import com.arawaney.plei.model.Category;
import com.arawaney.plei.model.Cover;
import com.arawaney.plei.model.Pleilist;
import com.arawaney.plei.model.Track;
import com.arawaney.plei.parse.ParseProvider;
import com.arawaney.plei.service.StreamPlayer;
import com.arawaney.plei.util.FileUtil;
import com.arawaney.plei.util.FontUtil;
import com.nhaarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter;

public class TrackActivity extends Activity implements ParseListener,
		OnSeekBarChangeListener, MediaPlayerListener {

	private final String LOG_TAG = "Pleilist-PleiListActivity";

	TextView categoryTitle;
	TextView pleiListTitle;
	static TextView trackTitle;
	static TextView playedTime;
	static TextView totalTime;

	ImageView previewsButton;
	ImageView nextButton;
	static ImageView pausePlayButton;
	ImageView favoriteButton;

	ImageView pleiListImage;

	static int totalDuration;
	static int seekBarProgress;
	static int seekbarBufferProgress;
	static SeekBar trackProgress;
	
	public static Handler messageHandler;

//	ArrayList<Track> tracks;
	AlphaInAnimationAdapter swingAnimationAdapter;
	Category category;
	Pleilist pleilist;
	static Track currentTrack ;
	

	public final static String PLEILIST_ID_TAG = "pleilistIdTag";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track_play);

		createMessageHandler();
		getCategory();
		getPleilist();

		setActionBar();

		registerBroadCastReceiver();

		loadViews();

		loadTitles();

		loadButtons();

	}

	private void createMessageHandler() {
	 messageHandler = new MessageHandler(this);		
	}

	private void registerBroadCastReceiver() {
		

	}

	private void loadButtons() {

		previewsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				trackIndex--;
//				if (trackIndex < 0) {
//					trackIndex = tracks.size() - 1;
//				}
//				setTrackTitle();
			}
		});

		nextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				trackIndex++;
//				if (trackIndex == tracks.size()) {
//					trackIndex = 0;
//				}
//				setTrackTitle();
			}
		});

		favoriteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (pleilist.getFavorite() == Pleilist.NOT_FAVORITE) {
					favoriteButton.setImageDrawable(getResources().getDrawable(
							R.drawable.track_play_like_on));
					pleilist.setFavorite(Pleilist.FAVORITE);
					ParseProvider.insertFavoritePleilist(pleilist,
							TrackActivity.this, TrackActivity.this);
				} else {
					favoriteButton.setImageDrawable(getResources().getDrawable(
							R.drawable.track_play_like_off));
					pleilist.setFavorite(Pleilist.NOT_FAVORITE);
					ParseProvider.removeFavoritePleilist(TrackActivity.this,
							TrackActivity.this, pleilist);
				}

			}
		});

		pausePlayButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(getApplicationContext(),
						StreamPlayer.class);
				i.setAction(StreamPlayer.ACTION_PLAY);
				i.putExtra(StreamPlayer.MESSENGER_TAG, new Messenger(messageHandler));
				i.putExtra(PLEILIST_ID_TAG, pleilist.getSystem_id());
				startService(i);

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

	private static void setTrackTitle() {
		if (currentTrack == null) {
			trackTitle.setText(" ");
		}else
			trackTitle.setText(currentTrack.getName().toString());
	
	}

	private void getPleilist() {
		String pleiListId = getIntent().getStringExtra(
				MainActivity.TAG_PLEILIST_ID);
		if (pleiListId != null) {
			pleilist = PleilistProvider.readPleilist(this, pleiListId);
		} else {
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
		playedTime = (TextView) findViewById(R.id.textView_track_played_time);
		totalTime = (TextView) findViewById(R.id.textView_track_total_time);

		previewsButton = (ImageView) findViewById(R.id.imageView_track_previews_button);
		nextButton = (ImageView) findViewById(R.id.imageView_track_next_button);
		pausePlayButton = (ImageView) findViewById(R.id.imageView_track_pauseplay_button);
		favoriteButton = (ImageView) findViewById(R.id.imageView_favorite_button);

		pleiListImage = (ImageView) findViewById(R.id.imageView_track_pleilist_image);

		trackProgress = (SeekBar) findViewById(R.id.progressBar_track_play);

		if (pleilist.getImage() != null) {
			if (FileUtil.imageExists(pleilist.getImage(), this)) {
				File filePath = getFileStreamPath(pleilist.getImage());
				pleiListImage.setImageDrawable(Drawable.createFromPath(filePath
						.toString()));
			}
		}

		if (category == null) {
			categoryTitle.setVisibility(View.GONE);
		}

		if (pleilist.getFavorite() == Pleilist.FAVORITE) {
			favoriteButton.setImageDrawable(getResources().getDrawable(
					R.drawable.track_play_like_on));
		} else {
			favoriteButton.setImageDrawable(getResources().getDrawable(
					R.drawable.track_play_like_off));
		}

		trackProgress.setOnSeekBarChangeListener(this);
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
		} else {
			pleilist.setFavorite(Pleilist.NOT_FAVORITE);
			favoriteButton.setImageDrawable(getResources().getDrawable(
					R.drawable.track_play_like_off));
			Toast.makeText(this,
					getResources().getString(R.string.toast_favorited_failed),
					Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public void onFavoritesUpdated(boolean b) {

	}

	@Override
	public void onFavoritedRemoved(boolean succes, Pleilist pleilist) {
		if (succes) {
			PleilistProvider.updatePleilist(this, pleilist);
		} else {
			pleilist.setFavorite(Pleilist.FAVORITE);
			favoriteButton.setImageDrawable(getResources().getDrawable(
					R.drawable.track_play_like_on));
			Toast.makeText(this,
					getResources().getString(R.string.toast_favorited_failed),
					Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (fromUser) {
			//Enviar solicitud
		}

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnMediaPlayerBufferUpdate(int position, int timePlayed,
			int bufferPosition) {
		trackProgress.setProgress(position);
		trackProgress.setSecondaryProgress(bufferPosition);

	}

	@Override
	public void OnGotFile(int duration) {
		trackProgress.setMax(duration);
	}

	@Override
	public void OnSongReady() {
		// TODO Auto-generated method stub
		
	}
	
	public static  class MessageHandler extends Handler {
		Context context;
	    public MessageHandler( Context context) {
	    	this.context = context;
}
	    public void handleMessage(Message message) {
	        int messageMode = message.arg1;
	        switch (messageMode) {
	        
		    case StreamPlayer.MESSAGE_MODE_SONG_READY :
		    	
		    	totalDuration = message.getData().getInt(StreamPlayer.TOTAL_DURATION_TAG, 0);
		    	trackProgress.setMax(totalDuration);
				totalTime.setText(millisToTimeFormat(totalDuration/1000));
		    	
				String trackId = message.getData().getString(StreamPlayer.CURRENT_TRACK_ID_TAG);
		    	currentTrack = TrackProvider.readTrack(context, trackId);
		    	
		    	setTrackTitle();
				
		        break;
		    case StreamPlayer.MESSAGE_MODE_REFRESH_SEEKBAR :
		    	seekBarProgress = message.getData().getInt(StreamPlayer.POSITION_TAG, 0);
		    	seekbarBufferProgress = message.getData().getInt(StreamPlayer.BUFFER_POSITION_TAG, 0);
		    	
		    	playedTime.setText(millisToTimeFormat(seekBarProgress/1000));
		    	
		    	trackProgress.setProgress(seekBarProgress);
				trackProgress.setSecondaryProgress(seekbarBufferProgress);
		        
		        break;
		    case StreamPlayer.MESSAGE_MODE_NEXT_SONG :
		        
		        break;
		        
		    case StreamPlayer.MESSAGE_MODE_MUSIC_STARTED :
				setToPause(context);
		        break;
		    
		    case StreamPlayer.MESSAGE_MODE_MUSIC_ENDED :
				setToBuffering(context);
		        break;
	    }
	}

		
		private CharSequence millisToTimeFormat(int seconds) {
			String time;
			int minutes = seconds/60;
			int secs = seconds - (minutes*60);
			DecimalFormat df = new DecimalFormat("#00.###");
			time = minutes+" : "+ df.format(secs)  ;
			
			return time;
		}
	}
	
	private static void setToPause(Context context) {
		pausePlayButton.setImageDrawable(context.getResources().getDrawable(
				R.drawable.ic_pause_track));
	}
	
	private static void setToPlay(Context context) {
		pausePlayButton.setImageDrawable(context.getResources().getDrawable(
				R.drawable.ic_now_playing));
	}
	
	private static void setToBuffering(Context context) {
		pausePlayButton.setImageDrawable(context.getResources().getDrawable(
				R.drawable.ic_now_buffering));		
	}

	// public void run ProgressBarThread(){
	// new Thread(new Runnable() {
	// @Override
	// public void run() {
	// int currentPosition = 0;
	// while (!musicThreadFinished) {
	// try {
	// Thread.sleep(1000);
	// currentPosition = getCurrentPosition();
	// } catch (InterruptedException e) {
	// return;
	// } catch (Exception e) {
	// return;
	// }
	// final int total = getDuration();
	// final String totalTime = getAsTime(total);
	// final String curTime = getAsTime(currentPosition);
	//
	// trackProgress.setMax(total); //song duration
	// trackProgress.setProgress(currentPosition); //for current song progress
	// trackProgress.setSecondaryProgress(getBufferPercentage()); // for buffer
	// progress
	// runOnUiThread(new Runnable() {
	// @Override
	// public void run() {
	// if (isPlaying()) {
	// if (pausePlayButton.) {
	// playPauseButton.setChecked(true);
	// }
	// } else {
	// if (playPauseButton.isChecked()) {
	// playPauseButton.setChecked(false);
	// }
	// }
	// musicDuration.setText(totalTime);
	// musicCurLoc.setText(curTime);
	// }
	// });
	// }
	// }
	// }).start();
	// }

}
