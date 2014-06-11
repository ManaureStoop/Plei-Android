package com.arawaney.plei.service;

import java.io.IOException;
import java.util.ArrayList;

import com.arawaney.plei.R;
import com.arawaney.plei.activity.TrackActivity;
import com.arawaney.plei.db.provider.PleilistProvider;
import com.arawaney.plei.db.provider.TrackProvider;
import com.arawaney.plei.listener.MediaPlayerListener;
import com.arawaney.plei.model.Track;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.util.Log;

public class StreamPlayer extends Service implements
		MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
		MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {

	// public StreamPlayer() {
	// // TODO Auto-generated constructor stub
	// }
	//
	// @Override
	// public IBinder onBind(Intent intent) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	//
	// public static final String ACTION_PLAY = "com.example.action.PLAY";
	// private static final int NOTIFICATION_ID = 001;
	//
	// MediaPlayer mMediaPlayer = null;
	//
	//
	//
	// public int onStartCommand(Intent intent, int flags, int startId) {
	//
	// if (intent.getAction().equals(ACTION_PLAY)) {
	//
	// try {
	// String url = "http://www.elcedral.com/site/downloads/elCedral.mp3"; //
	// your URL here
	// mMediaPlayer = new MediaPlayer();
	// mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	// mMediaPlayer.setDataSource(url);
	// lockDevice();
	// setAsForegroundService();
	// mMediaPlayer.prepareAsync(); // prepare async to not block main thread
	// } catch (IllegalArgumentException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (SecurityException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IllegalStateException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
	// return startId;
	//
	// }
	//
	//
	// private void setAsForegroundService() {
	// String songName = "El cedral";
	// // assign the song name to songName
	// PendingIntent pi = PendingIntent.getActivity(this, 0,
	// new Intent(this, TrackActivity.class),
	// PendingIntent.FLAG_UPDATE_CURRENT);
	// Notification notification = new Notification();
	// notification.tickerText = "";
	// notification.icon = R.drawable.ic_now_playing;
	// notification.flags |= Notification.FLAG_ONGOING_EVENT;
	// notification.setLatestEventInfo(this, getString(R.string.app_name),
	// "Playing: " + songName, pi);
	// startForeground(NOTIFICATION_ID, notification);
	// }
	//
	//
	// private void lockDevice() {
	// mMediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK); //
	// Setting wakelock to be active when devices goes to sleep
	// mMediaPlayer.setOnPreparedListener(this);
	// WifiLock wifiLock = ((WifiManager)
	// this.getSystemService(Context.WIFI_SERVICE))
	// .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
	//
	// wifiLock.acquire();
	// }
	//
	// /** Called when MediaPlayer is ready */
	// public void onPrepared(MediaPlayer player) {
	// player.start();
	// }
	//
	// @Override
	// public boolean onError(MediaPlayer mp, int what, int extra) {
	// // TODO Auto-generated method stub
	// // The MediaPlayer has moved to the Error state, must be reset!
	// return false;
	// }
	//
	// public void onDestroy() {
	// if (mMediaPlayer != null) mMediaPlayer.release();
	// }
	//
	// @Override
	// public void onBufferingUpdate(MediaPlayer mp, int percent) {
	// // TODO Auto-generated method stub
	//
	// }

	private static final String LOG_TAG = "Plei MPLAYER Service";
	public static final String ACTION_PLAY = "com.plei.player.PLAY";
	public static final String ACTION_PAUSE = "com.plei.player.PAUSE";
	public static final String ACTION_NEXT = "com.plei.player.NEXT";
	public static final String ACTION_PREVIEWS = "com.plei.player.PREVIEWS";
	public static final String ACTION_READY = "com.plei.player.READY";
	
	public static final String MESSENGER_TAG = "messenger";
	public static final String TOTAL_DURATION_TAG = "duration";
	public static final String POSITION_TAG = "position";
	public static final String BUFFER_POSITION_TAG = "buffer_position";
	public static final String CURRENT_TRACK_ID_TAG = "current_track_id";


	
	public static final int MESSAGE_MODE_REFRESH_SEEKBAR = 0;
	public static final int MESSAGE_MODE_SONG_READY = 1;
	public static final int MESSAGE_MODE_NEXT_SONG = 2;
	public static final int MESSAGE_MODE_MUSIC_STARTED = 3;
	public static final int MESSAGE_MODE_MUSIC_ENDED = 4;

	

	
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
			tracks = TrackProvider.readTracksByPleiList(this, pleilistId);
			//Temporal!
			changeUrls();
			currentTrack = tracks.get(0);
			indexOfTrack = 0;
			configureMediaPlayer(intent);
		} else {
			Log.e(LOG_TAG, "pleilist id is null");
			this.stopSelf();
		}
		return START_STICKY;

	}

	private void changeUrls() {
		for (Track track : tracks) {
			int position = tracks.indexOf(track);
			switch (position) {
			case 0:
				track.setUrl("http://web.7.c3.audiovideoweb.com/va90web25003/companions/Foundations%20of%20Rock/13.01.mp3");
				break;
			case 1:
				track.setUrl("http://web.7.c3.audiovideoweb.com/va90web25003/companions/Foundations%20of%20Rock/13.02.mp3");
				break;
			case 2:
				track.setUrl("http://web.7.c3.audiovideoweb.com/va90web25003/companions/Foundations%20of%20Rock/13.03.mp3");
				break;
			case 3:
				track.setUrl("http://web.7.c3.audiovideoweb.com/va90web25003/companions/Foundations%20of%20Rock/13.04.mp3");
				break;
			case 4:
				track.setUrl("http://web.7.c3.audiovideoweb.com/va90web25003/companions/Foundations%20of%20Rock/13.05.mp3");
				break;
			case 5:
				track.setUrl("http://web.7.c3.audiovideoweb.com/va90web25003/companions/Foundations%20of%20Rock/13.06.mp3");
				break;
			case 6:
				track.setUrl("http://web.7.c3.audiovideoweb.com/va90web25003/companions/Foundations%20of%20Rock/13.07.mp3");
				break;
			case 7:
				track.setUrl("http://www.elcedral.com/site/downloads/elCedral.mp3");
				break;
			case 8:
				track.setUrl("http://web.7.c3.audiovideoweb.com/va90web25003/companions/Foundations%20of%20Rock/13.04.mp3");
				break;
			case 9:
				track.setUrl("http://web.7.c3.audiovideoweb.com/va90web25003/companions/Foundations%20of%20Rock/13.05.mp3");
				break;

			default:
				break;
			}
		}
	}

	private void configureMediaPlayer(Intent intent) {
		if (intent.getAction().equals(ACTION_PLAY)) {
			mMediaPlayer = new MediaPlayer(); // initialize it here
			mMediaPlayer.setOnPreparedListener(this);
			mMediaPlayer.setOnErrorListener(this);
			mMediaPlayer.setOnBufferingUpdateListener(this);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			lockDevice();
			initMediaPlayer();
		}
	}

	private void initMediaPlayer() {
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
			mMediaPlayer.prepareAsync(); // prepare async to not block main
											// thread
		} catch (IllegalStateException e) {
			// ...
		}
		mState = State.Preparing;
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
		mState = State.Retrieving;
	}
	
	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		setBufferPosition(percent * getMusicDuration() / 100);
	
		sendMessage(MESSAGE_MODE_REFRESH_SEEKBAR);
		if (percent >= 25 && waitingForBuffer()){
			startMusic();
		}
	}
	
	@Override
	public void onCompletion(MediaPlayer mp) {
		
		Log.i("Completion Listener","Song Complete");

		
		indexOfTrack ++;
		if (indexOfTrack == tracks.size()) {
			indexOfTrack = 0;
		}
		currentTrack = tracks.get(indexOfTrack);
		
		mState = State.Stopped;
        mp.stop();
        mp.reset();
		
		initMediaPlayer();
	}

	public MediaPlayer getMediaPlayer() {
		return mMediaPlayer;
	}

	public void pauseMusic() {
		if (mState.equals(State.Playing)) {
			mMediaPlayer.pause();
			mState = State.Paused;
			updateNotification(currentTrack.getName() + "(paused)");
		}
	}

	public void startMusic() {
		if (!mState.equals(State.Preparing) && !mState.equals(State.Retrieving)) {
			mMediaPlayer.start();
			mState = State.Playing;
			sendMessage(MESSAGE_MODE_MUSIC_STARTED);
			updateNotification(currentTrack.getName() + "(playing)");
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
	
	void updateNotification(String text) {
		// Notify NotificationManager of new intent
	}


	void setUpAsForeground() {
		PendingIntent pi = PendingIntent.getActivity(getApplicationContext(),
				0, new Intent(getApplicationContext(), TrackActivity.class),
				PendingIntent.FLAG_UPDATE_CURRENT);
		mNotification = new Notification();
		mNotification.tickerText = currentTrack.getName();
		mNotification.icon = R.drawable.ic_now_playing;
		;
		mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
		mNotification.setLatestEventInfo(getApplicationContext(),
				getResources().getString(R.string.app_name), currentTrack.getName(), pi);
		startForeground(NOTIFICATION_ID, mNotification);
	}
	
	
	public void sendMessage(int messageMode) {
		Message message = Message.obtain();
	    Bundle bundle = new Bundle();
		switch (messageMode) {
    
		    case MESSAGE_MODE_SONG_READY :
		        message.arg1 = MESSAGE_MODE_SONG_READY;
		        bundle.putInt(TOTAL_DURATION_TAG, getMusicDuration());
		        bundle.putString(CURRENT_TRACK_ID_TAG, currentTrack.getSystem_id());
		        message.setData(bundle);
		        break;
		    case MESSAGE_MODE_REFRESH_SEEKBAR :
		        message.arg1 = MESSAGE_MODE_REFRESH_SEEKBAR;
		        bundle.putInt(POSITION_TAG, getCurrentPosition());
		        bundle.putInt(BUFFER_POSITION_TAG, getBufferPosition());
		        message.setData(bundle);
		        break;
		    case MESSAGE_MODE_MUSIC_STARTED :
		        message.arg1 = MESSAGE_MODE_MUSIC_STARTED;
		        break;
		        
		    case MESSAGE_MODE_MUSIC_ENDED :
		        message.arg1 = MESSAGE_MODE_MUSIC_ENDED;
		        break;
		        
		    case MESSAGE_MODE_NEXT_SONG :
		        message.arg1 = MESSAGE_MODE_NEXT_SONG;
		        bundle.putString(CURRENT_TRACK_ID_TAG, currentTrack.getSystem_id());
		        break;

		}
		try {
		    messageHandler.send(message);
		} catch (RemoteException e) {
		    e.printStackTrace();
		}
		}
	
	private void lockDevice() {
		 mMediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK); //
//		 Setting wakelock to be active when devices goes to sleep
		 mMediaPlayer.setOnPreparedListener(this);
		 WifiLock wifiLock = ((WifiManager)
		 this.getSystemService(Context.WIFI_SERVICE))
		 .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
		
		 wifiLock.acquire();
		 }



}