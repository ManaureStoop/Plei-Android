package com.arawaney.plei.db.provider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.arawaney.plei.db.PleilistEntity;
import com.arawaney.plei.db.PleiProvider;
import com.arawaney.plei.model.Pleilist;
import com.arawaney.plei.util.CalendarUtil;

public class PleilistProvider {
	private static final String LOG_TAG = "Plei-PleilistProvider";

	public static final Uri URI_PLEILIST = Uri.parse("content://"
			+ PleiProvider.PROVIDER_NAME + "/" + PleilistEntity.TABLE);

	public static long insertPleilist(Context context, Pleilist pleilist) {

		if (context == null || pleilist == null)
			return -1;

		try {
			ContentValues values = new ContentValues();
			values.put(PleilistEntity.COLUMN_SYSTEM_ID, pleilist.getSystem_id());
			values.put(PleilistEntity.COLUMN_NAME, pleilist.getName());

			if (pleilist.getImage() != null) {

				values.put(PleilistEntity.COLUMN_IMAGE, pleilist.getImage());
			} else {
				Log.d(LOG_TAG, "Image id null inserting: " + pleilist.getName());
			}

			if (pleilist.getUpdated_at() != null) {
				values.put(PleilistEntity.COLUMN_UPDATED_AT, pleilist
						.getUpdated_at().getTimeInMillis());
			} else {
				Log.d(LOG_TAG,
						"Updated_At null inserting: " + pleilist.getName());

			}

			if (pleilist.getCoverImage() != null) {

				values.put(PleilistEntity.COLUMN_COVER_IMAGE,
						pleilist.getCoverImage());
			} else {
				Log.d(LOG_TAG,
						"CoverImage is null inserting: " + pleilist.getName());
			}

			if (pleilist.getCategoryId() != null) {

				values.put(PleilistEntity.COLUMN_CATEGORY_ID,
						pleilist.getCategoryId());
			} else {
				Log.d(LOG_TAG,
						"categoryId is null inserting: " + pleilist.getName());
			}

			if (pleilist.getOrder() != -1) {

				values.put(PleilistEntity.COLUMN_CATEGORY_ORDER, pleilist.getOrder());
			} else {
				Log.d(LOG_TAG, "order is null inserting: " + pleilist.getName());
			}

			if (pleilist.getDeleted() != -1) {

				values.put(PleilistEntity.COLUMN_DELETED, pleilist.getDeleted());
			} else {
				Log.d(LOG_TAG,
						"deleted is null inserting: " + pleilist.getName());
			}

			if (pleilist.getFlaged() != -1) {

				values.put(PleilistEntity.COLUMN_FLAGED, pleilist.getFlaged());
			} else {
				Log.d(LOG_TAG,
						"flaged is null inserting: " + pleilist.getName());
			}
			
			if (pleilist.getFavorite() != -1) {

				values.put(PleilistEntity.COLUMN_FAVORITE, pleilist.getFavorite());
			} else {
				Log.d(LOG_TAG,
						"favorite is null inserting: " + pleilist.getName());
			}


			final Uri result = context.getContentResolver().insert(
					URI_PLEILIST, values);

			if (result != null) {
				long id = Long.parseLong(result.getPathSegments().get(1));
				if (id > 0) {
					Log.i(LOG_TAG, " Pleilist :" + pleilist.getName()
							+ " has bee inserted");
					return id;
				} else
					Log.e(LOG_TAG, " Pleilist :" + pleilist.getName()
							+ " has not bee inserted");

			}
		} catch (Exception e) {
			Log.e(LOG_TAG, " Pleilist :" + pleilist.getName()
					+ " has not bee inserted");
			e.printStackTrace();
		}
		return -1;

	}

	public static boolean updatePleilist(Context context, Pleilist pleilist) {

		if (context == null || pleilist == null)
			return false;

		try {
			ContentValues values = new ContentValues();
			values.put(PleilistEntity.COLUMN_ID, pleilist.getId());
			values.put(PleilistEntity.COLUMN_SYSTEM_ID, pleilist.getSystem_id());
			values.put(PleilistEntity.COLUMN_NAME, pleilist.getName());
			if (pleilist.getImage() != null) {
				values.put(PleilistEntity.COLUMN_IMAGE, pleilist.getImage());
			} else {
				Log.d(LOG_TAG, "Image id null inserting: " + pleilist.getName());
			}

			if (pleilist.getUpdated_at() != null) {
				values.put(PleilistEntity.COLUMN_UPDATED_AT, pleilist
						.getUpdated_at().getTimeInMillis());
			} else {
				Log.d(LOG_TAG,
						"Updated_At null inserting: " + pleilist.getName());

			}

			if (pleilist.getCoverImage() != null) {

				values.put(PleilistEntity.COLUMN_COVER_IMAGE,
						pleilist.getCoverImage());
			} else {
				Log.d(LOG_TAG,
						"CoverImage is null inserting: " + pleilist.getName());
			}

			if (pleilist.getCategoryId() != null) {

				values.put(PleilistEntity.COLUMN_CATEGORY_ID,
						pleilist.getCategoryId());
			} else {
				Log.d(LOG_TAG,
						"categoryId is null inserting: " + pleilist.getName());
			}

			if (pleilist.getOrder() != -1) {

				values.put(PleilistEntity.COLUMN_CATEGORY_ORDER, pleilist.getOrder());
			} else {
				Log.d(LOG_TAG, "order is null inserting: " + pleilist.getName());
			}

			if (pleilist.getDeleted() != -1) {

				values.put(PleilistEntity.COLUMN_DELETED, pleilist.getDeleted());
			} else {
				Log.d(LOG_TAG,
						"deleted is null inserting: " + pleilist.getName());
			}

			if (pleilist.getFlaged() != -1) {

				values.put(PleilistEntity.COLUMN_FLAGED, pleilist.getFlaged());
			} else {
				Log.d(LOG_TAG,
						"flaged is null inserting: " + pleilist.getName());
			}
			
			if (pleilist.getFavorite() != -1) {

				values.put(PleilistEntity.COLUMN_FAVORITE, pleilist.getFavorite());
			} else {
				Log.d(LOG_TAG,
						"favorite is null inserting: " + pleilist.getName());
			}

			String condition = PleilistEntity.COLUMN_SYSTEM_ID + " = " + "'"
					+ String.valueOf(pleilist.getSystem_id()) + "'";

			int row = context.getContentResolver().update(URI_PLEILIST, values,
					condition, null);

			if (row == 1) {
				Log.i(LOG_TAG, " Pleilist :" + pleilist.getName()
						+ " has bee updated");
				return true;
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, " Pleilist :" + pleilist.getName()
					+ " has not bee updated" + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	public static Pleilist readPleilist(Context context, String pleilistID) {

		if (context == null)
			return null;

		String condition = PleilistEntity.COLUMN_SYSTEM_ID + " = " + "'"
				+ pleilistID + "'";

		final Cursor cursor = context.getContentResolver().query(URI_PLEILIST,
				null, condition, null, null);

		Pleilist pleilist = null;

		if (cursor.getCount() == 0) {
			cursor.close();
			return null;
		}

		try {
			if (cursor.moveToFirst()) {

				do {
					final long id = cursor.getLong(cursor
							.getColumnIndex(PleilistEntity.COLUMN_ID));
					final String system_id = cursor.getString(cursor
							.getColumnIndex(PleilistEntity.COLUMN_SYSTEM_ID));
					final String name = cursor.getString(cursor
							.getColumnIndex(PleilistEntity.COLUMN_NAME));
					final long updated_at = cursor.getInt(cursor
							.getColumnIndex(PleilistEntity.COLUMN_UPDATED_AT));
					final String imageFile = cursor.getString(cursor
							.getColumnIndex(PleilistEntity.COLUMN_IMAGE));
					final String coverImage = cursor.getString(cursor
							.getColumnIndex(PleilistEntity.COLUMN_COVER_IMAGE));
					final String categoryId = cursor.getString(cursor
							.getColumnIndex(PleilistEntity.COLUMN_CATEGORY_ID));
					final int order = cursor.getInt(cursor
							.getColumnIndex(PleilistEntity.COLUMN_CATEGORY_ORDER));
					final int deleted = cursor.getInt(cursor
							.getColumnIndex(PleilistEntity.COLUMN_DELETED));
					final int flaged = cursor.getInt(cursor
							.getColumnIndex(PleilistEntity.COLUMN_FLAGED));
					final int favorite = cursor.getInt(cursor
							.getColumnIndex(PleilistEntity.COLUMN_FAVORITE));

					Calendar updatedAt = Calendar.getInstance();
					updatedAt.setTimeInMillis(updated_at);

					pleilist = new Pleilist();
					pleilist.setId(id);
					pleilist.setSystem_id(system_id);
					pleilist.setName(name);

					if (updatedAt != null) {
						pleilist.setUpdated_at(updatedAt);
					}
					if (imageFile != null) {
						pleilist.setImage(imageFile);
					}
					if (coverImage != null) {
						pleilist.setCoverImage(coverImage);
					}
					if (categoryId != null) {
						pleilist.setCategoryId(categoryId);
					}
					if (order != -1) {
						pleilist.setOrder(order);
					}
					if (deleted != -1) {
						pleilist.setDeleted(deleted);
					}
					if (flaged != -1) {
						pleilist.setFlaged(flaged);
					}	
					if (favorite != -1) {
						pleilist.setFavorite(favorite);
					}

				} while (cursor.moveToNext());
			}

		} catch (Exception e) {
			pleilist = null;
			Log.e(LOG_TAG, "Error : " + e.getMessage());
		} finally {
			cursor.close();
		}
		return pleilist;
	}

	public static ArrayList<Pleilist> readPleilists(Context context) {

		if (context == null)
			return null;

		ArrayList<Pleilist> pleilists = new ArrayList<Pleilist>();

		final Cursor cursor = context.getContentResolver().query(URI_PLEILIST,
				null, null, null, null);

		Pleilist pleilist = null;

		if (cursor.getCount() == 0) {
			cursor.close();
			return null;
		}

		try {
			if (cursor.moveToFirst()) {

				do {
					final long id = cursor.getLong(cursor
							.getColumnIndex(PleilistEntity.COLUMN_ID));
					final String system_id = cursor.getString(cursor
							.getColumnIndex(PleilistEntity.COLUMN_SYSTEM_ID));
					final String name = cursor.getString(cursor
							.getColumnIndex(PleilistEntity.COLUMN_NAME));
					final long updated_at = cursor.getInt(cursor
							.getColumnIndex(PleilistEntity.COLUMN_UPDATED_AT));
					final String imageFile = cursor.getString(cursor
							.getColumnIndex(PleilistEntity.COLUMN_IMAGE));
					final String coverImage = cursor.getString(cursor
							.getColumnIndex(PleilistEntity.COLUMN_COVER_IMAGE));
					final String categoryId = cursor.getString(cursor
							.getColumnIndex(PleilistEntity.COLUMN_CATEGORY_ID));
					final int order = cursor.getInt(cursor
							.getColumnIndex(PleilistEntity.COLUMN_CATEGORY_ORDER));
					final int deleted = cursor.getInt(cursor
							.getColumnIndex(PleilistEntity.COLUMN_DELETED));
					final int flaged = cursor.getInt(cursor
							.getColumnIndex(PleilistEntity.COLUMN_FLAGED));
					final int favorite = cursor.getInt(cursor
							.getColumnIndex(PleilistEntity.COLUMN_FAVORITE));

					Calendar updatedAt = Calendar.getInstance();
					updatedAt.setTimeInMillis(updated_at);

					pleilist = new Pleilist();
					pleilist.setId(id);
					pleilist.setSystem_id(system_id);
					pleilist.setName(name);

					if (updatedAt != null) {
						pleilist.setUpdated_at(updatedAt);
					}
					if (imageFile != null) {
						pleilist.setImage(imageFile);
					}
					if (coverImage != null) {
						pleilist.setCoverImage(coverImage);
					}
					if (categoryId != null) {
						pleilist.setCategoryId(categoryId);
					}
					if (order != -1) {
						pleilist.setOrder(order);
					}
					if (deleted != -1) {
						pleilist.setDeleted(deleted);
					}
					if (flaged != -1) {
						pleilist.setFlaged(flaged);
					}	
					if (favorite != -1) {
						pleilist.setFavorite(favorite);
					}

					pleilists.add(pleilist);

				} while (cursor.moveToNext());
			}

		} catch (Exception e) {
			pleilists = null;
			Log.e(LOG_TAG, "Error : " + e.getMessage());
		} finally {
			cursor.close();
		}
		return pleilists;
	}

	public static ArrayList<Pleilist> readPleilistsByCategory(Context context,
			String targetCategory) {

		if (context == null)
			return null;

		ArrayList<Pleilist> pleilists = new ArrayList<Pleilist>();

		String condition = PleilistEntity.COLUMN_CATEGORY_ID + " = " + "'"
				+ targetCategory + "'";

		final Cursor cursor = context.getContentResolver().query(URI_PLEILIST,
				null, condition, null, null);

		Pleilist pleilist = null;

		if (cursor.getCount() == 0) {
			cursor.close();
			return null;
		}

		try {
			if (cursor.moveToFirst()) {

				do {
					final long id = cursor.getLong(cursor
							.getColumnIndex(PleilistEntity.COLUMN_ID));
					final String system_id = cursor.getString(cursor
							.getColumnIndex(PleilistEntity.COLUMN_SYSTEM_ID));
					final String name = cursor.getString(cursor
							.getColumnIndex(PleilistEntity.COLUMN_NAME));
					final long updated_at = cursor.getInt(cursor
							.getColumnIndex(PleilistEntity.COLUMN_UPDATED_AT));
					final String imageFile = cursor.getString(cursor
							.getColumnIndex(PleilistEntity.COLUMN_IMAGE));
					final String coverImage = cursor.getString(cursor
							.getColumnIndex(PleilistEntity.COLUMN_COVER_IMAGE));
					final String categoryId = cursor.getString(cursor
							.getColumnIndex(PleilistEntity.COLUMN_CATEGORY_ID));
					final int order = cursor.getInt(cursor
							.getColumnIndex(PleilistEntity.COLUMN_CATEGORY_ORDER));
					final int deleted = cursor.getInt(cursor
							.getColumnIndex(PleilistEntity.COLUMN_DELETED));
					final int flaged = cursor.getInt(cursor
							.getColumnIndex(PleilistEntity.COLUMN_FLAGED));
					final int favorite = cursor.getInt(cursor
							.getColumnIndex(PleilistEntity.COLUMN_FAVORITE));

					Calendar updatedAt = Calendar.getInstance();
					updatedAt.setTimeInMillis(updated_at);

					pleilist = new Pleilist();
					pleilist.setId(id);
					pleilist.setSystem_id(system_id);
					pleilist.setName(name);

					if (updatedAt != null) {
						pleilist.setUpdated_at(updatedAt);
					}
					if (imageFile != null) {
						pleilist.setImage(imageFile);
					}
					if (coverImage != null) {
						pleilist.setCoverImage(coverImage);
					}
					if (categoryId != null) {
						pleilist.setCategoryId(categoryId);
					}
					if (order != -1) {
						pleilist.setOrder(order);
					}
					if (deleted != -1) {
						pleilist.setDeleted(deleted);
					}
					if (flaged != -1) {
						pleilist.setFlaged(flaged);
					}	
					if (favorite != -1) {
						pleilist.setFavorite(favorite);
					}

					pleilists.add(pleilist);

				} while (cursor.moveToNext());
			}

		} catch (Exception e) {
			pleilists = null;
			Log.e(LOG_TAG, "Error : " + e.getMessage());
		} finally {
			cursor.close();
		}
		return pleilists;
	}

	public static boolean removePleilist(Context context, long pleilistId) {

		try {
			String condition = PleilistEntity.COLUMN_ID + " = "
					+ String.valueOf(pleilistId);
			int rows = context.getContentResolver().delete(URI_PLEILIST,
					condition, null);

			if (rows == 1) {
				Log.i(LOG_TAG, "Pleilist : " + pleilistId + "has been deleted");
				return true;
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, "Error deleting pleilist: " + e.getMessage());
		}
		return false;
	}

	public static Date getLastUpdate(Context context) {
		final Cursor cursor = context.getContentResolver().query(URI_PLEILIST,
				null, null, null, PleilistEntity.COLUMN_UPDATED_AT + " DESC");

		if (cursor.getCount() == 0) {
			cursor.close();
			return null;
		}

		try {
			if (cursor.moveToFirst()) {

				final long updated_at = cursor.getLong(cursor
						.getColumnIndex(PleilistEntity.COLUMN_UPDATED_AT));
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
	
	public static ArrayList<Pleilist> getFavoritesPleiLists(Context context) {

		if (context == null)
			return null;

		ArrayList<Pleilist> pleilists = new ArrayList<Pleilist>();

		String condition = PleilistEntity.COLUMN_FAVORITE + " = " + "'"
				+ Pleilist.FAVORITE + "'";

		final Cursor cursor = context.getContentResolver().query(URI_PLEILIST,
				null, condition, null, null);

		Pleilist pleilist = null;

		if (cursor.getCount() == 0) {
			cursor.close();
			return null;
		}

		try {
			if (cursor.moveToFirst()) {

				do {
					final long id = cursor.getLong(cursor
							.getColumnIndex(PleilistEntity.COLUMN_ID));
					final String system_id = cursor.getString(cursor
							.getColumnIndex(PleilistEntity.COLUMN_SYSTEM_ID));
					final String name = cursor.getString(cursor
							.getColumnIndex(PleilistEntity.COLUMN_NAME));
					final long updated_at = cursor.getInt(cursor
							.getColumnIndex(PleilistEntity.COLUMN_UPDATED_AT));
					final String imageFile = cursor.getString(cursor
							.getColumnIndex(PleilistEntity.COLUMN_IMAGE));
					final String coverImage = cursor.getString(cursor
							.getColumnIndex(PleilistEntity.COLUMN_COVER_IMAGE));
					final String categoryId = cursor.getString(cursor
							.getColumnIndex(PleilistEntity.COLUMN_CATEGORY_ID));
					final int order = cursor.getInt(cursor
							.getColumnIndex(PleilistEntity.COLUMN_CATEGORY_ORDER));
					final int deleted = cursor.getInt(cursor
							.getColumnIndex(PleilistEntity.COLUMN_DELETED));
					final int flaged = cursor.getInt(cursor
							.getColumnIndex(PleilistEntity.COLUMN_FLAGED));
					final int favorite = cursor.getInt(cursor
							.getColumnIndex(PleilistEntity.COLUMN_FAVORITE));

					Calendar updatedAt = Calendar.getInstance();
					updatedAt.setTimeInMillis(updated_at);

					pleilist = new Pleilist();
					pleilist.setId(id);
					pleilist.setSystem_id(system_id);
					pleilist.setName(name);

					if (updatedAt != null) {
						pleilist.setUpdated_at(updatedAt);
					}
					if (imageFile != null) {
						pleilist.setImage(imageFile);
					}
					if (coverImage != null) {
						pleilist.setCoverImage(coverImage);
					}
					if (categoryId != null) {
						pleilist.setCategoryId(categoryId);
					}
					if (order != -1) {
						pleilist.setOrder(order);
					}
					if (deleted != -1) {
						pleilist.setDeleted(deleted);
					}
					if (flaged != -1) {
						pleilist.setFlaged(flaged);
					}	
					if (favorite != -1) {
						pleilist.setFavorite(favorite);
					}

					pleilists.add(pleilist);

				} while (cursor.moveToNext());
			}

		} catch (Exception e) {
			pleilists = null;
			Log.e(LOG_TAG, "Error : " + e.getMessage());
		} finally {
			cursor.close();
		}
		return pleilists;
	}

}
