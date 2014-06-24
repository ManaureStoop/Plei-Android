package com.arawaney.plei.service;

import java.io.IOException;
import java.util.ArrayList;

import android.app.DownloadManager.Request;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.CursorJoiner.Result;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.util.Log;

import com.arawaney.plei.MainActivity;
import com.arawaney.plei.R;
import com.arawaney.plei.activity.TrackActivity;
import com.arawaney.plei.db.provider.TrackProvider;
import com.arawaney.plei.listener.MediaPlayerListener;
import com.arawaney.plei.model.Track;
import com.arawaney.plei.util.YoutubeUtil;

public class StreamPlayer extends Service implements
		MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
		MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {

	private static final String LOG_TAG = "Plei MPLAYER Service";
	public static final String ACTION_PLAY = "com.plei.player.PLAY";
	public static final String ACTION_PAUSE = "com.plei.player.PAUSE";
	public static final String ACTION_NEXT = "com.plei.player.NEXT";
	public static final String ACTION_PREVIEWS = "com.plei.player.PREVIEWS";
	public static final String ACTION_READY = "com.plei.player.READY";
	public static final String ACTION_REQUIRE_INFO = "com.plei.player.REQUIRE_INFO";
	public static final String ACTION_REQUIRE_PLEILIST_ID = "com.plei.player.REQUIRE_PLEILIST_ID";
	public static final String ACTION_REFRESH_TRACKS = "com.plei.player.ACTION_REFRESH_TRACKS";

	public static final String TAG_KILL_SERVICE = "TAG_KILL_SERVICE";
	public static final String INTENT_KILL_SERVICE = "killService";

	public static final String MESSENGER_TAG = "messenger";
	public static final String SONG_STATUS_TAG = "songStatus";
	public static final String STATUS_PLAYING = "playing";
	public static final String STATUS_PAUSED = "paused";
	public static final String STATUS_LOADING = "loading";
	public static final String TOTAL_DURATION_TAG = "duration";
	public static final String POSITION_TAG = "position";
	public static final String BUFFER_POSITION_TAG = "buffer_position";
	public static final String CURRENT_TRACK_ID_TAG = "current_track_id";
	public static final String CURRENT_PLEILIST_ID_TAG = "current_pleilist_id";

	public static final int MESSAGE_MODE_REFRESH_SEEKBAR = 0;
	public static final int MESSAGE_MODE_SONG_READY = 1;
	public static final int MESSAGE_MODE_NEXT_SONG = 2;
	public static final int MESSAGE_MODE_MUSIC_STARTED = 3;
	public static final int MESSAGE_MODE_MUSIC_ENDED = 4;
	public static final int MESSAGE_MODE_MUSIC_PAUSED = 5;
	public static final int MESSAGE_MODE_ALL_INFO = 6;
	public static final int MESSAGE_MODE_PLEILIST_ID = 7;

	public boolean youtubeUrlsReady = false;

	public static final String PLAY_DIRECTION_BACKWARD = "backward";
	public static final String PLAY_DIRECTION_FORWARD = "forward";

	private static StreamPlayer mInstance = null;

	private MediaPlayer mMediaPlayer = null; // The Media Player
	private int mBufferPosition;

	ArrayList<Track> tracks;
	Track currentTrack;
	int indexOfTrack;
	String pleilistId;

	NotificationManager mNotificationManager;
	Notification mNotification = null;
	final int NOTIFICATION_ID = 1;

	MediaPlayerListener listener;
	private Messenger messageHandler;

	// indicates the state our service:
	enum State {
		Retrieving, // the MediaRetriever is retrieving music
		Stopped, // media player is stopped and not prepared to play
		Preparing, // media player is preparing...
		Playing, // playback active (media player ready!). (but the media player
					// may actually be
					// paused in this state if we don't have audio focus. But we
					// stay in this state
					// so that we know we have to resume playback once we get
					// focus back)
		Paused,
		// playback paused (media player ready!)
		WaitingBuffer // Waiting for 25% of load
	};

	State mState = State.Retrieving;

	@Override
	public void onCreate() {
		mInstance = this;
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		setBroadCastReceiver();

	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Bundle extras = intent.getExtras();
		messageHandler = (Messenger) extras.get(MESSENGER_TAG);

		pleilistId = intent.getStringExtra(TrackActivity.PLEILIST_ID_TAG);

		if (pleilistId != null) {
			readTracks();
			if (tracks != null) {
				startMediaPlayer();
			} else {
				Log.e(LOG_TAG, "no tracks found on player for pleilist : "
						+ pleilistId);
			}
		} else {
			Log.e(LOG_TAG, "pleilist id is null");
			this.stopSelf();
		}
		return START_STICKY;

	}

	private void readTracks() {
		tracks = TrackProvider.readTracksByPleiList(this, pleilistId);
	}

	private void startMediaPlayer() {
		
		DownloadYoutubeUrls downloadYoutubeUrls = new DownloadYoutubeUrls(this, true);
		downloadYoutubeUrls.execute();

		currentTrack = tracks.get(0);
		indexOfTrack = 0;
		configureMediaPlayer(PLAY_DIRECTION_FORWARD);
	}

	private void setBroadCastReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_PLAY);
		filter.addAction(ACTION_PAUSE);
		filter.addAction(ACTION_NEXT);
		filter.addAction(ACTION_PREVIEWS);
		filter.addAction(ACTION_REQUIRE_INFO);
		filter.addAction(ACTION_REQUIRE_PLEILIST_ID);
		filter.addAction(ACTION_REFRESH_TRACKS);
		registerReceiver(receiver, filter);
	}

	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ACTION_PLAY)) {
				if (isPaused()) {
					startMusic();
				}
			} else if (action.equals(ACTION_PAUSE)) {
				pauseMusic();
			} else if (action.equals(ACTION_NEXT)) {

				playNextSong();
			} else if (action.equals(ACTION_PREVIEWS)) {

				playPreviewSong();
			} else if (action.equals(ACTION_REQUIRE_INFO)) {
				sendMessage(MESSAGE_MODE_ALL_INFO);
			} else if (action.equals(ACTION_REQUIRE_PLEILIST_ID)) {
				sendMessage(MESSAGE_MODE_PLEILIST_ID);
			} else if (action.equals(ACTION_REFRESH_TRACKS)) {
				readTracks();
				
				if (tracks != null) {
					if (mMediaPlayer == null) {
						startMediaPlayer();
					} else {
						DownloadYoutubeUrls downloadYoutubeUrls = new DownloadYoutubeUrls(
								getApplicationContext(), false);
						downloadYoutubeUrls.execute();
					}
				}
			}
		}

	};

	private void configureMediaPlayer(String playDirection) {

		mMediaPlayer = new MediaPlayer(); // initialize it here
		mMediaPlayer.setOnPreparedListener(this);
		mMediaPlayer.setOnErrorListener(this);
		mMediaPlayer.setOnBufferingUpdateListener(this);
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		lockDevice();
		initMediaPlayer(playDirection);

	}

	private void initMediaPlayer(String playDirection) {
		if (youtubeUrlsReady) {

			if (currentTrack.getUrl() != null) {
				try {
					mMediaPlayer.setDataSource(currentTrack.getUrl());
					setUpAsForeground();
				} catch (IllegalArgumentException e) {
					// ...
				} catch (IllegalStateException e) {
					// ...
				} catch (IOException e) {
					// ...
				}

				try {
					mMediaPlayer.prepareAsync(); // prepare async to not block
													// main
													// thread
				} catch (IllegalStateException e) {
					onErrorOcurred(playDirection);
					e.printStackTrace();
				}
				mState = State.Preparing;
			} else {
				Log.d(LOG_TAG, "Track Url is null , changing song...");

				if (playDirection.equals(PLAY_DIRECTION_FORWARD)) {
					playNextSong();
				} else if (playDirection.equals(PLAY_DIRECTION_BACKWARD)) {
					playPreviewSong();
				}
			}
		}

	}

	private void onErrorOcurred(String playDirection) {
		Log.d(LOG_TAG, "Problem preparing song , changing song...");

		if (playDirection.equals(PLAY_DIRECTION_FORWARD)) {
			playNextSong();
		} else if (playDirection.equals(PLAY_DIRECTION_BACKWARD)) {
			playPreviewSong();
		}
	}

	public void restartMusic() {
		// Restart music
	}

	protected void setBufferPosition(int progress) {
		mBufferPosition = progress;
	}

	/** Called when MediaPlayer is ready */
	@Override
	public void onPrepared(MediaPlayer player) {
		sendMessage(MESSAGE_MODE_SONG_READY);
		mState = State.WaitingBuffer;
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onDestroy() {
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
		}

		unregisterReceiver(receiver);

		mState = State.Retrieving;
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		setBufferPosition(percent * getMusicDuration() / 100);

		sendMessage(MESSAGE_MODE_REFRESH_SEEKBAR);
		if (percent >= 25 && waitingForBuffer()) {
			startMusic();
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {

		playNextSong();

	}

	private void playNextSong() {
		indexOfTrack++;
		if (indexOfTrack == tracks.size()) {
			indexOfTrack = 0;
		}

		startNewSong(PLAY_DIRECTION_FORWARD);
	}

	private void playPreviewSong() {
		indexOfTrack--;
		if (indexOfTrack < 0) {
			indexOfTrack = tracks.size() - 1;
		}
		startNewSong(PLAY_DIRECTION_BACKWARD);
	}

	private void startNewSong(String playDirection) {
		
		currentTrack = tracks.get(indexOfTrack);
		sendMessage(MESSAGE_MODE_MUSIC_ENDED);
		mMediaPlayer.release();
		configureMediaPlayer(playDirection);
	}

	public MediaPlayer getMediaPlayer() {
		return mMediaPlayer;
	}

	public void pauseMusic() {
		if (isPlaying()) {
			mMediaPlayer.pause();
			mState = State.Paused;
			updateNotification();
			sendMessage(MESSAGE_MODE_MUSIC_PAUSED);
		}
	}

	public void startMusic() {
		if (!mState.equals(State.Preparing) && !mState.equals(State.Retrieving)) {
			mMediaPlayer.start();
			mState = State.Playing;
			sendMessage(MESSAGE_MODE_MUSIC_STARTED);
			updateNotification();
			mMediaPlayer.setOnCompletionListener(this);
		}
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

	public boolean isStoped() {
		if (mState.equals(State.Stopped)) {
			return true;
		}
		return false;
	}

	public boolean waitingForBuffer() {
		if (mState.equals(State.WaitingBuffer)) {
			return true;
		}
		return false;
	}

	public int getMusicDuration() {
		if (isStoped()) {
			return 0;
		}
		return mMediaPlayer.getDuration();
		// Return current music duration
	}

	public int getCurrentPosition() {
		if (isStoped()) {
			return 0;
		}
		return mMediaPlayer.getCurrentPosition();
		// Return current position
	}

	public int getBufferPosition() {
		if (isStoped()) {
			return 0;
		}
		return mBufferPosition;
	}

	public void seekMusicTo(int pos) {

	}

	public static StreamPlayer getInstance() {
		return mInstance;
	}

	/** Updates the notification. */

	/**
	 * Configures service as a foreground service. A foreground service is a
	 * service that's doing something the user is actively aware of (such as
	 * playing music), and must appear to the user as a notification. That's why
	 * we create the notification here.
	 */

	void updateNotification() {
		if (isPlaying()) {
			mNotification.icon = R.drawable.ic_now_playing;
		} else if (isPaused()) {
			mNotification.icon = R.drawable.ic_pause_track;
		}
		NotificationManager nM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nM.notify(NOTIFICATION_ID, mNotification);
	}

	void setUpAsForeground() {

		Intent contentIntent = new Intent(getApplicationContext(),
				TrackActivity.class);
		contentIntent.putExtra(TrackActivity.TAG_CALL_MODE,TrackActivity.MODE_OPEN_PLAYING_PLEILIST);
		contentIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

		PendingIntent pContentIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, contentIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		Intent cancelIntent = new Intent(getApplicationContext(),
				MainActivity.class);
		cancelIntent.putExtra(TAG_KILL_SERVICE, INTENT_KILL_SERVICE);
		PendingIntent pCancelIntent = PendingIntent.getActivity(this, 0,
				cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		mNotification = new Notification.Builder(this)
				.addAction(R.drawable.ic_stop_player, "Stop", pCancelIntent)
				.setTicker(currentTrack.getName())
				.setSmallIcon(R.drawable.ic_now_playing)
				.setContentIntent(pContentIntent)
				.setContentTitle(getResources().getString(R.string.app_name))
				.build();

		mNotification.flags |= Notification.FLAG_ONGOING_EVENT;

		startForeground(NOTIFICATION_ID, mNotification);
	}

	public void sendMessage(int messageMode) {
		Message message = Message.obtain();
		Bundle bundle = new Bundle();
		switch (messageMode) {

		case MESSAGE_MODE_SONG_READY:
			message.arg1 = MESSAGE_MODE_SONG_READY;
			bundle.putInt(TOTAL_DURATION_TAG, getMusicDuration());
			bundle.putString(CURRENT_TRACK_ID_TAG, currentTrack.getSystem_id());
			message.setData(bundle);
			break;
		case MESSAGE_MODE_REFRESH_SEEKBAR:
			message.arg1 = MESSAGE_MODE_REFRESH_SEEKBAR;
			bundle.putInt(POSITION_TAG, getCurrentPosition());
			bundle.putInt(BUFFER_POSITION_TAG, getBufferPosition());
			message.setData(bundle);
			break;
		case MESSAGE_MODE_MUSIC_STARTED:
			message.arg1 = MESSAGE_MODE_MUSIC_STARTED;
			break;

		case MESSAGE_MODE_MUSIC_ENDED:
			message.arg1 = MESSAGE_MODE_MUSIC_ENDED;
			bundle.putString(CURRENT_TRACK_ID_TAG, currentTrack.getSystem_id());
			message.setData(bundle);
			break;

		case MESSAGE_MODE_NEXT_SONG:
			message.arg1 = MESSAGE_MODE_NEXT_SONG;
			bundle.putString(CURRENT_TRACK_ID_TAG, currentTrack.getSystem_id());
			break;

		case MESSAGE_MODE_MUSIC_PAUSED:
			message.arg1 = MESSAGE_MODE_MUSIC_PAUSED;
			break;
		case MESSAGE_MODE_ALL_INFO:
			message.arg1 = MESSAGE_MODE_ALL_INFO;
			bundle.putString(CURRENT_PLEILIST_ID_TAG, pleilistId);
			bundle.putString(CURRENT_TRACK_ID_TAG, currentTrack.getSystem_id());
			bundle.putInt(TOTAL_DURATION_TAG, getMusicDuration());

			if (isPlaying()) {
				bundle.putString(SONG_STATUS_TAG, STATUS_PLAYING);
			} else if (isPaused()) {
				bundle.putString(SONG_STATUS_TAG, STATUS_PAUSED);
			} else
				bundle.putString(SONG_STATUS_TAG, STATUS_LOADING);

			message.setData(bundle);
			break;

		case MESSAGE_MODE_PLEILIST_ID:
			message.arg1 = MESSAGE_MODE_PLEILIST_ID;
			bundle.putString(CURRENT_PLEILIST_ID_TAG, pleilistId);
			message.setData(bundle);
			break;

		}
		try {
			messageHandler.send(message);
		} catch (RemoteException e) {
			Log.d(LOG_TAG, "Error sending message");
			e.printStackTrace();
		}
	}

	private void lockDevice() {
		mMediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK); //
		// Setting wakelock to be active when devices goes to sleep
		mMediaPlayer.setOnPreparedListener(this);
		WifiLock wifiLock = ((WifiManager) this
				.getSystemService(Context.WIFI_SERVICE)).createWifiLock(
				WifiManager.WIFI_MODE_FULL, "mylock");

		wifiLock.acquire();
	}

	class DownloadYoutubeUrls extends AsyncTask<Request, Void, Result> {
		Context context;
		boolean initializeNeeded;
		public DownloadYoutubeUrls(Context context, boolean initializeNeeded) {
			this.context = context;
			this.initializeNeeded = initializeNeeded;
		}

		@Override
		protected void onPreExecute() {

			super.onPreExecute();

		}

		@Override
		protected Result doInBackground(Request... params) {

			for (Track track : tracks) {

				String videoId = YoutubeUtil.getYoutubeVideoId(track
						.getYoutubeUrl());
				Log.d(LOG_TAG, videoId + " for: " + track.getName());

				String url = YoutubeUtil
						.getVideoInfo("http://www.youtube.com/get_video_info?video_id="
								+ videoId
								+ "&el=v&ps=default&eurl=&gl=US&hl=en");
				if (url == null) {
					Log.d(LOG_TAG, "URL NULL for: " + track.getName());
				} else
					Log.d(LOG_TAG, "URL : " + url + " for: " + track.getName());

				track.setUrl(url);

			}

			if (initializeNeeded) {
				youtubeUrlsReady = true;
				initMediaPlayer(PLAY_DIRECTION_FORWARD);
			}

			return null;

		}

		@Override
		protected void onPostExecute(Result res) {

		}
	}

}