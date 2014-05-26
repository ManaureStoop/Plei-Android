package com.arawaney.plei.db;


public class CoverEntity {
	public static final String TABLE = "cover";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_SYSTEM_ID = "system_id";
	public static final String COLUMN_IMAGE_FILE = "imageFIle";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_UPDATED_AT = "updated_at";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_CATEGORY_ID = "categoryId";
	public static final String COLUMN_PLEILIST_ID = "pleilistId";
	public static final String COLUMN_SECTION = "section";

	public static final int DATABASE_VERSION = 1;

	public static final String CREATE_TABLE_COVER = "CREATE TABLE IF NOT EXISTS "
			+ TABLE 
			+ " (" 
			+ COLUMN_ID 
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " 
			+ COLUMN_SYSTEM_ID
			+ " TEXT, " 
			+ COLUMN_IMAGE_FILE
			+ " TEXT, "
			+ COLUMN_UPDATED_AT 
			+ " INTEGER, " 
			+ COLUMN_TYPE 
			+ " TEXT, " 
			+ COLUMN_CATEGORY_ID 
			+ " TEXT, " 
			+ COLUMN_PLEILIST_ID 
			+ " TEXT, " 
			+ COLUMN_SECTION 
			+ " INTEGER, " 
			+ COLUMN_NAME 
			+ " TEXT);";
}
