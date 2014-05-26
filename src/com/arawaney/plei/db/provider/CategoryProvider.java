package com.arawaney.plei.db.provider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.View.OnClickListener;

import com.arawaney.plei.db.CategoryEntity;
import com.arawaney.plei.db.PleiProvider;
import com.arawaney.plei.model.Category;
import com.arawaney.plei.util.CalendarUtil;

public class CategoryProvider {
	private static final String LOG_TAG = "Plei-CategoryProvider";

	public static final Uri URI_CATEGORY = Uri.parse("content://"
			+ PleiProvider.PROVIDER_NAME + "/" + CategoryEntity.TABLE);

	
	public static long insertCategory(Context context, Category category) {

		if (context == null || category == null)
			return -1;

		try {
			ContentValues values = new ContentValues();
			values.put(CategoryEntity.COLUMN_SYSTEM_ID, category.getSystem_id());
			values.put(CategoryEntity.COLUMN_NAME, category.getName());
			if (category.getImageFile()!= null) {
					
				values.put(CategoryEntity.COLUMN_IMAGE_FILE, category.getImageFile());
			} else {
				Log.d(LOG_TAG, "ImageFile id null inserting: " + category.getName());
			}

			if (category.getUpdated_at() != null) {
				values.put(CategoryEntity.COLUMN_UPDATED_AT, category
						.getUpdated_at().getTimeInMillis());
			}else{
				Log.d(LOG_TAG, "Updated_At null inserting: " + category.getName());

			}
			

			final Uri result = context.getContentResolver().insert(URI_CATEGORY,
					values);

			if (result != null) {
				long id = Long.parseLong(result.getPathSegments().get(1));
				if (id > 0) {
					Log.i(LOG_TAG, " Category :" + category.getName()
							+ " has bee inserted");
					return id;
				} else
					Log.e(LOG_TAG, " Category :" + category.getName()
							+ " has not bee inserted");

			}
		} catch (Exception e) {
			Log.e(LOG_TAG, " Category :" + category.getName()
					+ " has not bee inserted");
			e.printStackTrace();
		}
		return -1;

	}

	public static boolean updateCategory(Context context, Category category) {

		if (context == null || category == null)
			return false;

		try {
			ContentValues values = new ContentValues();
			values.put(CategoryEntity.COLUMN_ID, category.getId());
			values.put(CategoryEntity.COLUMN_SYSTEM_ID, category.getSystem_id());
			values.put(CategoryEntity.COLUMN_NAME, category.getName());
			if (category.getImageFile() != null) {
					values.put(CategoryEntity.COLUMN_IMAGE_FILE, category.getImageFile());	
			} else {
				Log.d(LOG_TAG, "ImageFile id null inserting: " + category.getName());
			}
			
			if (category.getUpdated_at() != null) {
				values.put(CategoryEntity.COLUMN_UPDATED_AT, category
						.getUpdated_at().getTimeInMillis());
			}else{
				Log.d(LOG_TAG, "Updated_At null inserting: " + category.getName());

			}
			String condition = CategoryEntity.COLUMN_SYSTEM_ID + " = " + "'"
					+ String.valueOf(category.getSystem_id()) + "'";

			int row = context.getContentResolver().update(URI_CATEGORY, values,
					condition, null);

			if (row == 1) {
				Log.i(LOG_TAG, " Category :" + category.getName()
						+ " has bee updated");
				return true;
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, " Category :" + category.getName()
					+ " has not bee updated" + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	public static Category readCategory(Context context, String categoryID) {

		if (context == null)
			return null;

		String condition = CategoryEntity.COLUMN_SYSTEM_ID + " = " + "'" + categoryID
				+ "'";

		final Cursor cursor = context.getContentResolver().query(URI_CATEGORY,
				null, condition, null, null);

		Category category = null;

		if (cursor.getCount() == 0) {
			cursor.close();
			return null;
		}

		try {
			if (cursor.moveToFirst()) {

				do {
					final long id = cursor.getLong(cursor
							.getColumnIndex(CategoryEntity.COLUMN_ID));
					final String system_id = cursor.getString(cursor
							.getColumnIndex(CategoryEntity.COLUMN_SYSTEM_ID));
					final String name = cursor.getString(cursor
							.getColumnIndex(CategoryEntity.COLUMN_NAME));
					final long updated_at = cursor.getInt(cursor
							.getColumnIndex(CategoryEntity.COLUMN_UPDATED_AT));
					final String imageFile = cursor.getString(cursor
							.getColumnIndex(CategoryEntity.COLUMN_IMAGE_FILE));
					
					Calendar updatedAt = Calendar.getInstance();
					updatedAt.setTimeInMillis(updated_at);

					category = new Category();
					category.setId(id);
					category.setSystem_id(system_id);
					category.setName(name);
				
					if (updatedAt != null) {
						category.setUpdated_at(updatedAt);
					}
					if (imageFile != null) {
						category.setImageFile(imageFile);
					}

				} while (cursor.moveToNext());
			}

		} catch (Exception e) {
			category = null;
			Log.e(LOG_TAG, "Error : " + e.getMessage());
		} finally {
			cursor.close();
		}
		return category;
	}

	public static ArrayList<Category> readCategorys(Context context) {

		if (context == null)
			return null;

		ArrayList<Category> categorys = new ArrayList<Category>();

		final Cursor cursor = context.getContentResolver().query(URI_CATEGORY,
				null, null, null, null);

		Category category = null;

		if (cursor.getCount() == 0) {
			cursor.close();
			return null;
		}

		try {
			if (cursor.moveToFirst()) {

				do {

					final long id = cursor.getLong(cursor
							.getColumnIndex(CategoryEntity.COLUMN_ID));
					final String system_id = cursor.getString(cursor
							.getColumnIndex(CategoryEntity.COLUMN_SYSTEM_ID));
					final String name = cursor.getString(cursor
							.getColumnIndex(CategoryEntity.COLUMN_NAME));
					final long updated_at = cursor.getInt(cursor
							.getColumnIndex(CategoryEntity.COLUMN_UPDATED_AT));
					final String imageFile = cursor.getString(cursor
							.getColumnIndex(CategoryEntity.COLUMN_IMAGE_FILE));
					
					Calendar updatedAt = Calendar.getInstance();
					updatedAt.setTimeInMillis(updated_at);

					category = new Category();
					category.setId(id);
					category.setSystem_id(system_id);
					category.setName(name);

					if (updatedAt != null) {
						category.setUpdated_at(updatedAt);
					}
					if (imageFile != null) {
						category.setImageFile(imageFile);
					}

					categorys.add(category);

				} while (cursor.moveToNext());
			}

		} catch (Exception e) {
			categorys = null;
			Log.e(LOG_TAG, "Error : " + e.getMessage());
		} finally {
			cursor.close();
		}
		return categorys;
	}
	

	public static boolean removeCategory(Context context, long categoryId) {

		try {
			String condition = CategoryEntity.COLUMN_ID + " = "
					+ String.valueOf(categoryId);
			int rows = context.getContentResolver().delete(URI_CATEGORY,
					condition, null);

			if (rows == 1) {
				Log.i(LOG_TAG, "Category : " + categoryId + "has been deleted");
				return true;
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, "Error deleting category: " + e.getMessage());
		}
		return false;
	}

	public static Date getLastUpdate(Context context) {
		final Cursor cursor = context.getContentResolver().query(URI_CATEGORY, null,
				null, null, CategoryEntity.COLUMN_UPDATED_AT+" DESC");
		
		if (cursor.getCount() == 0) {
			cursor.close();
			return null;
		}

		try {
			if (cursor.moveToFirst()) {

					final long updated_at = cursor.getLong(cursor
							.getColumnIndex(CategoryEntity.COLUMN_UPDATED_AT));
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
