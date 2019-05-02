package com.tech4use.sqlitedatabaseexample;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DatabaseSQLite extends SQLiteOpenHelper {

    Context context;
    // storing database name
    private static final String DATABASE_NAME = "info.db";
    // storing database version
    private static final int DATABASE_VERSION = 1;
    // storing table name
    static final String TABLE_NAME = "infoTable";
    // creating the column id
    static final String COLUMN_ID = "_id";
    // creating the items to be saved in the column
    static final String COLUMN_firstNAME = "firstName";
    static final String COLUMN_lastNAME = "lastName";
    static final String COLUMN_TIMESTAMP = "timestamp";
    // creating the s_query to pass to the database
    String s_query = "CREATE TABLE "+ TABLE_NAME +"(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_firstNAME + " TEXT, " +
            COLUMN_lastNAME + " TEXT, "+ COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +");";
    // creating s_query for droping table if it exists
    String DROP_TABLE  = "DROP TABLE IF EXISTS "+TABLE_NAME;

    public DatabaseSQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating the database if not available
        try {
            db.execSQL(s_query);
            Toast.makeText(context, "Database created successfully", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            Toast.makeText(context, "Error while creating SQLite : "+e, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // droping the previous databse if exits and then creating a new with new parameters
        try {
            db.execSQL(DROP_TABLE);
            onCreate(db);
            Toast.makeText(context, "Database updated successfully", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            Toast.makeText(context, "Error while upgrading SQLite : "+e, Toast.LENGTH_SHORT).show();
        }
    }

    // for inserting new data
    public long InsertData(String username, String password) {
        // getting the database
        SQLiteDatabase db = getWritableDatabase();

        // creating contentValue instance to put the data
        ContentValues values = new ContentValues();
        values.put(COLUMN_firstNAME, username);
        values.put(COLUMN_lastNAME, password);
        // inserting the new data
        long result = db.insert(TABLE_NAME, null, values);
        return result;
    }

    // deleting data on the basis of id
    public int DeleteData(String id) {
        SQLiteDatabase db = getWritableDatabase();
        String[] whereArgs = {id};
        // deleting the data
        int count = db.delete(TABLE_NAME, COLUMN_ID + "=?", whereArgs); // here ? will be replaced by whereArgs

        return count;
    }

    // method to get all data of the database in one click
    public Cursor getAllData () {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ TABLE_NAME, null);
        return cursor;
    }

    public int UpdateData(String oldUsername, String newUsername) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_firstNAME, newUsername);
        String[] whereArgs = {oldUsername};
        int count = db.update(TABLE_NAME, values, COLUMN_firstNAME +" =?", whereArgs);
        return count;
    }
}
