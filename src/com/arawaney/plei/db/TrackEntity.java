package com.arawaney.plei.db;

import java.util.Calendar;

public class TrackEntity {
	public static final String TABLE = "track";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_SYSTEM_ID = "system_id";
	public static final String COLUMN_ARTIST = "artist";
	public static final String COLUMN_URL = "url";
	public static final String COLUMN_YOUTUBE_URL = "youtubeUrl";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_UPDATED_AT = "updated_at";
	public static final String COLUMN_PLEILIST_ORDER = "pleilistOrder";
	

	public static final int DATABASE_VERSION = 1;

	public static final String CREATE_TABLE_TRACK = "CREATE TABLE IF NOT EXISTS "
			+ TABLE 
			+ " (" 
			+ COLUMN_ID 
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " 
			+ COLUMN_SYSTEM_ID
			+ " TEXT, " 
			+ COLUMN_ARTIST	
			+ " TEXT, "
			+ COLUMN_URL
			+ " TEXT, "
			+ COLUMN_YOUTUBE_URL
			+ " TEXT, "
			+ COLUMN_PLEILIST_ORDER 
			+ " INTEGER, "
			+ COLUMN_UPDATED_AT 
			+ " INTEGER, "  
			+ COLUMN_NAME 
			+ " TEXT);";
}
