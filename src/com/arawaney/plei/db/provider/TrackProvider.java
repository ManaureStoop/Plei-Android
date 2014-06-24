package com.arawaney.plei.db.provider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.arawaney.plei.db.PleilistTrackEntity;
import com.arawaney.plei.db.TrackEntity;
import com.arawaney.plei.db.PleiProvider;
import com.arawaney.plei.model.Pleilist;
import com.arawaney.plei.model.Track;
import com.arawaney.plei.util.CalendarUtil;

public class TrackProvider {
	private static final String LOG_TAG = "Plei-TrackProvider";

	public static final Uri URI_TRACK = Uri.parse("content://"
			+ PleiProvider.PROVIDER_NAME + "/" + TrackEntity.TABLE);
	public static final Uri URI_PLEILIST_TRACK = Uri.parse("content://"
			+ PleiProvider.PROVIDER_NAME + "/" + PleilistTrackEntity.TABLE);

	public static long insertTrack(Context context, Track track) {

		if (context == null || track == null)
			return -1;

		try {
			ContentValues values = new ContentValues();
			values.put(TrackEntity.COLUMN_SYSTEM_ID, track.getSystem_id());
			values.put(TrackEntity.COLUMN_NAME, track.getName());

			if (track.getUrl() != null) {

				values.put(TrackEntity.COLUMN_URL, track.getUrl());
			} else {
				Log.d(LOG_TAG, "Url id null inserting: " + track.getName());
			}

			if (track.getUpdated_at() != null) {
				values.put(TrackEntity.COLUMN_UPDATED_AT, track.getUpdated_at()
						.getTimeInMillis());
			} else {
				Log.d(LOG_TAG, "Updated_At null inserting: " + track.getName());

			}

			if (track.getArtist() != null) {

				values.put(TrackEntity.COLUMN_ARTIST, track.getArtist());
			} else {
				Log.d(LOG_TAG, "Artist is null inserting: " + track.getName());
			}

			if (track.getYoutubeUrl() != null) {

				values.put(TrackEntity.COLUMN_YOUTUBE_URL,
						track.getYoutubeUrl());
			} else {
				Log.d(LOG_TAG,
						"youtubeUrl is null inserting: " + track.getName());
			}

			final Uri result = context.getContentResolver().insert(URI_TRACK,
					values);

			if (result != null) {
				long id = Long.parseLong(result.getPathSegments().get(1));
				if (id > 0) {
					Log.i(LOG_TAG, " Track :" + track.getName() + " id: "
							+ track.getSystem_id() + " has bee inserted");
					return id;
				} else
					Log.e(LOG_TAG, " Track :" + track.getName()
							+ " has not bee inserted ");

			}
		} catch (Exception e) {
			Log.e(LOG_TAG, " Track :" + track.getName()
					+ " has not been inserted");
			e.printStackTrace();
		}
		return -1;

	}

	public static long insertPelilistTrack(Context context, String trackId,
			String pleiListId, int order) {

		if (context == null || trackId == null || pleiListId == null)
			return -1;

		try {
			ContentValues values = new ContentValues();
			values.put(PleilistTrackEntity.COLUMN_TRACK_ID, trackId);
			values.put(PleilistTrackEntity.COLUMN_PLEILIST_ID, pleiListId);
			if (order != -1) {

				values.put(PleilistTrackEntity.COLUMN_PLEILIST_ORDER, order);
			} else {
				Log.d(LOG_TAG, "order is null inserting: " + trackId);
			}

			final Uri result = context.getContentResolver().insert(
					URI_PLEILIST_TRACK, values);

			if (result != null) {
				long id = Long.parseLong(result.getPathSegments().get(1));
				if (id > 0) {
					Log.i(LOG_TAG, " Track relation with Pleilist: track "
							+ trackId + " pleilist " + pleiListId
							+ " has bee inserted");
					return id;
				} else
					Log.e(LOG_TAG, " Track :" + trackId
							+ "  relation has not been inserted with: "
							+ pleiListId);

			}
		} catch (Exception e) {
			Log.e(LOG_TAG, " Track :" + trackId
					+ "  relation has not been inserted with: " + pleiListId);
			e.printStackTrace();
		}
		return -1;

	}

	public static boolean updateTrack(Context context, Track track) {

		if (context == null || track == null)
			return false;

		try {
			ContentValues values = new ContentValues();
			values.put(TrackEntity.COLUMN_ID, track.getId());
			values.put(TrackEntity.COLUMN_SYSTEM_ID, track.getSystem_id());
			values.put(TrackEntity.COLUMN_NAME, track.getName());
			if (track.getUrl() != null) {
				values.put(TrackEntity.COLUMN_URL, track.getUrl());
			} else {
				Log.d(LOG_TAG, "Url id null inserting: " + track.getName());
			}

			if (track.getUpdated_at() != null) {
				values.put(TrackEntity.COLUMN_UPDATED_AT, track.getUpdated_at()
						.getTimeInMillis());
			} else {
				Log.d(LOG_TAG, "Updated_At null inserting: " + track.getName());

			}

			if (track.getArtist() != null) {

				values.put(TrackEntity.COLUMN_ARTIST, track.getArtist());
			} else {
				Log.d(LOG_TAG, "Artist is null inserting: " + track.getName());
			}

			if (track.getYoutubeUrl() != null) {

				values.put(TrackEntity.COLUMN_YOUTUBE_URL,
						track.getYoutubeUrl());
			} else {
				Log.d(LOG_TAG,
						"youtubeUrl is null inserting: " + track.getName());
			}

			String condition = TrackEntity.COLUMN_SYSTEM_ID + " = " + "'"
					+ String.valueOf(track.getSystem_id()) + "'";

			int row = context.getContentResolver().update(URI_TRACK, values,
					condition, null);

			if (row == 1) {
				Log.i(LOG_TAG, " Track :" + track.getName()
						+ " has bee updated");
				return true;
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, " Track :" + track.getName()
					+ " has not bee updated " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	public static Track readTrack(Context context, String trackID) {

		if (context == null)
			return null;

		String condition = TrackEntity.COLUMN_SYSTEM_ID + " = " + "'" + trackID
				+ "'";

		final Cursor cursor = context.getContentResolver().query(URI_TRACK,
				null, condition, null, null);

		Track track = null;

		if (cursor.getCount() == 0) {
			cursor.close();
			return null;
		}
		try {
			if (cursor.moveToFirst()) {

				do {

					final long id = cursor.getLong(cursor
							.getColumnIndex(TrackEntity.COLUMN_ID));
					final String system_id = cursor.getString(cursor
							.getColumnIndex(TrackEntity.COLUMN_SYSTEM_ID));
					final String name = cursor.getString(cursor
							.getColumnIndex(TrackEntity.COLUMN_NAME));
					final long updated_at = cursor.getInt(cursor
							.getColumnIndex(TrackEntity.COLUMN_UPDATED_AT));
					final String url = cursor.getString(cursor
							.getColumnIndex(TrackEntity.COLUMN_URL));
					final String artist = cursor.getString(cursor
							.getColumnIndex(TrackEntity.COLUMN_ARTIST));
					final String youtubeUrl = cursor.getString(cursor
							.getColumnIndex(TrackEntity.COLUMN_YOUTUBE_URL));

					Calendar updatedAt = Calendar.getInstance();
					updatedAt.setTimeInMillis(updated_at);

					track = new Track();
					track.setId(id);
					track.setSystem_id(system_id);
					track.setName(name);

					if (updatedAt != null) {
						track.setUpdated_at(updatedAt);
					}
					if (url != null) {
						track.setUrl(url);
					}
					if (artist != null) {
						track.setArtist(artist);
					}
					if (youtubeUrl != null) {
						track.setYoutubeUrl(youtubeUrl);
					}

				} while (cursor.moveToNext());
			}

		} catch (Exception e) {
			track = null;
			Log.e(LOG_TAG, "Error : " + e.getMessage());
		} finally {
			cursor.close();
		}
		return track;
	}

	public static ArrayList<Track> readTracks(Context context) {

		if (context == null)
			return null;

		ArrayList<Track> tracks = new ArrayList<Track>();

		final Cursor cursor = context.getContentResolver().query(URI_TRACK,
				null, null, null, null);

		Track track = null;

		if (cursor.getCount() == 0) {
			cursor.close();
			return null;
		}

		try {
			if (cursor.moveToFirst()) {

				do {
					final long id = cursor.getLong(cursor
							.getColumnIndex(TrackEntity.COLUMN_ID));
					final String system_id = cursor.getString(cursor
							.getColumnIndex(TrackEntity.COLUMN_SYSTEM_ID));
					final String name = cursor.getString(cursor
							.getColumnIndex(TrackEntity.COLUMN_NAME));
					final long updated_at = cursor.getInt(cursor
							.getColumnIndex(TrackEntity.COLUMN_UPDATED_AT));
					final String url = cursor.getString(cursor
							.getColumnIndex(TrackEntity.COLUMN_URL));
					final String artist = cursor.getString(cursor
							.getColumnIndex(TrackEntity.COLUMN_ARTIST));
					final String youtubeUrl = cursor.getString(cursor
							.getColumnIndex(TrackEntity.COLUMN_YOUTUBE_URL));

					Calendar updatedAt = Calendar.getInstance();
					updatedAt.setTimeInMillis(updated_at);

					track = new Track();
					track.setId(id);
					track.setSystem_id(system_id);
					track.setName(name);

					if (updatedAt != null) {
						track.setUpdated_at(updatedAt);
					}
					if (url != null) {
						track.setUrl(url);
					}
					if (artist != null) {
						track.setArtist(artist);
					}
					if (youtubeUrl != null) {
						track.setYoutubeUrl(youtubeUrl);
					}

					tracks.add(track);

				} while (cursor.moveToNext());
			}

		} catch (Exception e) {
			tracks = null;
			Log.e(LOG_TAG, "Error : " + e.getMessage());
		} finally {
			cursor.close();
		}
		return tracks;
	}

	public static ArrayList<Track> readTracksByPleiList(Context context,
			String pleilistId) {

		if (context == null)
			return null;

		ArrayList<Track> tracks = new ArrayList<Track>();

		String condition = PleilistTrackEntity.COLUMN_PLEILIST_ID + " = " + "'"
				+ pleilistId + "'";

		Log.e(LOG_TAG, "Searching tracks for " + pleilistId);

		final Cursor cursor = context.getContentResolver().query(
				URI_PLEILIST_TRACK, null, condition, null,
				PleilistTrackEntity.COLUMN_PLEILIST_ORDER + " ASC");

		Track track = null;

		if (cursor.getCount() == 0) {
			cursor.close();
			Log.e(LOG_TAG, "Error : no tracks found ");
			return null;
		}

		try {
			if (cursor.moveToFirst()) {

				do {

					final String trackID = cursor
							.getString(cursor
									.getColumnIndex(PleilistTrackEntity.COLUMN_TRACK_ID));

					track = readTrack(context, trackID);
					if (track != null) {
						tracks.add(track);

					} else {
						Log.d(LOG_TAG, "track with ID : " + trackID
								+ "is null reading from pleilist: "
								+ pleilistId);

					}

				} while (cursor.moveToNext());
			}

		} catch (Exception e) {
			tracks = null;
			Log.e(LOG_TAG, "Error : " + e.getMessage());
		} finally {
			cursor.close();
		}
		return tracks;
	}

	public static boolean removeTrack(Context context, long trackId) {

		try {
			String condition = TrackEntity.COLUMN_ID + " = "
					+ String.valueOf(trackId);
			int rows = context.getContentResolver().delete(URI_TRACK,
					condition, null);

			if (rows == 1) {
				Log.i(LOG_TAG, "Track : " + trackId + "has been deleted");
				return true;
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, "Error deleting track: " + e.getMessage());
		}
		return false;
	}

	public static Date getLastUpdate(Context context) {
		final Cursor cursor = context.getContentResolver().query(URI_TRACK,
				null, null, null, TrackEntity.COLUMN_UPDATED_AT + " DESC");

		if (cursor.getCount() == 0) {
			cursor.close();
			return null;
		}

		try {
			if (cursor.moveToFirst()) {

				final long updated_at = cursor.getLong(cursor
						.getColumnIndex(TrackEntity.COLUMN_UPDATED_AT));
				Date date = new Date(updated_at);
				Log.d(LOG_TAG,
						"last update "
								+ CalendarUtil.getDateFormated(date,
										"dd MM yyy mm:ss"));

				return date;
			}

		} catch (Exception e) {
			Log.e(LOG_TAG, "Error : " + e.getMessage());
		} finally {
			cursor.close();
		}

		return null;
	}

	public static String readTrackRelation(Context context, String trackId) {

		if (context == null)
			return null;

		String condition = PleilistTrackEntity.COLUMN_TRACK_ID + " = " + "'"
				+ trackId + "'";

		Log.e(LOG_TAG, "Searching pleilist for " + trackId);

		final Cursor cursor = context.getContentResolver().query(
				URI_PLEILIST_TRACK, null, condition, null,
				PleilistTrackEntity.COLUMN_PLEILIST_ORDER + " ASC");

		Track track = null;

		if (cursor.getCount() == 0) {
			cursor.close();
			Log.e(LOG_TAG, "Error : no pleilist found ");
			return null;
		}

		String pleilistId = null;
		try {
			if (cursor.moveToFirst()) {

				do {

					pleilistId = cursor
							.getString(cursor
									.getColumnIndex(PleilistTrackEntity.COLUMN_PLEILIST_ID));

				} while (cursor.moveToNext());
			}

		} catch (Exception e) {
			pleilistId = null;
			Log.e(LOG_TAG, "Error : " + e.getMessage());
		} finally {
			cursor.close();
		}
		return pleilistId;
	}

	public static Date getLastUpdateByPleilist(Context context,
			String pleilistId) {
		Date maximum = null;
		ArrayList<Track> tracks = readTracksByPleiList(context, pleilistId);
		if (tracks != null) {
			maximum = tracks.get(0).getUpdated_at().getTime();
			for (Track track : tracks) {
				if (track.getUpdated_at().getTime().after(maximum)) {
					maximum = track.getUpdated_at().getTime();
				}
			}

		}
		return maximum;

	}

}
