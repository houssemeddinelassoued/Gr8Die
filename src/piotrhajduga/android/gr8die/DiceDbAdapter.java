package piotrhajduga.android.gr8die;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DiceDbAdapter {
	
	public static final String KEY_FROM = "from_number";
	public static final String KEY_TO = "to_number";
	public static final String KEY_COUNT = "count";
	public static final String KEY_SUMUP = "sumup";
	public static final String KEY_HIDE_ALL = "hideall";
	public static final String KEY_DIE_ROWID = "_id";
	public static final String KEY_GROUP_ROWID = "_id";
	public static final String KEY_NAME = "name";
	
	private static final String TAG = "DiceDbAdapter";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	private static final String DATABASE_NAME = "gr8die";
	private static final String DATABASE_TABLE_DICE = "dice";
	private static final String DATABASE_TABLE_GROUPS = "groups";
	private static final String DATABASE_TABLE_DICE_GROUPS = "dice_groups";
	private static final int DATABASE_VERSION = 8;
	
	private static final String DATABASE_CREATE = 
			"create table if not exists " + DATABASE_TABLE_DICE + " (" +
			KEY_DIE_ROWID + " integer primary key autoincrement," +
			KEY_FROM + " integer not null," +
			KEY_TO + " integer not null," +
			KEY_COUNT + " integer not null," +
			KEY_SUMUP + " integer not null," +
			KEY_HIDE_ALL + " integer not null" +
			");" +
			"create table if not exists" + DATABASE_TABLE_GROUPS + " (" +
			KEY_GROUP_ROWID + " integer primary key autoincrement," +
			KEY_NAME + " text not null" +
			");" +
			"create table if not exists" + DATABASE_TABLE_DICE_GROUPS + " (" +
			KEY_DIE_ROWID + " integer," +
			KEY_GROUP_ROWID + " integer" +
			"CONSTRAINT pkey PRIMARY KEY (" +
			DATABASE_TABLE_DICE + "." + KEY_DIE_ROWID + "," +
			DATABASE_TABLE_GROUPS + "." + KEY_GROUP_ROWID +
			")" +
			");";
	
	private final Context mCtx;
	
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
            
            ContentValues cv = new ContentValues();
            cv.put(KEY_FROM, 1);
            cv.put(KEY_TO, 20);
            cv.put(KEY_COUNT, 3);
            cv.put(KEY_SUMUP, 0);
            cv.put(KEY_HIDE_ALL, 0);
            db.insert(DATABASE_TABLE_DICE, null, cv);
            
            cv = new ContentValues();
            cv.put(KEY_FROM, 1);
            cv.put(KEY_TO, 20);
            cv.put(KEY_COUNT, 1);
            cv.put(KEY_SUMUP, 0);
            cv.put(KEY_HIDE_ALL, 0);
            db.insert(DATABASE_TABLE_DICE, null, cv);
            
            cv = new ContentValues();
            cv.put(KEY_FROM, 1);
            cv.put(KEY_TO, 100);
            cv.put(KEY_COUNT, 1);
            cv.put(KEY_SUMUP, 0);
            cv.put(KEY_HIDE_ALL, 0);
            db.insert(DATABASE_TABLE_DICE, null, cv);
        }
        
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_DICE + ";" +
            		"DROP TABLE IF EXISTS " + DATABASE_TABLE_GROUPS + ";" +
            		"DROP TABLE IF EXISTS" + DATABASE_TABLE_DICE_GROUPS);
            onCreate(db);
        }
    }
    
    public DiceDbAdapter(Context ctx) {
    	mCtx = ctx;
    }
    
    public DiceDbAdapter open() throws SQLException {
    	mDbHelper = new DatabaseHelper(mCtx);
    	mDb = mDbHelper.getWritableDatabase();
    	return this;
    }
    
    public void close() {
    	mDbHelper.close();
    }
    
    public long addDie(Integer from, Integer to, Integer count, Boolean sumup, Boolean hide_all) {
    	Cursor cursor = mDb.query(true, DATABASE_TABLE_DICE,
            		new String[] {KEY_DIE_ROWID},
                    KEY_FROM + "=" + from + " and " +
                    KEY_TO + "=" + to + " and " +
                    KEY_COUNT + "=" + count + " and " +
                    KEY_SUMUP + "=" + (sumup?1:0) + " and " +
                    KEY_HIDE_ALL + "=" + (hide_all?1:0),
                    null, null, null, null, null);
        if (cursor.getCount()>0)
	        return cursor.getPosition();
    	ContentValues cv = new ContentValues();
    	cv.put(KEY_FROM, from);
    	cv.put(KEY_TO, to);
    	cv.put(KEY_COUNT, count);
    	cv.put(KEY_SUMUP, (sumup?1:0));
    	cv.put(KEY_HIDE_ALL, (hide_all?1:0));
    
    	return mDb.insert(DATABASE_TABLE_DICE, null, cv);
    }
    
    public int updateDie(Integer rowid, Integer from, Integer to, Integer count, Boolean sumup, Boolean hide_all) {
    	Cursor cursor = mDb.query(true, DATABASE_TABLE_DICE,
            		new String[] {KEY_DIE_ROWID},
                    KEY_FROM + "=" + from + " and " +
                    KEY_TO + "=" + to + " and " +
                    KEY_COUNT + "=" + count + " and " +
                    KEY_SUMUP + "=" + (sumup?1:0) + " and " +
                    KEY_HIDE_ALL + "=" + (hide_all?1:0),
                    null, null, null, null, null);
        if (cursor.getCount()>0)
	        return cursor.getPosition();
    	ContentValues cv = new ContentValues();
    	cv.put(KEY_FROM, from);
    	cv.put(KEY_TO, to);
    	cv.put(KEY_COUNT, count);
    	cv.put(KEY_SUMUP, (sumup?1:0));
    	cv.put(KEY_HIDE_ALL, (hide_all?1:0));
    
    	return mDb.update(DATABASE_TABLE_DICE, cv, KEY_DIE_ROWID + "=" + rowid, null);
    }

    
    public boolean deleteDie(long rowId) {
        return mDb.delete(DATABASE_TABLE_DICE, KEY_DIE_ROWID + "=" + rowId, null) > 0;
    }
    
    public Cursor fetchAllDice() {
        return mDb.query(DATABASE_TABLE_DICE,
        		new String[] {KEY_DIE_ROWID, KEY_FROM, KEY_TO, KEY_COUNT, KEY_SUMUP, KEY_HIDE_ALL},
        		null, null, null, null, null);
    }
    
    public Cursor fetchDie(Integer id) {
        return mDb.query(DATABASE_TABLE_DICE,
        		new String[] {KEY_DIE_ROWID, KEY_FROM, KEY_TO, KEY_COUNT, KEY_SUMUP, KEY_HIDE_ALL},
        		KEY_DIE_ROWID + "=" + id, null, null, null, null);
    }
}
