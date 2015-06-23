package com.stoianovici.horatiu.kontrol.database;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
 
@SuppressLint("SimpleDateFormat")
public class SavedServers extends SQLiteOpenHelper {
 
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "kontrol";
 
    // Contacts table name
    private static final String TABLE_NAME = "messages";
 
    // Contacts Table Columns names
    private static final String KEY_KEY = "server_key";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_ID = "server_id";
 
    public SavedServers(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
        		+ KEY_PASSWORD + " text, "
        		+ KEY_KEY + " text, "
        		+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT)";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
 
        // Create tables again
        onCreate(db);
    }
    
    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
 
    public void addSavedServer(SavedServerInfo server) {
    	if(server.getKey() != null){
    		//only add the message if it doesn't already exist
	        SQLiteDatabase db = this.getWritableDatabase();
	 
	        ContentValues values = new ContentValues();
	        values.put(KEY_PASSWORD, server.getPassword());
	        values.put(KEY_KEY, server.getKey());
	        // Inserting Row
	        server.setId(db.insert(TABLE_NAME, null, values));
	        db.close(); // Closing database connection
    	}
    }
     
    public List<SavedServerInfo> getAllSavedServers() {
        List<SavedServerInfo> contactList = new ArrayList<SavedServerInfo>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	SavedServerInfo server = new SavedServerInfo();
            	server.setPassword(cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)));
            	server.setKey(cursor.getString(cursor.getColumnIndex(KEY_KEY)));
            	server.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                contactList.add(server);
            } while (cursor.moveToNext());
        }
        
        return contactList;
    }
 
    public int updateSavedServer(SavedServerInfo message) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_PASSWORD, message.getPassword());
        values.put(KEY_KEY, message.getKey());
 
        // updating row
        return db.update(TABLE_NAME, values,  KEY_ID + " = ?", new String[] {String.valueOf(message.getId())});
    }
 
    public int getSavedServersCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
 
        // return count
        return cursor.getCount();
    }

	public SavedServerInfo getSavedServer(String key) {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_KEY + " = '" + key + "'", null);
		if (cursor.moveToFirst()) {
            do {
            	SavedServerInfo server = new SavedServerInfo();
            	server.setPassword(cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)));
                server.setKey(cursor.getString(cursor.getColumnIndex(KEY_KEY)));
                server.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                db.close();
                return server;
            } while (cursor.moveToNext());
        }
		return null;
	}
	
	public SavedServerInfo getSavedServer(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_ID + " = " + String.valueOf(id) + "", null);
		if (cursor.moveToFirst()) {
            do {
                SavedServerInfo server = new SavedServerInfo();
                server.setPassword(cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)));
                server.setKey(cursor.getString(cursor.getColumnIndex(KEY_KEY)));
                server.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                return server;
            } while (cursor.moveToNext());
        }
		return null;
	}

	public void deleteServers() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_NAME, null, null);
		
		db.close();
	}
	
	public void deleteServer(SavedServerInfo server){
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete(TABLE_NAME, KEY_ID + " = ?", new String[] { String.valueOf(server.getId()) });
	}
	
	public void deleteServer(int id){
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete(TABLE_NAME, KEY_ID + " = ?", new String[] { String.valueOf(id) });
	}
	
	public void deleteServer(String key){
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete(TABLE_NAME, KEY_KEY + " = ?", new String[] { key });
	}
}
    
