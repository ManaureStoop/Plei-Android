package com.arawaney.plei.db.provider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.arawaney.plei.db.CoverEntity;
import com.arawaney.plei.db.PleiProvider;
import com.arawaney.plei.model.Cover;
import com.arawaney.plei.util.CalendarUtil;

public class CoverProvider {
	private static final String LOG_TAG = "Plei-CoverProvider";

	public static final Uri URI_COVER = Uri.parse("content://"
			+ PleiProvider.PROVIDER_NAME + "/" + CoverEntity.TABLE);

	
	public static long insertCover(Context context, Cover cover) {

		if (context == null || cover == null)
			return -1;

		try {
			ContentValues values = new ContentValues();
			values.put(CoverEntity.COLUMN_SYSTEM_ID, cover.getSystem_id());
			values.put(CoverEntity.COLUMN_NAME, cover.getName());
			
			if (cover.getImageFile()!= null) {
					
				values.put(CoverEntity.COLUMN_IMAGE_FILE, cover.getImageFile());
			} else {
				Log.d(LOG_TAG, "ImageFile id null inserting: " + cover.getName());
			}


			if (cover.getUpdated_at() != null) {
				values.put(CoverEntity.COLUMN_UPDATED_AT, cover
						.getUpdated_at().getTimeInMillis());
			}else{
				Log.d(LOG_TAG, "Updated_At null inserting: " + cover.getName());

			}
			
			
			if (cover.getType()!= null) {
				
				values.put(CoverEntity.COLUMN_TYPE, cover.getType());
			} else {
				Log.d(LOG_TAG, "Type is null inserting: " + cover.getName());
			}
			
			if (cover.getCategoryId()!= null) {
				
				values.put(CoverEntity.COLUMN_CATEGORY_ID, cover.getCategoryId());
			} else {
				Log.d(LOG_TAG, "categoryId is null inserting: " + cover.getName());
			}
			
			if (cover.getPleilistId()!= null) {
				
				values.put(CoverEntity.COLUMN_PLEILIST_ID, cover.getPleilistId());
			} else {
				Log.d(LOG_TAG, "pleilistId is null inserting: " + cover.getName());
			}
			
			if (cover.getSection()!= -1) {
				
				values.put(CoverEntity.COLUMN_SECTION, cover.getSection());
			} else {
				Log.d(LOG_TAG, "section is null inserting: " + cover.getName());
			}
			

			final Uri result = context.getContentResolver().insert(URI_COVER,
					values);

			if (result != null) {
				long id = Long.parseLong(result.getPathSegments().get(1));
				if (id > 0) {
					Log.i(LOG_TAG, " Cover :" + cover.getName()
							+ " has bee inserted");
					return id;
				} else
					Log.e(LOG_TAG, " Cover :" + cover.getName()
							+ " has not bee inserted");

			}
		} catch (Exception e) {
			Log.e(LOG_TAG, " Cover :" + cover.getName()
					+ " has not bee inserted");
			e.printStackTrace();
		}
		return -1;

	}

	public static boolean updateCover(Context context, Cover cover) {

		if (context == null || cover == null)
			return false;

		try {
			ContentValues values = new ContentValues();
			values.put(CoverEntity.COLUMN_ID, cover.getId());
			values.put(CoverEntity.COLUMN_SYSTEM_ID, cover.getSystem_id());
			values.put(CoverEntity.COLUMN_NAME, cover.getName());
			if (cover.getImageFile() != null) {
					values.put(CoverEntity.COLUMN_IMAGE_FILE, cover.getImageFile());	
			} else {
				Log.d(LOG_TAG, "ImageFile id null inserting: " + cover.getName());
			}
			
			if (cover.getUpdated_at() != null) {
				values.put(CoverEntity.COLUMN_UPDATED_AT, cover
						.getUpdated_at().getTimeInMillis());
			}else{
				Log.d(LOG_TAG, "Updated_At null inserting: " + cover.getName());

			}
			
			
			if (cover.getType()!= null) {
				
				values.put(CoverEntity.COLUMN_TYPE, cover.getType());
			} else {
				Log.d(LOG_TAG, "Type is null inserting: " + cover.getName());
			}
			
			if (cover.getCategoryId()!= null) {
				
				values.put(CoverEntity.COLUMN_CATEGORY_ID, cover.getCategoryId());
			} else {
				Log.d(LOG_TAG, "categoryId is null inserting: " + cover.getName());
			}
			
			if (cover.getPleilistId()!= null) {
				
				values.put(CoverEntity.COLUMN_PLEILIST_ID, cover.getPleilistId());
			} else {
				Log.d(LOG_TAG, "pleilistId is null inserting: " + cover.getName());
			}
			
			if (cover.getSection()!= -1) {
				
				values.put(CoverEntity.COLUMN_SECTION, cover.getSection());
			} else {
				Log.d(LOG_TAG, "section is null inserting: " + cover.getName());
			}
			
			String condition = CoverEntity.COLUMN_SYSTEM_ID + " = " + "'"
					+ String.valueOf(cover.getSystem_id()) + "'";

			int row = context.getContentResolver().update(URI_COVER, values,
					condition, null);

			if (row == 1) {
				Log.i(LOG_TAG, " Cover :" + cover.getName()
						+ " has bee updated");
				return true;
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, " Cover :" + cover.getName()
					+ " has not bee updated" + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	public static Cover readCover(Context context, String coverID) {

		if (context == null)
			return null;

		String condition = CoverEntity.COLUMN_SYSTEM_ID + " = " + "'" + coverID
				+ "'";

		final Cursor cursor = context.getContentResolver().query(URI_COVER,
				null, condition, null, null);

		Cover cover = null;

		if (cursor.getCount() == 0) {
			cursor.close();
			return null;
		}

		try {
			if (cursor.moveToFirst()) {

				do {
					final long id = cursor.getLong(cursor
							.getColumnIndex(CoverEntity.COLUMN_ID));
					final String system_id = cursor.getString(cursor
							.getColumnIndex(CoverEntity.COLUMN_SYSTEM_ID));
					final String name = cursor.getString(cursor
							.getColumnIndex(CoverEntity.COLUMN_NAME));
					final long updated_at = cursor.getInt(cursor
							.getColumnIndex(CoverEntity.COLUMN_UPDATED_AT));
					final String imageFile = cursor.getString(cursor
							.getColumnIndex(CoverEntity.COLUMN_IMAGE_FILE));
					final String type = cursor.getString(cursor
							.getColumnIndex(CoverEntity.COLUMN_TYPE));
					final String categoryId = cursor.getString(cursor
							.getColumnIndex(CoverEntity.COLUMN_CATEGORY_ID));
					final String pleilistId = cursor.getString(cursor
							.getColumnIndex(CoverEntity.COLUMN_PLEILIST_ID));
					final int section = cursor.getInt(cursor
							.getColumnIndex(CoverEntity.COLUMN_SECTION));
					
					Calendar updatedAt = Calendar.getInstance();
					updatedAt.setTimeInMillis(updated_at);

					cover = new Cover();
					cover.setId(id);
					cover.setSystem_id(system_id);
					cover.setName(name);
				
					if (updatedAt != null) {
						cover.setUpdated_at(updatedAt);
					}
					if (imageFile != null) {
						cover.setImageFile(imageFile);
					}
					if (type != null) {
						cover.setType(type);
					}
					if (categoryId != null) {
						cover.setCategoryId(categoryId);
					}
					if (pleilistId != null) {
						cover.setPleilistId(pleilistId);
					}
					if (section != -1) {
						cover.setSection(section);
					}

				} while (cursor.moveToNext());
			}

		} catch (Exception e) {
			cover = null;
			Log.e(LOG_TAG, "Error : " + e.getMessage());
		} finally {
			cursor.close();
		}
		return cover;
	}

	public static ArrayList<Cover> readCovers(Context context) {

		if (context == null)
			return null;

		ArrayList<Cover> covers = new ArrayList<Cover>();

		final Cursor cursor = context.getContentResolver().query(URI_COVER,
				null, null, null, null);

		Cover cover = null;

		if (cursor.getCount() == 0) {
			cursor.close();
			return null;
		}

		try {
			if (cursor.moveToFirst()) {

				do {		final long id = cursor.getLong(cursor
						.getColumnIndex(CoverEntity.COLUMN_ID));
				final String system_id = cursor.getString(cursor
						.getColumnIndex(CoverEntity.COLUMN_SYSTEM_ID));
				final String name = cursor.getString(cursor
						.getColumnIndex(CoverEntity.COLUMN_NAME));
				final long updated_at = cursor.getInt(cursor
						.getColumnIndex(CoverEntity.COLUMN_UPDATED_AT));
				final String imageFile = cursor.getString(cursor
						.getColumnIndex(CoverEntity.COLUMN_IMAGE_FILE));
				final String type = cursor.getString(cursor
						.getColumnIndex(CoverEntity.COLUMN_TYPE));
				final String categoryId = cursor.getString(cursor
						.getColumnIndex(CoverEntity.COLUMN_CATEGORY_ID));
				final String pleilistId = cursor.getString(cursor
						.getColumnIndex(CoverEntity.COLUMN_PLEILIST_ID));
				final int section = cursor.getInt(cursor
						.getColumnIndex(CoverEntity.COLUMN_SECTION));
				
				Calendar updatedAt = Calendar.getInstance();
				updatedAt.setTimeInMillis(updated_at);

				cover = new Cover();
				cover.setId(id);
				cover.setSystem_id(system_id);
				cover.setName(name);
			
				if (updatedAt != null) {
					cover.setUpdated_at(updatedAt);
				}
				if (imageFile != null) {
					cover.setImageFile(imageFile);
				}
				if (type != null) {
					cover.setType(type);
				}
				if (categoryId != null) {
					cover.setCategoryId(categoryId);
				}
				if (pleilistId != null) {
					cover.setPleilistId(pleilistId);
				}
				if (section != -1) {
					cover.setSection(section);
				}

					covers.add(cover);

				} while (cursor.moveToNext());
			}

		} catch (Exception e) {
			covers = null;
			Log.e(LOG_TAG, "Error : " + e.getMessage());
		} finally {
			cursor.close();
		}
		return covers;
	}
	
	public static ArrayList<Cover> readCoversBySection(Context context, int targetSection) {

		if (context == null)
			return null;

		ArrayList<Cover> covers = new ArrayList<Cover>();
		
		String condition = CoverEntity.COLUMN_SECTION + " = " + "'" + targetSection
				+ "'";

		final Cursor cursor = context.getContentResolver().query(URI_COVER,
				null, condition, null, null);

		Cover cover = null;

		if (cursor.getCount() == 0) {
			cursor.close();
			return null;
		}

		try {
			if (cursor.moveToFirst()) {

				do {		final long id = cursor.getLong(cursor
						.getColumnIndex(CoverEntity.COLUMN_ID));
				final String system_id = cursor.getString(cursor
						.getColumnIndex(CoverEntity.COLUMN_SYSTEM_ID));
				final String name = cursor.getString(cursor
						.getColumnIndex(CoverEntity.COLUMN_NAME));
				final long updated_at = cursor.getInt(cursor
						.getColumnIndex(CoverEntity.COLUMN_UPDATED_AT));
				final String imageFile = cursor.getString(cursor
						.getColumnIndex(CoverEntity.COLUMN_IMAGE_FILE));
				final String type = cursor.getString(cursor
						.getColumnIndex(CoverEntity.COLUMN_TYPE));
				final String categoryId = cursor.getString(cursor
						.getColumnIndex(CoverEntity.COLUMN_CATEGORY_ID));
				final String pleilistId = cursor.getString(cursor
						.getColumnIndex(CoverEntity.COLUMN_PLEILIST_ID));
				final int section = cursor.getInt(cursor
						.getColumnIndex(CoverEntity.COLUMN_SECTION));
				
				Calendar updatedAt = Calendar.getInstance();
				updatedAt.setTimeInMillis(updated_at);

				cover = new Cover();
				cover.setId(id);
				cover.setSystem_id(system_id);
				cover.setName(name);
			
				if (updatedAt != null) {
					cover.setUpdated_at(updatedAt);
				}
				if (imageFile != null) {
					cover.setImageFile(imageFile);
				}
				if (type != null) {
					cover.setType(type);
				}
				if (categoryId != null) {
					cover.setCategoryId(categoryId);
				}
				if (pleilistId != null) {
					cover.setPleilistId(pleilistId);
				}
				if (section != -1) {
					cover.setSection(section);
				}

					covers.add(cover);

				} while (cursor.moveToNext());
			}

		} catch (Exception e) {
			covers = null;
			Log.e(LOG_TAG, "Error : " + e.getMessage());
		} finally {
			cursor.close();
		}
		return covers;
	}
	


	public static boolean removeCover(Context context, long coverId) {

		try {
			String condition = CoverEntity.COLUMN_ID + " = "
					+ String.valueOf(coverId);
			int rows = context.getContentResolver().delete(URI_COVER,
					condition, null);

			if (rows == 1) {
				Log.i(LOG_TAG, "Cover : " + coverId + "has been deleted");
				return true;
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, "Error deleting cover: " + e.getMessage());
		}
		return false;
	}

	public static Date getLastUpdate(Context context) {
		final Cursor cursor = context.getContentResolver().query(URI_COVER, null,
				null, null, CoverEntity.COLUMN_UPDATED_AT+" DESC");
		
		if (cursor.getCount() == 0) {
			cursor.close();
			return null;
		}

		try {
			if (cursor.moveToFirst()) {

					final long updated_at = cursor.getLong(cursor
							.getColumnIndex(CoverEntity.COLUMN_UPDATED_AT));
					Date date = new Date(updated_at);
					Log.d(LOG_TAG, "last update "+CalendarUtil.getDateFormated(date, "dd MM yyy mm:ss"));
					
			return date;		
			}

		} catch (Exception e) {
			Log.e(LOG_TAG, "Error : " + e.getMessage());
		} finally {
			cursor.close();
		}
		
		return null;
	}
	
	
}
