package edu.mbryla.andlogger.database;

import java.text.SimpleDateFormat;
import java.util.Date;

import edu.mbryla.andlogger.database.models.GenericLog;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/** Database managing class. Handles db opening and closing as well as data insertion & retrieval.
 * 
 * @author mbryla
 * @version 1.0
 */
public class DbAdapter {
	private final static String DEBUG_TAG = "DbAdapter";
	
	/* db management constants */
	private final static int DB_VERSION = 1;
	private final static String DB_NAME = "andlogger.db";
	
	/* table specific constants */
	private final static String TABLE_LOGS = "logs";	
	
	public final static String KEY_ID = "_id";
	public final static String TYPE_ID = "integer primary key autoincrement";
	public final static int COL_NUM_ID = 0;
	
	public final static String KEY_TIMESTAMP = "timestamp";
	public final static String TYPE_TIMESTAMP = "text not null";
	public final static int COL_NUM_TIMESTAMP = 1;
	
	public final static String KEY_TAG = "tag";
	public final static String TYPE_TAG = "text not null";
	public final static int COL_NUM_TAG = 2;
	
	public final static String KEY_DATA = "data";
	public final static String TYPE_DATA = "text not null";
	public final static int COL_NUM_DATA = 3;
	
	private final static String[] COLUMNS_LOGS = {KEY_ID, KEY_TIMESTAMP, KEY_TAG, KEY_DATA};
	
	private final static String CREATE_TABLE_LOGS = "create table " + TABLE_LOGS + "(" +
			KEY_ID			+ " " + 	TYPE_ID			+ ", " +
			KEY_TIMESTAMP	+ " " + 	TYPE_TIMESTAMP	+ ", " +
			KEY_TAG			+ " " + 	TYPE_TAG		+ ", " +
			KEY_DATA		+ " " + 	TYPE_DATA		+ ")";
	private final static String DROP_TABLE_LOGS = "drop table if exists " + TABLE_LOGS;
	
	/** Database creation and update managing class
	 *
	 * @author mbryla
	 * @version 1.0	 
	 */
	private static class DbHelper extends SQLiteOpenHelper {
		public DbHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d(DEBUG_TAG, "DB :: creating db ver. " + DB_VERSION);
			
			db.execSQL(DbAdapter.CREATE_TABLE_LOGS);
			Log.d(DEBUG_TAG, "DB :: table " + TABLE_LOGS + " created");
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d(DEBUG_TAG, "DB :: updating db ver. " + oldVersion + " to ver. " + newVersion);
			
			db.execSQL(DbAdapter.DROP_TABLE_LOGS);
			Log.d(DEBUG_TAG, "DB :: table " + TABLE_LOGS + " dropped");
		}
	}
	
	private Context context;
	private SQLiteDatabase db;
	private DbHelper dbHelper;
	
	public DbAdapter(Context context) {
		this.context = context;
	}
	
	public DbAdapter openWritable() {
		this.dbHelper = new DbHelper(context, DB_NAME, null, DB_VERSION);
		
		try {
			db = dbHelper.getWritableDatabase();
			return this;
		} catch(SQLException e) {
			Log.e(DEBUG_TAG, "DB :: can't get writable database!");			
			Toast.makeText(context, "Can't get writable db!", Toast.LENGTH_SHORT).show();					
		}	
		
		return null;
	}
	
	public DbAdapter openReadable() {
		this.dbHelper = new DbHelper(context, DB_NAME, null, DB_VERSION);
		
		try {
			db = dbHelper.getReadableDatabase();
			return this;
		} catch (SQLException e) {			
			Log.e(DEBUG_TAG, "DB :: can't get readable database!");
			Toast.makeText(context, "Can't get readable db!", Toast.LENGTH_SHORT).show();
		}
		
		return null;
	}
	
	public void close() {
		dbHelper.close();
	}
	
	public boolean isOpenWritable() {
		return db.isOpen() && !db.isReadOnly();
	}
	
	public boolean isOpenReadable() {
		return db.isOpen();
	}
	
	/* data insertion & retrieval */
	@SuppressLint("SimpleDateFormat")
	public long insertGenericLog(String tag, String data) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timestamp = sdf.format(new Date());
		
		ContentValues cVals = new ContentValues();
		cVals.put(KEY_TIMESTAMP, timestamp);
		cVals.put(KEY_TAG, tag);
		cVals.put(KEY_DATA, data);
		
		Log.d(DEBUG_TAG, "DB :: inserting log: " + timestamp + " | " + tag + " | " + data);
		return db.insert(TABLE_LOGS, null, cVals);
	}
	
	public GenericLog getGenericLog(long id) {
		String selection = KEY_ID + "=" + id;				
		
		Cursor cursor = db.query(TABLE_LOGS, COLUMNS_LOGS, selection, null, null, null, null);
		if(cursor != null && cursor.moveToFirst()) {
			String timestamp = cursor.getString(COL_NUM_TIMESTAMP);
			String tag = cursor.getString(COL_NUM_TAG);
			String data = cursor.getString(COL_NUM_DATA);
			
			return new GenericLog(id, timestamp, tag, data);
		}
		
		return null;
	}
	
	/** Retrieves the <code>numOfLogs</code> last inserted generic logs.
	 * 
	 * @param numOfLogs
	 * @return cursor with last <code>numOfLogs</code> generic logs
	 */
	public Cursor getLastGenericLogs(int numOfLogs) {
		String orderBy = KEY_ID + " DESC";
		String limit = "0, " + numOfLogs;
		
		return db.query(TABLE_LOGS, COLUMNS_LOGS, null, null, null, null, orderBy, limit);
	}
}
