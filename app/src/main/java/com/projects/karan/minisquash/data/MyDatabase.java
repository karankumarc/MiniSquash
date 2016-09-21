package com.projects.karan.minisquash.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.projects.karan.minisquash.data.MiniSquashContract.GameStateDetailsEntry;
import com.projects.karan.minisquash.data.MiniSquashContract.MatchDetailsEntry;
import com.projects.karan.minisquash.data.MiniSquashContract.PlayerDetailsEntry;
import com.projects.karan.minisquash.model.Match;
import com.projects.karan.minisquash.model.Player;

import java.util.ArrayList;

/**
 * Created by ADMIN on 9/6/2016.
 */
public class MyDatabase {

    public SQLiteDatabase database;
    private MiniSquashDbHelper helper;

    public MyDatabase(Context context) {
        helper = new MiniSquashDbHelper(context);
    }

    public SQLiteDatabase openReadableDatabaseInstance() {
        return helper.getReadableDatabase();
    }

    public SQLiteDatabase openWritableDatabaseInstance() {
        return helper.getWritableDatabase();
    }

    public void closeDatabaseConnection() {
        database.close();
        helper.close();
    }

    //region Methods for PlayerDetails table
    public long createPlayerRow(String playerName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PlayerDetailsEntry.COLUMN_PLAYER_NAME, playerName);
        contentValues.put(PlayerDetailsEntry.COLUMN_MATCHES_PLAYED, 0);
        contentValues.put(PlayerDetailsEntry.COLUMN_MATCHES_WON, 0);
        return database.insert(PlayerDetailsEntry.TABLE_NAME, null, contentValues);
    }

    public int[] getPlayerMatchesPlayedAndWon(long id){
        int[] matchesPlayedAndWon = new int[2];
        String[] projections = {PlayerDetailsEntry.COLUMN_MATCHES_PLAYED, PlayerDetailsEntry.COLUMN_MATCHES_WON};
        String selection = PlayerDetailsEntry._ID +" = ?";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = database.query(PlayerDetailsEntry.TABLE_NAME, projections, selection, selectionArgs, null, null, null);

        if(cursor.moveToFirst()){
            matchesPlayedAndWon[0] = cursor.getInt(cursor.getColumnIndex(PlayerDetailsEntry.COLUMN_MATCHES_PLAYED));
            matchesPlayedAndWon[1] = cursor.getInt(cursor.getColumnIndex(PlayerDetailsEntry.COLUMN_MATCHES_WON));
        }

        return matchesPlayedAndWon;
    }

    public long updatePlayerMatchesPlayedAndWon(long id, int matchesPlayed, int matchesWon){

        ContentValues contentValues = new ContentValues();
        contentValues.put(PlayerDetailsEntry.COLUMN_MATCHES_PLAYED, matchesPlayed);
        contentValues.put(PlayerDetailsEntry.COLUMN_MATCHES_WON, matchesWon);


        String selection = PlayerDetailsEntry._ID +" = ?";
        String[] selectionArgs = {String.valueOf(id)};

        return database.update(PlayerDetailsEntry.TABLE_NAME, contentValues, selection, selectionArgs);
    }

    public long getIdIfPlayerExists(String playerName){

        String[] projections = {PlayerDetailsEntry._ID};
        String selection = PlayerDetailsEntry.COLUMN_PLAYER_NAME +" = ?";
        String[] selectionArgs = {playerName};

        Cursor cursor = database.query(PlayerDetailsEntry.TABLE_NAME, projections, selection, selectionArgs, null, null, null );

        if(cursor.moveToFirst()){
            return cursor.getInt(cursor.getColumnIndex(PlayerDetailsEntry._ID));
        } else {
            return -1;
        }
    }

    public ArrayList<Player> getAllPlayerDetails() {

        Cursor cursor = database.query(PlayerDetailsEntry.TABLE_NAME, null, null, null, null, null, null);
        ArrayList<Player> arrayList = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                Player player = new Player(cursor.getInt(cursor.getColumnIndex(PlayerDetailsEntry._ID)),
                        cursor.getString(cursor.getColumnIndex(PlayerDetailsEntry.COLUMN_PLAYER_NAME)),
                        cursor.getInt(cursor.getColumnIndex(PlayerDetailsEntry.COLUMN_MATCHES_PLAYED)),
                        cursor.getInt(cursor.getColumnIndex(PlayerDetailsEntry.COLUMN_MATCHES_WON)));
                arrayList.add(player);
            }while (cursor.moveToNext());
        }

        return arrayList;
    }

    public Cursor getPlayerHistory() {
        String[] projection = {PlayerDetailsEntry._ID, PlayerDetailsEntry.COLUMN_PLAYER_NAME,
                PlayerDetailsEntry.COLUMN_MATCHES_PLAYED, PlayerDetailsEntry.COLUMN_MATCHES_WON};
        return database.query(PlayerDetailsEntry.TABLE_NAME, projection, null, null, null, null, null);
    }

    public String[] getPlayerNames(){
        String[] projection = {PlayerDetailsEntry.COLUMN_PLAYER_NAME};
        Cursor cursor= database.query(PlayerDetailsEntry.TABLE_NAME, projection, null, null, null, null, null);
        String[] playerNames = new String[cursor.getCount()];

        if(cursor.moveToFirst()){
            do{
                playerNames[cursor.getPosition()] = cursor.getString(cursor.getColumnIndex(PlayerDetailsEntry.COLUMN_PLAYER_NAME));
            }while (cursor.moveToNext());
        }
        return playerNames;
    }

    public boolean checkIfPlayerNamesExist(){
        String[] projection = {PlayerDetailsEntry.COLUMN_PLAYER_NAME};
        Cursor cursor= database.query(PlayerDetailsEntry.TABLE_NAME, projection, null, null, null, null, null);

        if(cursor.moveToFirst()){
            return true;
        } else {
            return false;
        }
    }
    //endregion

    //region Methods related to GameStateDetails table
    public long insertGameDetails(boolean didPlayer1Win) {
        ContentValues contentValues = new ContentValues();
        if (didPlayer1Win)
            contentValues.put(GameStateDetailsEntry.COLUMN_DID_PLAYER_1_WIN, 1);
        else
            contentValues.put(GameStateDetailsEntry.COLUMN_DID_PLAYER_1_WIN, 0);
        return database.insert(GameStateDetailsEntry.TABLE_NAME, null, contentValues);
    }

    public Cursor getAllGameDetails() {
        String[] projection = {GameStateDetailsEntry.COLUMN_DID_PLAYER_1_WIN};
        return database.query(GameStateDetailsEntry.TABLE_NAME, projection, null, null, null, null, null);
    }

    public long deleteAllGameDetails() {
        return database.delete(GameStateDetailsEntry.TABLE_NAME, null, null);
    }
    //endregion

    //region Methods related to MatchDetails table
    public Cursor getMatchHistory() {
        String[] projection = {MatchDetailsEntry._ID, MatchDetailsEntry.COLUMN_DATE, MatchDetailsEntry.COLUMN_WINNER_NAME,
                MatchDetailsEntry.COLUMN_LOSER_NAME, MatchDetailsEntry.COLUMN_WINNER_SETS_WON, MatchDetailsEntry.COLUMN_LOSER_SETS_WON};
        return database.query(MatchDetailsEntry.TABLE_NAME, projection, null, null, null, null, null);
    }

    public ArrayList<Match> getMatchHistoryInArrayList() {
        openReadableDatabaseInstance();
        String[] projection = {MatchDetailsEntry._ID, MatchDetailsEntry.COLUMN_DATE, MatchDetailsEntry.COLUMN_WINNER_NAME,
                MatchDetailsEntry.COLUMN_LOSER_NAME, MatchDetailsEntry.COLUMN_WINNER_SETS_WON, MatchDetailsEntry.COLUMN_LOSER_SETS_WON};
        Cursor c =  database.query(MatchDetailsEntry.TABLE_NAME, projection, null, null, null, null, null);
        ArrayList<Match> arrayListMatches = new ArrayList<>();

        if(c.moveToFirst()){
            do{
                String date = c.getString(c.getColumnIndex(MatchDetailsEntry.COLUMN_DATE));
                Match match = new Match(c.getInt(c.getColumnIndex(MatchDetailsEntry._ID)),c.getInt(c.getColumnIndex(MatchDetailsEntry.COLUMN_WINNER_SETS_WON)),
                        c.getInt(c.getColumnIndex(MatchDetailsEntry.COLUMN_LOSER_SETS_WON)), c.getString(c.getColumnIndex(MatchDetailsEntry.COLUMN_WINNER_NAME)),
                        c.getString(c.getColumnIndex(MatchDetailsEntry.COLUMN_LOSER_NAME)),
                        date);
                arrayListMatches.add(match);
            }while (c.moveToNext());
        }
        closeDatabaseConnection();

        return arrayListMatches;
    }



    public long insertMatchDetails(String date, String time, int pointsPerSet, int serviceStartedByWinner, int totalSets,
                                   String winnerName, int winnerTotalPointsWon, int winnerSetsWon, int winnerTotalServes, int winnerPointsWonInService,
                                   String loserName, int loserTotalPointsWon, int loserSetsWon, int loserTotalServes, int loserPointsWonInService,
                                   long winnerId, long loserId) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(MatchDetailsEntry.COLUMN_DATE, date);
        contentValues.put(MatchDetailsEntry.COLUMN_TIME, time);
        contentValues.put(MatchDetailsEntry.COLUMN_POINTS_PER_SET, pointsPerSet);
        contentValues.put(MatchDetailsEntry.COLUMN_SERVICE_STARTED_BY_WINNER, serviceStartedByWinner);
        contentValues.put(MatchDetailsEntry.COLUMN_TOTAL_SETS, totalSets);
        contentValues.put(MatchDetailsEntry.COLUMN_WINNER_NAME, winnerName);
        contentValues.put(MatchDetailsEntry.COLUMN_WINNER_TOTAL_POINTS_WON, winnerTotalPointsWon);
        contentValues.put(MatchDetailsEntry.COLUMN_WINNER_SETS_WON, winnerSetsWon);
        contentValues.put(MatchDetailsEntry.COLUMN_WINNER_TOTAL_SERVES, winnerTotalServes);
        contentValues.put(MatchDetailsEntry.COLUMN_WINNER_POINTS_WON_IN_SERVICE, winnerPointsWonInService);
        contentValues.put(MatchDetailsEntry.COLUMN_LOSER_NAME, loserName);
        contentValues.put(MatchDetailsEntry.COLUMN_LOSER_TOTAL_POINTS_WON, loserTotalPointsWon);
        contentValues.put(MatchDetailsEntry.COLUMN_LOSER_SETS_WON, loserSetsWon);
        contentValues.put(MatchDetailsEntry.COLUMN_LOSER_TOTAL_SERVES, loserTotalServes);
        contentValues.put(MatchDetailsEntry.COLUMN_LOSER_POINTS_WON_IN_SERVICE, loserPointsWonInService);
        contentValues.put(MatchDetailsEntry.COLUMN_WINNER_ID, winnerId);
        contentValues.put(MatchDetailsEntry.COLUMN_LOSER_ID, loserId);

        return database.insert(MatchDetailsEntry.TABLE_NAME, null, contentValues);
    }
    //endregion

    private class MiniSquashDbHelper extends SQLiteOpenHelper {

        //region SQL Statements
        private static final String SQL_CREATE_GAME_STATE_DETAILS_TABLE = "CREATE TABLE " + GameStateDetailsEntry.TABLE_NAME + "("
                + GameStateDetailsEntry._ID + " INTEGER PRIMARY KEY, "
                + GameStateDetailsEntry.COLUMN_DID_PLAYER_1_WIN + " BOOLEAN NOT NULL);";

        private static final String SQL_CREATE_PLAYER_DETAILS_TABLE = "CREATE TABLE " + PlayerDetailsEntry.TABLE_NAME + "("
                + PlayerDetailsEntry._ID + " INTEGER PRIMARY KEY, "
                + PlayerDetailsEntry.COLUMN_PLAYER_NAME + " TEXT NOT NULL, "
                + PlayerDetailsEntry.COLUMN_MATCHES_PLAYED + " INT NOT NULL, "
                + PlayerDetailsEntry.COLUMN_MATCHES_WON + " INT NOT NULL);";


        private static final String SQL_CREATE_MATCH_DETAILS_TABLE = "CREATE TABLE " + MatchDetailsEntry.TABLE_NAME + "("
                + MatchDetailsEntry._ID + " INTEGER PRIMARY KEY, "
                + MatchDetailsEntry.COLUMN_DATE + " TEXT NOT NULL, "
                + MatchDetailsEntry.COLUMN_TIME + " TEXT NOT NULL, "
                + MatchDetailsEntry.COLUMN_POINTS_PER_SET + " INT NOT NULL, "
                + MatchDetailsEntry.COLUMN_SERVICE_STARTED_BY_WINNER + " BOOLEAN NOT NULL, "
                + MatchDetailsEntry.COLUMN_TOTAL_SETS + " INT NOT NULL, "
                + MatchDetailsEntry.COLUMN_WINNER_NAME + " TEXT NOT NULL, "
                + MatchDetailsEntry.COLUMN_WINNER_ID + " INT NOT NULL, "
                + MatchDetailsEntry.COLUMN_WINNER_TOTAL_POINTS_WON + " INT NOT NULL, "
                + MatchDetailsEntry.COLUMN_WINNER_SETS_WON + " INT NOT NULL, "
                + MatchDetailsEntry.COLUMN_WINNER_TOTAL_SERVES + " INT NOT NULL, "
                + MatchDetailsEntry.COLUMN_WINNER_POINTS_WON_IN_SERVICE + " INT NOT NULL, "
                + MatchDetailsEntry.COLUMN_LOSER_NAME + " TEXT NOT NULL, "
                + MatchDetailsEntry.COLUMN_LOSER_ID + " INT NOT NULL, "
                + MatchDetailsEntry.COLUMN_LOSER_TOTAL_POINTS_WON + " INT NOT NULL, "
                + MatchDetailsEntry.COLUMN_LOSER_SETS_WON + " INT NOT NULL, "
                + MatchDetailsEntry.COLUMN_LOSER_TOTAL_SERVES + " INT NOT NULL, "
                + MatchDetailsEntry.COLUMN_LOSER_POINTS_WON_IN_SERVICE + " INT NOT NULL, "
                + " FOREIGN KEY(" + MatchDetailsEntry.COLUMN_WINNER_ID + ","+MatchDetailsEntry.COLUMN_LOSER_ID +") REFERENCES "
                + PlayerDetailsEntry.TABLE_NAME + "(" + PlayerDetailsEntry._ID + "," + PlayerDetailsEntry._ID+")"
                + ");";

        private static final String SQL_DROP_GAME_STATE_DETAILS_TABLE = "DROP TABLE " + GameStateDetailsEntry.TABLE_NAME + ";";
        private static final String SQL_DROP_MATCH_DETAILS_TABLE = "DROP TABLE " + MatchDetailsEntry.TABLE_NAME + ";";
        private static final String SQL_DROP_PLAYER_DETAILS_TABLE = "DROP TABLE " + PlayerDetailsEntry.TABLE_NAME + ";";
        //endregion

        private static final String DATABASE_NAME = "MiniSquash.db";
        private static final int DATABASE_VERSION = 5;

        public MiniSquashDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_GAME_STATE_DETAILS_TABLE);
            db.execSQL(SQL_CREATE_PLAYER_DETAILS_TABLE);
            db.execSQL(SQL_CREATE_MATCH_DETAILS_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (newVersion > oldVersion) {
                db.execSQL(SQL_DROP_GAME_STATE_DETAILS_TABLE);
                db.execSQL(SQL_DROP_MATCH_DETAILS_TABLE);
                db.execSQL(SQL_DROP_PLAYER_DETAILS_TABLE);
                onCreate(db);
            }
        }
    }


}
