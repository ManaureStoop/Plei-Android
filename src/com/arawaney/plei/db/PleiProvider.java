package com.arawaney.plei.db;

import java.sql.SQLException;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class PleiProvider extends ContentProvider {

	private static final String LOG_TAG = "Plei-Provider";
	public static final String PROVIDER_NAME = "com.arawaney.plei.db.contentprovider";

	// Identifiers

	private static final int CATEGORIES = 1;
	private static final int CATEGORY_ID = 2;
	private static final int COVERS = 3;
	private static final int COVER_ID = 4;
	private static final int PLEILISTS = 5;
	private static final int PLEILIST_ID = 6;
	private static final int TRACKS = 7;
	private static final int TRACK_ID = 8;
	private static final int PLEILIST_TRACKS = 9;
	private static final int PLEILIST_TRACK_ID = 10;


	// URIs

	private static final String CONTENT_CATEGORY = "content://" + PROVIDER_NAME
			+ "/" + CategoryEntity.TABLE;
	public static final Uri URI_CATEGORY = Uri.parse(CONTENT_CATEGORY);

	private static final String CONTENT_COVER = "content://" + PROVIDER_NAME
			+ "/" + CoverEntity.TABLE;
	public static final Uri URI_COVER = Uri.parse(CONTENT_COVER);

	private static final String CONTENT_PLEILIST = "content://" + PROVIDER_NAME
			+ "/" + PleilistEntity.TABLE;
	public static final Uri URI_PLEILIST = Uri.parse(CONTENT_PLEILIST);
	
	private static final String CONTENT_TRACK = "content://" + PROVIDER_NAME
			+ "/" + TrackEntity.TABLE;
	public static final Uri URI_TRACK = Uri.parse(CONTENT_TRACK);
	
	private static final String CONTENT_PLEILIST_TRACK = "content://" + PROVIDER_NAME
			+ "/" + PleilistTrackEntity.TABLE;
	public static final Uri URI_PLEILIST_TRACK = Uri.parse(CONTENT_PLEILIST_TRACK);

	// Content Types

	private static final String TYPE_CATEGORY_ITEM = "android.cursor.item/vnd."
			+ PROVIDER_NAME + "." + CategoryEntity.TABLE;
	private static final String TYPE_CATEGORY_ITEMS = "android.cursor.item/vnd."
			+ PROVIDER_NAME + "." + CategoryEntity.TABLE;
	private static final String TYPE_COVER_ITEM = "android.cursor.item/vnd."
			+ PROVIDER_NAME + "." + CoverEntity.TABLE;
	private static final String TYPE_COVER_ITEMS = "android.cursor.item/vnd."
			+ PROVIDER_NAME + "." + CoverEntity.TABLE;
	private static final String TYPE_PLEILIST_ITEM = "android.cursor.item/vnd."
			+ PROVIDER_NAME + "." + PleilistEntity.TABLE;
	private static final String TYPE_PLEILIST_ITEMS = "android.cursor.item/vnd."
			+ PROVIDER_NAME + "." + PleilistEntity.TABLE;
	private static final String TYPE_TRACK_ITEM = "android.cursor.item/vnd."
			+ PROVIDER_NAME + "." + TrackEntity.TABLE;
	private static final String TYPE_TRACK_ITEMS = "android.cursor.item/vnd."
			+ PROVIDER_NAME + "." + TrackEntity.TABLE;
	private static final String TYPE_PLEILIST_TRACK_ITEM = "android.cursor.item/vnd."
			+ PROVIDER_NAME + "." + PleilistTrackEntity.TABLE;
	private static final String TYPE_PLEILIST_TRACK_ITEMS = "android.cursor.item/vnd."
			+ PROVIDER_NAME + "." + PleilistTrackEntity.TABLE;

	private static final UriMatcher uriMatcher;

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

		uriMatcher.addURI(PROVIDER_NAME, CategoryEntity.TABLE, CATEGORIES);
		uriMatcher.addURI(PROVIDER_NAME, CategoryEntity.TABLE + "/#", CATEGORY_ID);

		uriMatcher.addURI(PROVIDER_NAME, CoverEntity.TABLE, COVERS);
		uriMatcher.addURI(PROVIDER_NAME, CoverEntity.TABLE + "/#", COVER_ID);

		uriMatcher.addURI(PROVIDER_NAME, PleilistEntity.TABLE, PLEILISTS);
		uriMatcher
				.addURI(PROVIDER_NAME, PleilistEntity.TABLE + "/#", PLEILIST_ID);
		
		uriMatcher.addURI(PROVIDER_NAME, TrackEntity.TABLE, TRACKS);
		uriMatcher
				.addURI(PROVIDER_NAME, TrackEntity.TABLE + "/#", TRACK_ID);
		
		uriMatcher.addURI(PROVIDER_NAME, PleilistTrackEntity.TABLE, PLEILIST_TRACKS);
		uriMatcher
				.addURI(PROVIDER_NAME, PleilistTrackEntity.TABLE + "/#", PLEILIST_TRACK_ID);

	}

	public static final String DATABASE_NAME = "tumascotik_client_db";
	private SQLiteDatabase db;
	private DataBaseHelper dbHelper;
	private int dataBaseVersion = 1;

	@Override
	public boolean onCreate() {
		Log.d(LOG_TAG, "- on create");
		return createDataBaseHelper();
	}

	private boolean createDataBaseHelper() {
		Context context = getContext();

		try {
			Log.i(LOG_TAG, "Conten Provider - Database verion: "
					+ dataBaseVersion);
			dbHelper = new DataBaseHelper(context, DATABASE_NAME, null,
					dataBaseVersion);
			return true;

		} catch (Exception e) {
			Log.e(LOG_TAG, "Error: " + e.getMessage());
		}
		return false;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case CATEGORIES:
			return TYPE_CATEGORY_ITEMS;
		case CATEGORY_ID:
			return TYPE_CATEGORY_ITEM;
		case COVERS:
			return TYPE_COVER_ITEMS;
		case COVER_ID:
			return TYPE_COVER_ITEM;
		case PLEILISTS:
			return TYPE_PLEILIST_ITEMS;
		case PLEILIST_ID:
			return TYPE_PLEILIST_ITEM;
			
		case TRACKS:
			return TYPE_TRACK_ITEMS;
		case TRACK_ID:
			return TYPE_TRACK_ITEM;
		case PLEILIST_TRACKS:
			return TYPE_PLEILIST_TRACK_ITEMS;
		case PLEILIST_TRACK_ID:
			return TYPE_PLEILIST_TRACK_ITEM;
		default:
			throw new IllegalArgumentException("Unsupported UR" + uri);
		}

	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		String tableName = null;
		Uri target = null;

		switch (uriMatcher.match(uri)) {
		case CATEGORIES:
			tableName = CategoryEntity.TABLE;
			target = URI_CATEGORY;
			break;

		case COVERS:
			tableName = CoverEntity.TABLE;
			target = URI_COVER;
			break;

		case PLEILISTS:
			tableName = PleilistEntity.TABLE;
			target = URI_PLEILIST;
			break;

		case TRACKS:
			tableName = TrackEntity.TABLE;
			target = URI_TRACK;
			break;
			
		case PLEILIST_TRACKS:
			tableName = PleilistTrackEntity.TABLE;
			target = URI_PLEILIST_TRACK;
			break;
			
		default:
			throw new IllegalArgumentException("Unsupported UR" + uri);
		}

		if (tableName != null && target != null) {
			try {
				return insert(uri, values, tableName, target);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	private Uri insert(Uri uri, ContentValues values, String tableName,
			Uri target) throws SQLException {
		Log.d(LOG_TAG, " - insert :" + uri);

		if (dbHelper == null) {
			createDataBaseHelper();
		}

		db = dbHelper.getWritableDatabase();
		dbHelper.onCreate(db);
		dbHelper.onUpgrade(db, dataBaseVersion,
				DataBaseHelper.getVersionAvailable());

		// add item
		long rowID = db.insert(tableName, "", values);

		if (rowID > 0) {
			// added successfully
			Uri itemUri = ContentUris.withAppendedId(target, rowID);
			getContext().getContentResolver().notifyChange(itemUri, null);
			return itemUri;
		}
		throw new SQLException("Failed to insert row into " + uri + " into "
				+ tableName);

	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		String tableName = null;
		Uri target = null;
		boolean single = false;

		switch (uriMatcher.match(uri)) {
		case CATEGORIES:
			tableName = CategoryEntity.TABLE;
			target = URI_CATEGORY;
			break;

		case CATEGORY_ID:
			tableName = CategoryEntity.TABLE;
			target = URI_CATEGORY;
			single = true;
			break;

		case COVERS:
			tableName = CoverEntity.TABLE;
			target = URI_COVER;
			break;

		case COVER_ID:
			tableName = CoverEntity.TABLE;
			target = URI_COVER;
			single = true;
			break;

		case PLEILISTS:
			tableName = PleilistEntity.TABLE;
			target = URI_PLEILIST;
			break;

		case PLEILIST_ID:
			tableName = PleilistEntity.TABLE;
			target = URI_PLEILIST;
			single = true;
			break;
			
		case TRACKS:
			tableName = TrackEntity.TABLE;
			target = URI_TRACK;
			break;
			
		case TRACK_ID:
			tableName = TrackEntity.TABLE;
			target = URI_TRACK;
			single = true;
			break;
			
		case PLEILIST_TRACKS:
			tableName = PleilistTrackEntity.TABLE;
			target = URI_PLEILIST_TRACK;
			break;
			
		case PLEILIST_TRACK_ID:
			tableName = PleilistTrackEntity.TABLE;
			target = URI_PLEILIST_TRACK;
			single = true;
			break;
			

		default:
			throw new IllegalArgumentException("Unsupported UR" + uri);
		}

		if (tableName != null && target != null) {

			return query(uri, tableName, single, projection, selection,
					selectionArgs, sortOrder);

		}
		return null;
	}

	private Cursor query(Uri uri, String tableName, boolean single,
			String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		Log.d(LOG_TAG, " - query :" + uri + " Table;" + tableName);

		if (dbHelper == null) {
			createDataBaseHelper();
		}

		db = dbHelper.getWritableDatabase();
		dbHelper.onCreate(db);
		dbHelper.onUpgrade(db, dataBaseVersion,
				DataBaseHelper.getVersionAvailable());

		SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
		sqlBuilder.setTables(tableName);

		if (single) {
			sqlBuilder.appendWhere("id" + "=" + uri.getPathSegments().get(1));
		}

		Cursor c = sqlBuilder.query(db, projection, selection, selectionArgs,
				null, null, sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;

	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		Log.d(LOG_TAG, " - update :" + uri);

		if (dbHelper == null) {
			createDataBaseHelper();
		}

		db = dbHelper.getWritableDatabase();
		dbHelper.onCreate(db);
		dbHelper.onUpgrade(db, dataBaseVersion,
				DataBaseHelper.getVersionAvailable());

		int count = 0;

		switch (uriMatcher.match(uri)) {

		case CATEGORIES:
			count = db.update(CategoryEntity.TABLE, values, selection,
					selectionArgs);
			break;

		case CATEGORY_ID:
			selection = CategoryEntity.COLUMN_ID
					+ " = "
					+ uri.getPathSegments().get(1)
					+ (!TextUtils.isEmpty(selection) ? "AND (" + selection
							+ ')' : "");
			count = db.update(CategoryEntity.TABLE, values, selection,
					selectionArgs);
			break;

		case COVERS:
			count = db
					.update(CoverEntity.TABLE, values, selection, selectionArgs);
			break;

		case COVER_ID:
			selection = CoverEntity.COLUMN_ID
					+ " = "
					+ uri.getPathSegments().get(1)
					+ (!TextUtils.isEmpty(selection) ? "AND (" + selection
							+ ')' : "");
			count = db.update(CoverEntity.TABLE, values, selection,
					selectionArgs);
			break;

		case PLEILISTS:
			count = db.update(PleilistEntity.TABLE, values, selection,
					selectionArgs);
			break;

		case PLEILIST_ID:
			selection = PleilistEntity.COLUMN_ID
					+ " = "
					+ uri.getPathSegments().get(1)
					+ (!TextUtils.isEmpty(selection) ? "AND (" + selection
							+ ')' : "");
			count = db.update(PleilistEntity.TABLE, values, selection,
					selectionArgs);
			break;
			
		case TRACKS:
			count = db.update(TrackEntity.TABLE, values, selection,
					selectionArgs);
			break;

		case TRACK_ID:
			selection = TrackEntity.COLUMN_ID
					+ " = "
					+ uri.getPathSegments().get(1)
					+ (!TextUtils.isEmpty(selection) ? "AND (" + selection
							+ ')' : "");
			count = db.update(PleilistEntity.TABLE, values, selection,
					selectionArgs);
			break;
			
		case PLEILIST_TRACKS:
			count = db.update(PleilistTrackEntity.TABLE, values, selection,
					selectionArgs);
			break;

		case PLEILIST_TRACK_ID:
			selection = PleilistTrackEntity.COLUMN_ID
					+ " = "
					+ uri.getPathSegments().get(1)
					+ (!TextUtils.isEmpty(selection) ? "AND (" + selection
							+ ')' : "");
			count = db.update(PleilistTrackEntity.TABLE, values, selection,
					selectionArgs);
			break;
			

		default:
			throw new IllegalArgumentException("Unsupported URI" + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int rowsAffected = 0;
		Log.d(LOG_TAG, " - query :" + uri);

		if (dbHelper == null) {
			createDataBaseHelper();
		}

		db = dbHelper.getWritableDatabase();
		dbHelper.onCreate(db);
		dbHelper.onUpgrade(db, dataBaseVersion,
				DataBaseHelper.getVersionAvailable());
		
		String id = null;
		
		switch (uriMatcher.match(uri)) {

		case CATEGORIES:
			rowsAffected = db.delete(CategoryEntity.TABLE, selection,
					selectionArgs);
			break;

		case CATEGORY_ID:
			selection = CategoryEntity.COLUMN_ID
					+ " = "
					+ uri.getPathSegments().get(1)
					+ (!TextUtils.isEmpty(selection) ? "AND (" + selection
							+ ')' : "");
			rowsAffected = db.delete(CategoryEntity.TABLE, selection,
					selectionArgs);
			break;

		case COVERS:
			rowsAffected = db.delete(CoverEntity.TABLE, selection,
					selectionArgs);
			break;

		case COVER_ID:
			selection = CoverEntity.COLUMN_ID
					+ " = "
					+ uri.getPathSegments().get(1)
					+ (!TextUtils.isEmpty(selection) ? "AND (" + selection
							+ ')' : "");
			rowsAffected = db.delete(CoverEntity.TABLE, selection,
					selectionArgs);
			break;

		case PLEILISTS:
			rowsAffected = db.delete(PleilistEntity.TABLE, selection,
					selectionArgs);
			break;

		case PLEILIST_ID:
			selection = PleilistEntity.COLUMN_ID
					+ " = "
					+ uri.getPathSegments().get(1)
					+ (!TextUtils.isEmpty(selection) ? "AND (" + selection
							+ ')' : "");
			rowsAffected = db.delete(PleilistEntity.TABLE, selection,
					selectionArgs);
			break;
			
		case TRACKS:
			rowsAffected = db.delete(TrackEntity.TABLE, selection,
					selectionArgs);
			break;

		case TRACK_ID:
			selection = TrackEntity.COLUMN_ID
					+ " = "
					+ uri.getPathSegments().get(1)
					+ (!TextUtils.isEmpty(selection) ? "AND (" + selection
							+ ')' : "");
			rowsAffected = db.delete(TrackEntity.TABLE, selection,
					selectionArgs);
			break;

			
		case PLEILIST_TRACKS:
			rowsAffected = db.delete(PleilistTrackEntity.TABLE, selection,
					selectionArgs);
			break;

		case PLEILIST_TRACK_ID:
			selection = PleilistTrackEntity.COLUMN_ID
					+ " = "
					+ uri.getPathSegments().get(1)
					+ (!TextUtils.isEmpty(selection) ? "AND (" + selection
							+ ')' : "");
			rowsAffected = db.delete(PleilistTrackEntity.TABLE, selection,
					selectionArgs);
			break;

			
		default:
			throw new IllegalArgumentException("Unsupported UR" + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsAffected;
	}

}
