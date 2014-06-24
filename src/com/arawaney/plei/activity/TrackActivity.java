package com.arawaney.plei.activity;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Currency;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.CursorJoiner.Result;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
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
import com.arawaney.plei.util.ServiceUtil;
import com.arawaney.plei.util.YoutubeUtil;

public class TrackActivity extends Activity implements ParseListener,
		OnSeekBarChangeListener {

	private final static String LOG_TAG = "Pleilist-TrackActivity";

	static TextView categoryTitle;
	static TextView pleiListTitle;
	static TextView trackTitle;
	static TextView playedTime;
	static TextView totalTime;

	ImageView previewsButton;
	ImageView nextButton;
	static ImageView pausePlayButton;
	static ImageView favoriteButton;

	static ImageView pleiListImage;

	public static Animation anim;

	static int totalDuration;
	static int seekBarProgress;
	static int seekbarBufferProgress;
	static SeekBar trackProgress;

	public static Handler messageHandler;

	ArrayList<Track> tracks;
	static Category category;
	static Pleilist pleilist;
	static Track currentTrack;

	public final static String PLEILIST_ID_TAG = "pleilistIdTag";

	public static final String TAG_CALL_MODE = "callMode";
	public static final String MODE_NEW_PLEILIST = "modeNewPleilist";
	public static final String MODE_OPEN_PLAYING_PLEILIST = "modeOpenPlayingPLeilist";

	public static final String ACTION_FINISH = "com.plei.player.FINISH";

	enum State {
		Retrieving, Playing, Paused,
	};

	static State mState = State.Retrieving;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track_play);

		createMessageHandler();

		setBroadCastReceiver();

		connectToMediaPlayer();

		setActionBar();

		loadViews();

		scaleAnimation();

		loadTitles(this);
		setTrackTitle();

		loadButtons();

	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	private void connectToMediaPlayer() {
		if (getIntent().getStringExtra(TAG_CALL_MODE).equals(MODE_NEW_PLEILIST)) {

			getCategory();

			getPleilist();

			if (ServiceUtil
					.isServiceRunning(StreamPlayer.class.getName(), this)) {
				Intent intent = new Intent();
				intent.setAction(StreamPlayer.ACTION_REQUIRE_PLEILIST_ID);
				sendBroadcast(intent);
			} else {

				runNewService(this, this);
			}

		} else if (getIntent().getStringExtra(TAG_CALL_MODE).equals(
				MODE_OPEN_PLAYING_PLEILIST)) {
			Intent intent = new Intent();
			intent.setAction(StreamPlayer.ACTION_REQUIRE_INFO);
			sendBroadcast(intent);
		}
	}

	private void scaleAnimation() {
		anim = AnimationUtils.loadAnimation(this, R.drawable.scale);
	}

	private void createMessageHandler() {
		messageHandler = new MessageHandler(this, this);
	}

	private void loadButtons() {

		previewsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(StreamPlayer.ACTION_PREVIEWS);
				sendBroadcast(intent);
			}
		});

		nextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(StreamPlayer.ACTION_NEXT);
				sendBroadcast(intent);
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
				Intent intent = new Intent();

				if (isPaused()) {
					intent.setAction(StreamPlayer.ACTION_PLAY);
					sendBroadcast(intent);
				} else if (isPlaying()) {
					intent.setAction(StreamPlayer.ACTION_PAUSE);
					sendBroadcast(intent);
				}
			}

		});

	}

	private static void runService(Context context) {
		if (pleilist != null) {
			Intent i = new Intent(context, StreamPlayer.class);
			i.setAction(StreamPlayer.ACTION_PLAY);
			i.putExtra(StreamPlayer.MESSENGER_TAG,
					new Messenger(messageHandler));
			i.putExtra(PLEILIST_ID_TAG, pleilist.getSystem_id());
			context.startService(i);
		}

	}

	private static void loadTitles(Context context) {
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

		setFonts(context);

	}

	private static void setFonts(Context context) {
		pleiListTitle.setTypeface(FontUtil.getTypeface(context,
				FontUtil.HELVETICA_NEUE_LIGHT));
		categoryTitle.setTypeface(FontUtil.getTypeface(context,
				FontUtil.HELVETICA_NEUE_LIGHT));
		trackTitle.setTypeface(FontUtil.getTypeface(context,
				FontUtil.HELVETICA_NEUE_LIGHT));
	}

	private static void setTrackTitle() {
		if (currentTrack == null) {
			trackTitle.setText(" ");
		} else {
			if (currentTrack.getName() != null
					&& currentTrack.getArtist() != null) {
				String title = currentTrack.getName() + "  -  "
						+ currentTrack.getArtist();
				trackTitle.setText(title);
			}
		}
	}

	private void getPleilist() {
		String pleiListId = getIntent().getStringExtra(
				MainActivity.TAG_PLEILIST_ID);
		if (pleiListId != null) {
			pleilist = PleilistProvider.readPleilist(this, pleiListId);
			if (pleilist == null) {
				Log.d(LOG_TAG, "pleilist null : " + pleiListId);
				finish();
			}
		} else {
			Log.d(LOG_TAG, "Pleilist Id null");
			finish();
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
		anim = AnimationUtils.loadAnimation(this, R.drawable.scale);

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

		trackProgress.setProgress(0);

		loadPleilistImages(this);

		setToBuffering(this);

		if (category == null) {
			categoryTitle.setVisibility(View.GONE);
		}

		trackProgress.setOnSeekBarChangeListener(this);

		trackTitle.setText("");
		trackTitle.setSelected(true);

	}

	private static void loadPleilistImages(Context context) {
		if (pleilist != null) {

			if (pleilist.getFavorite() == Pleilist.FAVORITE) {
				favoriteButton.setImageDrawable(context.getResources()
						.getDrawable(R.drawable.track_play_like_on));
			} else {
				favoriteButton.setImageDrawable(context.getResources()
						.getDrawable(R.drawable.track_play_like_off));
			}

			if (pleilist.getImage() != null) {
				if (FileUtil.imageExists(pleilist.getImage(), context)) {
					File filePath = context.getFileStreamPath(pleilist
							.getImage());
					pleiListImage.setImageDrawable(Drawable
							.createFromPath(filePath.toString()));
				}
			}
		} else {
			favoriteButton.setImageDrawable(context.getResources().getDrawable(
					R.drawable.track_play_like_off));
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
			// Enviar solicitud
		}

	}

	public static class MessageHandler extends Handler {
		Context context;
		ParseListener listener;

		public MessageHandler(Context context, ParseListener listener) {
			this.context = context;
			this.listener = listener;
		}

		public void handleMessage(Message message) {
			int messageMode = message.arg1;
			switch (messageMode) {

			case StreamPlayer.MESSAGE_MODE_SONG_READY:

				loadPlayingTimeStatus(message);

				String trackId = message.getData().getString(
						StreamPlayer.CURRENT_TRACK_ID_TAG);
				currentTrack = TrackProvider.readTrack(context, trackId);

				setTrackTitle();

				break;
			case StreamPlayer.MESSAGE_MODE_REFRESH_SEEKBAR:
				seekBarProgress = message.getData().getInt(
						StreamPlayer.POSITION_TAG, 0);
				seekbarBufferProgress = message.getData().getInt(
						StreamPlayer.BUFFER_POSITION_TAG, 0);

				playedTime.setText(millisToTimeFormat(seekBarProgress / 1000));

				trackProgress.setProgress(seekBarProgress);
				trackProgress.setSecondaryProgress(seekbarBufferProgress);

				break;
			case StreamPlayer.MESSAGE_MODE_NEXT_SONG:

				break;

			case StreamPlayer.MESSAGE_MODE_MUSIC_STARTED:
				setToPause(context);
				break;

			case StreamPlayer.MESSAGE_MODE_MUSIC_ENDED:

				setToBuffering(context);

				String trackid = message.getData().getString(
						StreamPlayer.CURRENT_TRACK_ID_TAG);
				currentTrack = TrackProvider.readTrack(context, trackid);

				setTrackTitle();

				resetTimes();

				break;

			case StreamPlayer.MESSAGE_MODE_MUSIC_PAUSED:
				setToPlay(context);
				break;

			case StreamPlayer.MESSAGE_MODE_ALL_INFO:

				loadPlayingTimeStatus(message);

				String playingTrackId = message.getData().getString(
						StreamPlayer.CURRENT_TRACK_ID_TAG);
				currentTrack = TrackProvider.readTrack(context, playingTrackId);

				Log.d("TEST", "duration: " + totalDuration + " track: "
						+ playingTrackId);

				String playingPleilistId = message.getData().getString(
						StreamPlayer.CURRENT_PLEILIST_ID_TAG);
				pleilist = PleilistProvider.readPleilist(context,
						playingPleilistId);

				setSongStatus(message);

				loadTitles(context);
				loadPleilistImages(context);
				setTrackTitle();

				break;

			case StreamPlayer.MESSAGE_MODE_PLEILIST_ID:
				String pleilistId = message.getData().getString(
						StreamPlayer.CURRENT_PLEILIST_ID_TAG);

				if (pleilistId.equals(pleilist.getSystem_id())) {
					requireCurrentPlayerInfo();
				} else {
					context.stopService(new Intent(context, StreamPlayer.class));
					runNewService(context, listener);

				}

				break;
			}
		}

		private void resetTimes() {
			seekBarProgress = 0;
			seekbarBufferProgress = 0;

			playedTime.setText(millisToTimeFormat(0));

			trackProgress.setProgress(seekBarProgress);
			trackProgress.setSecondaryProgress(seekbarBufferProgress);

			totalDuration = 0;
			trackProgress.setMax(totalDuration);
			totalTime.setText(millisToTimeFormat(totalDuration / 1000));
		}

		private void requireCurrentPlayerInfo() {
			Intent intent = new Intent();
			intent.setAction(StreamPlayer.ACTION_REQUIRE_INFO);
			context.sendBroadcast(intent);
		}

		private void setSongStatus(Message message) {
			String songStatus = message.getData().getString(
					StreamPlayer.SONG_STATUS_TAG);
			if (songStatus != null) {
				if (songStatus.equals(StreamPlayer.STATUS_PLAYING)) {
					setToPause(context);
				} else if (songStatus.equals(StreamPlayer.STATUS_PAUSED)) {
					setToPlay(context);
				} else if (songStatus.equals(StreamPlayer.STATUS_LOADING)) {
					setToBuffering(context);
				}

			} else {
				Log.d(LOG_TAG, "No song status sended");
			}

		}

		private void loadPlayingTimeStatus(Message message) {
			totalDuration = message.getData().getInt(
					StreamPlayer.TOTAL_DURATION_TAG, 0);
			trackProgress.setMax(totalDuration);
			totalTime.setText(millisToTimeFormat(totalDuration / 1000));
		}

		private CharSequence millisToTimeFormat(int seconds) {
			String time;
			int minutes = seconds / 60;
			int secs = seconds - (minutes * 60);
			DecimalFormat df = new DecimalFormat("#00.###");
			time = minutes + " : " + df.format(secs);

			return time;
		}
	}

	private static void runNewService(Context context, ParseListener listener) {
		LoadTracksByPleilist loadTracksByPleilist = new LoadTracksByPleilist(
				context, listener);
		loadTracksByPleilist.execute();
		runService(context);
	}

	private static void setToPause(Context context) {
		pausePlayButton.setImageDrawable(context.getResources().getDrawable(
				R.drawable.ic_pause_track));
		pausePlayButton.clearAnimation();
		mState = State.Playing;

	}

	private static void setToPlay(Context context) {
		pausePlayButton.setImageDrawable(context.getResources().getDrawable(
				R.drawable.ic_now_playing));
		pausePlayButton.clearAnimation();
		mState = State.Paused;
	}

	private static void setToBuffering(Context context) {
		pausePlayButton.setImageDrawable(context.getResources().getDrawable(
				R.drawable.ic_now_buffering));
		pausePlayButton.setAnimation(anim);
		anim.start();
		mState = State.Retrieving;
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	public boolean isPlaying() {
		if (mState.equals(State.Playing)) {
			return true;
		}
		return false;
	}

	public boolean isPaused() {
		if (mState.equals(State.Paused)) {
			return true;
		}
		return false;
	}

	public boolean isRetrieving() {
		if (mState.equals(State.Retrieving)) {
			return true;
		}
		return false;
	}

	@Override
	public void onImageCoverDownloaded() {

	}

	@Override
	public void onImagePleilistDownloaded() {
		if (pleilist.getImage() != null) {
			if (FileUtil.imageExists(pleilist.getImage(), this)) {
				File filePath = this.getFileStreamPath(pleilist.getImage());
				pleiListImage.setImageDrawable(Drawable.createFromPath(filePath
						.toString()));
			}
		}

	}

	private void setBroadCastReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_FINISH);
		registerReceiver(receiver, filter);
	}

	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ACTION_FINISH)) {
				finish();
			}
		}
	};

	static class LoadTracksByPleilist extends AsyncTask<Request, Void, Result> {
		Context context;
		ParseListener listener;

		public LoadTracksByPleilist(Context context, ParseListener listener) {
			this.context = context;
			this.listener = listener;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Result doInBackground(Request... params) {

			ParseProvider.updateTracksByPleilist(context, listener,
					pleilist.getSystem_id());
			return null;
		}

		@Override
		protected void onPostExecute(Result res) {

		}
	}

	@Override
	public void onAllTracksByPLeilistFinished() {

		tracks = TrackProvider.readTracksByPleiList(this,
				pleilist.getSystem_id());

		if (tracks != null) {
			currentTrack = tracks.get(0);
			setTrackTitle();
		}

		Intent intent = new Intent();
		intent.setAction(StreamPlayer.ACTION_REFRESH_TRACKS);
		sendBroadcast(intent);
	}

}
