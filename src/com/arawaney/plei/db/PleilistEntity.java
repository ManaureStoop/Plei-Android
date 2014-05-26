package com.arawaney.plei.db;


public class PleilistEntity {
	public static final String TABLE = "pleilist";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_SYSTEM_ID = "system_id";
	public static final String COLUMN_IMAGE = "image";
	public static final String COLUMN_COVER_IMAGE = "coverImage";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_UPDATED_AT = "updated_at";
	public static final String COLUMN_CATEGORY_ID = "categoryId";
	public static final String COLUMN_CATEGORY_ORDER = "categoryOrder";
	public static final String COLUMN_FAVORITE = "favorite";
	public static final String COLUMN_DELETED = "deleted";
	public static final String COLUMN_FLAGED = "flaged";
	


	public static final int DATABASE_VERSION = 1;

	public static final String CREATE_TABLE_PLEILIST = "CREATE TABLE IF NOT EXISTS "
			+ TABLE 
			+ " (" 
			+ COLUMN_ID 
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " 
			+ COLUMN_SYSTEM_ID
			+ " TEXT, " 
			+ COLUMN_IMAGE
			+ " TEXT, "
			+ COLUMN_COVER_IMAGE
			+ " TEXT, "
			+ COLUMN_UPDATED_AT 
			+ " INTEGER, " 
			+ COLUMN_DELETED
			+ " INTEGER, " 
			+ COLUMN_FLAGED
			+ " INTEGER, " 
			+ COLUMN_CATEGORY_ORDER
			+ " INTEGER, " 
			+ COLUMN_FAVORITE
			+ " INTEGER, " 
			+ COLUMN_CATEGORY_ID 
			+ " TEXT, " 
			+ COLUMN_NAME 
			+ " TEXT);";
	
}
