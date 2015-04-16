package net.rizkyzulkarnaen.productgallery.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ProductSqlHelper extends SQLiteOpenHelper {

	public static final String ARTICLE_TABLE = "product";
	public static final String IMAGE_TABLE = "image";
	public static final String FIELDS_TABLE = "fields";
	public static final String ID = "_id";
	public static final String ARTICLE_ID = "article_id";
	public static final String NO = "no";
	public static final String CATEGORY = "category";
	public static final String NAME = "name";
	public static final String VALUE = "value";
	public static final String IMAGE = "image";
	public static final String DATE = "date";

	private static final String DATABASE_NAME = "product_gallery.db";
	private static final int DATABASE_VERSION = 6;

	// Database creation sql statement
	private static final String ARTICLE_CREATE = "create table "
			+ ARTICLE_TABLE
			+ " (" 
			+ ID + " integer primary key autoincrement, " 
			+ NO + " text not null, " 
			+ IMAGE + " text null, "
			+ DATE + " integer not null " 
			+ ");";
	private static final String IMAGE_CREATE = "create table "
			+ IMAGE_TABLE
			+ " (" 
			+ ID + " integer primary key autoincrement, "  
			+ NO + " text null, " 
			+ IMAGE + " text null, "
			+ DATE + " integer not null " 
			+ ");";
	private static final String FIELDS_CREATE = "create table "
			+ FIELDS_TABLE
			+ " (" 
			+ ID + " integer primary key autoincrement, " 
			+ ARTICLE_ID + " integer not null, " 
			+ CATEGORY+ " text not null, " 
			+ NAME+ " text not null, " 
			+ VALUE + " text null"
			+ ");";

	public ProductSqlHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		// TODO Auto-generated method stub
		database.execSQL(ARTICLE_CREATE);
		database.execSQL(FIELDS_CREATE);
		database.execSQL(IMAGE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.w(ProductSqlHelper.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");

		db.execSQL("DROP TABLE IF EXISTS " + ARTICLE_TABLE+"; "+ ARTICLE_CREATE);
		db.execSQL("DROP TABLE IF EXISTS " + FIELDS_TABLE+"; "+  FIELDS_CREATE);
		db.execSQL("DROP TABLE IF EXISTS " + IMAGE_TABLE+"; "+  IMAGE_CREATE);
		onCreate(db);
	}

}
