package com.arawaney.plei.db;


public class PleilistTrackEntity {
	public static final String TABLE = "pleilistTrack";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_PLEILIST_ID = "pleilist_id";
	public static final String COLUMN_TRACK_ID = "track_id";
	public static final String COLUMN_PLEILIST_ORDER = "pleilistOrder";


	


	public static final int DATABASE_VERSION = 1;

	public static final String CREATE_TABLE_PLEILIST_TRACK = "CREATE TABLE IF NOT EXISTS "
			+ TABLE 
			+ " (" 
			+ COLUMN_ID 
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " 
			+ COLUMN_PLEILIST_ID
			+ " TEXT, " 
			+ COLUMN_PLEILIST_ORDER 
			+ " INTEGER, "
			+ COLUMN_TRACK_ID
			+ " TEXT);";
}
