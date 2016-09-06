package com.projects.karan.minisquash.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.projects.karan.minisquash.data.MiniSquashContract.GameStateDetailsEntry;

/**
 * Created by ADMIN on 9/6/2016.
 */
public class MyDatabase{

    public SQLiteDatabase database;
    private MiniSquashDbHelper helper;

    public MyDatabase(Context context){
        helper = new MiniSquashDbHelper(context);
    }

    public SQLiteDatabase openReadableDatabaseInstance(){
        return helper.getReadableDatabase();
    }

    public SQLiteDatabase openWritableDatabaseInstance(){
        return helper.getWritableDatabase();
    }

    public void closeDatabaseConnection(){
        database.close();
        helper.close();
    }

    public long insertGameDetails(boolean didPlayer1Win){
        ContentValues contentValues = new ContentValues();
        if(didPlayer1Win)
            contentValues.put(GameStateDetailsEntry.COLUMN_DID_PLAYER_1_WIN, 1);
        else
            contentValues.put(GameStateDetailsEntry.COLUMN_DID_PLAYER_1_WIN, 0);
        return database.insert(GameStateDetailsEntry.TABLE_NAME, null, contentValues);
    }

    public Cursor getAllGameDetails(){
        String[] projection = {GameStateDetailsEntry.COLUMN_DID_PLAYER_1_WIN};
        return database.query(GameStateDetailsEntry.TABLE_NAME, projection, null, null, null, null, null);
    }

    public long deleteAllGameDetails(){
        return database.delete(GameStateDetailsEntry.TABLE_NAME, null, null);
    }

    private class MiniSquashDbHelper extends SQLiteOpenHelper{

        private static final String SQL_CREATE_GAME_STATE_DETAILS_TABLE = "CREATE TABLE "+ GameStateDetailsEntry.TABLE_NAME +"("
                +   GameStateDetailsEntry._ID + " INTEGER PRIMARY KEY, "
                +   GameStateDetailsEntry.COLUMN_DID_PLAYER_1_WIN + " BOOLEAN NOT NULL);";
        private static final String SQL_DROP_GAME_STATE_DETAILS_TABLE = "DROP TABLE "+ GameStateDetailsEntry.TABLE_NAME +";";

        private static final String DATABASE_NAME = "MiniSquash.db";
        private static final int DATABASE_VERSION = 1;

        public MiniSquashDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_GAME_STATE_DETAILS_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if(newVersion > oldVersion){
                db.execSQL(SQL_DROP_GAME_STATE_DETAILS_TABLE);
                onCreate(db);
            }
        }
    }





}
