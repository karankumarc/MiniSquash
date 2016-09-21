package com.projects.karan.minisquash.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.projects.karan.minisquash.R;
import com.projects.karan.minisquash.data.MiniSquashContract;
import com.projects.karan.minisquash.data.MyDatabase;
import com.projects.karan.minisquash.model.GameState;
import com.projects.karan.minisquash.utils.Constants;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    private MyDatabase myDatabase;

    private LinearLayout linearLayoutPlayer1, linearLayoutPlayer2;
    private TextView textViewPlayer1Name, textViewPlayer2Name, textViewPlayer1Score, textViewPlayer2Score, textViewCurrentSet,
            textViewPlayer1SetsWon, textViewPlayer2SetsWon;
    private Button buttonPlayer1Point, buttonPlayer2Point;

    private int pointsPerSet = 0, serviceStarts = 0, totalSets = 1, player1CurrentScore = 0, player2CurrentScore = 0,
            player1TotalPointsWon = 0, player2TotalPointsWon = 0, totalScoreCount = 0,
            player1SetsWon = 0, player2SetsWon = 0, currentSet = 1,
            player1TotalServes = 0, player2TotalServes = 0, player1TotalPointsWonOnServes = 0, player2TotalPointsWonOnServes = 0;
    private String playerOneName, playerTwoName;
    private boolean isPlayer1Serving, hasPlayer1ServedFirstInSet, hasPlayer1ServedFirstInMatch;
    private boolean isTieBreaker = false, isGameOver = false, isSetOver = false;
    private boolean isHomePressed = false;

    private ArrayList<GameState> gameTrackerList = new ArrayList<>();
    private boolean didPlayer1WinGame=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        setTitle("Game");

        myDatabase = new MyDatabase(this);

        linearLayoutPlayer1 = (LinearLayout) findViewById(R.id.linear_player_1_vertical);
        linearLayoutPlayer2 = (LinearLayout) findViewById(R.id.linear_player_2_vertical);

        textViewPlayer1Name = (TextView) findViewById(R.id.text_view_player_one_name);
        textViewPlayer2Name = (TextView) findViewById(R.id.text_view_player_two_name);

        textViewPlayer1Score = (TextView) findViewById(R.id.text_view_player_one_score);
        textViewPlayer2Score = (TextView) findViewById(R.id.text_view_player_two_score);

        buttonPlayer1Point = (Button) findViewById(R.id.button_player_one_point);
        buttonPlayer2Point = (Button) findViewById(R.id.button_player_two_point);

        textViewCurrentSet = (TextView) findViewById(R.id.text_view_current_set);

        textViewPlayer1SetsWon = (TextView) findViewById(R.id.text_view_player_one_sets_won);
        textViewPlayer2SetsWon = (TextView) findViewById(R.id.text_view_player_two_sets_won);

        buttonPlayer1Point.setOnClickListener(this);
        buttonPlayer2Point.setOnClickListener(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            playerOneName = bundle.getString(Constants.KEY_PLAYER_ONE_NAME);
            playerTwoName = bundle.getString(Constants.KEY_PLAYER_TWO_NAME);
            pointsPerSet = bundle.getInt(Constants.KEY_POINTS_PER_SET);
            serviceStarts = bundle.getInt(Constants.KEY_SERVICE_STARTS);
            totalSets = bundle.getInt(Constants.KEY_TOTAL_SETS);

            if (serviceStarts == 1) {
                hasPlayer1ServedFirstInMatch = true;
                hasPlayer1ServedFirstInSet = true;
                isPlayer1Serving = true;
            } else {
                hasPlayer1ServedFirstInMatch = true;
                hasPlayer1ServedFirstInSet = false;
                isPlayer1Serving = false;
            }

            setService();

        } else {
            restoreMatchStateFromSharedPreferences();
        }

        textViewPlayer1Name.setText(playerOneName);
        textViewPlayer2Name.setText(playerTwoName);
    }

    private void restoreMatchStateFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCE_FILE_NAME, MODE_PRIVATE);

        playerOneName = sharedPreferences.getString(Constants.KEY_SP_PLAYER_1_NAME, null);
        playerTwoName = sharedPreferences.getString(Constants.KEY_SP_PLAYER_2_NAME, null);
        pointsPerSet = sharedPreferences.getInt(Constants.KEY_SP_POINTS_PER_SET, 0);
        totalSets = sharedPreferences.getInt(Constants.KEY_SP_TOTAL_SETS, 0);
        hasPlayer1ServedFirstInMatch = sharedPreferences.getBoolean(Constants.KEY_SP_HAS_PLAYER_1_SERVED_FIRST_IN_MATCH, false);

        isPlayer1Serving = sharedPreferences.getBoolean(Constants.KEY_SP_IS_PLAYER_1_SERVING, false);
        hasPlayer1ServedFirstInSet = sharedPreferences.getBoolean(Constants.KEY_SP_HAS_PLAYER_1_SERVED_FIRST_IN_SET, false);
        isTieBreaker = sharedPreferences.getBoolean(Constants.KEY_SP_IS_TIE_BREAKER, false);
        isGameOver = sharedPreferences.getBoolean(Constants.KEY_SP_IS_GAME_OVER, false);
        player1CurrentScore = sharedPreferences.getInt(Constants.KEY_SP_PLAYER_1_CURRENT_SCORE, 0);
        player2CurrentScore = sharedPreferences.getInt(Constants.KEY_SP_PLAYER_2_CURRENT_SCORE, 0);
        totalScoreCount = sharedPreferences.getInt(Constants.KEY_SP_TOTAL_SCORE_COUNT, 0);
        player1SetsWon = sharedPreferences.getInt(Constants.KEY_SP_PLAYER_1_SETS_WON, 0);
        player2SetsWon = sharedPreferences.getInt(Constants.KEY_SP_PLAYER_2_SETS_WON, 0);
        currentSet = sharedPreferences.getInt(Constants.KEY_SP_CURRENT_SET, 0);
        player1TotalPointsWon = sharedPreferences.getInt(Constants.KEY_SP_PLAYER_1_TOTAL_POINTS_WON, 0);
        player2TotalPointsWon = sharedPreferences.getInt(Constants.KEY_SP_PLAYER_2_TOTAL_POINTS_WON, 0);
        player1TotalServes = sharedPreferences.getInt(Constants.KEY_SP_PLAYER_1_TOTAL_SERVES, 0);
        player2TotalServes = sharedPreferences.getInt(Constants.KEY_SP_PLAYER_2_TOTAL_SERVES, 0);
        player1TotalPointsWonOnServes = sharedPreferences.getInt(Constants.KEY_SP_PLAYER_1_TOTAL_POINTS_WON_ON_SERVES, 0);
        player2TotalPointsWonOnServes = sharedPreferences.getInt(Constants.KEY_SP_PLAYER_2_TOTAL_POINTS_WON_ON_SERVES, 0);

        setService();
        textViewPlayer1Score.setText("" + player1CurrentScore);
        textViewPlayer2Score.setText("" + player2CurrentScore);
        textViewPlayer1SetsWon.setText("" + player1SetsWon);
        textViewPlayer2SetsWon.setText("" + player2SetsWon);
        textViewCurrentSet.setText("" + currentSet + "/" + totalSets);

        myDatabase.database = myDatabase.openReadableDatabaseInstance();
        Cursor c = myDatabase.getAllGameDetails();
        if (c.moveToFirst()) {
            do {
                int didPlayer1win = c.getInt(c.getColumnIndex(MiniSquashContract.GameStateDetailsEntry.COLUMN_DID_PLAYER_1_WIN));
                GameState gameState = new GameState(didPlayer1win);
                gameTrackerList.add(gameState);
            } while (c.moveToNext());
        }
        myDatabase.closeDatabaseConnection();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isPlayer1Serving = savedInstanceState.getBoolean(Constants.KEY_IS_PLAYER_1_SERVING);
        hasPlayer1ServedFirstInSet = savedInstanceState.getBoolean(Constants.KEY_HAS_PLAYER_1_SERVED_FIRST_IN_SET);
        isTieBreaker = savedInstanceState.getBoolean(Constants.KEY_IS_TIE_BREAKER);
        isGameOver = savedInstanceState.getBoolean(Constants.KEY_IS_GAME_OVER);
        player1CurrentScore = savedInstanceState.getInt(Constants.KEY_PLAYER_1_CURRENT_SCORE);
        player2CurrentScore = savedInstanceState.getInt(Constants.KEY_PLAYER_2_CURRENT_SCORE);
        totalScoreCount = savedInstanceState.getInt(Constants.KEY_TOTAL_SCORE_COUNT);
        player1SetsWon = savedInstanceState.getInt(Constants.KEY_PLAYER_1_SETS_WON);
        player2SetsWon = savedInstanceState.getInt(Constants.KEY_PLAYER_2_SETS_WON);
        currentSet = savedInstanceState.getInt(Constants.KEY_CURRENT_SET);
        player2TotalPointsWon = savedInstanceState.getInt(Constants.KEY_PLAYER_2_TOTAL_POINTS_WON);
        player1TotalServes = savedInstanceState.getInt(Constants.KEY_PLAYER_1_TOTAL_SERVES);
        player2TotalServes = savedInstanceState.getInt(Constants.KEY_PLAYER_2_TOTAL_SERVES);
        player1TotalPointsWonOnServes = savedInstanceState.getInt(Constants.KEY_PLAYER_1_TOTAL_POINTS_WON_ON_SERVES);
        player2TotalPointsWonOnServes = savedInstanceState.getInt(Constants.KEY_PLAYER_2_TOTAL_POINTS_WON_ON_SERVES);

        try {
            gameTrackerList = (ArrayList<GameState>) savedInstanceState.getSerializable(Constants.KEY_GAME_TRACKER_LIST);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setService();
        textViewPlayer1Score.setText("" + player1CurrentScore);
        textViewPlayer2Score.setText("" + player2CurrentScore);
        textViewPlayer1SetsWon.setText("" + player1SetsWon);
        textViewPlayer2SetsWon.setText("" + player2SetsWon);
        textViewCurrentSet.setText("" + currentSet + "/" + totalSets);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Constants.KEY_IS_PLAYER_1_SERVING, isPlayer1Serving);
        outState.putBoolean(Constants.KEY_HAS_PLAYER_1_SERVED_FIRST_IN_SET, hasPlayer1ServedFirstInSet);
        outState.putBoolean(Constants.KEY_IS_TIE_BREAKER, isTieBreaker);
        outState.putBoolean(Constants.KEY_IS_GAME_OVER, isGameOver);
        outState.putInt(Constants.KEY_PLAYER_1_CURRENT_SCORE, player1CurrentScore);
        outState.putInt(Constants.KEY_PLAYER_2_CURRENT_SCORE, player2CurrentScore);
        outState.putInt(Constants.KEY_TOTAL_SCORE_COUNT, totalScoreCount);
        outState.putInt(Constants.KEY_PLAYER_1_SETS_WON, player1SetsWon);
        outState.putInt(Constants.KEY_PLAYER_2_SETS_WON, player2SetsWon);
        outState.putInt(Constants.KEY_CURRENT_SET, currentSet);
        outState.putSerializable(Constants.KEY_GAME_TRACKER_LIST, gameTrackerList);
        outState.putInt(Constants.KEY_PLAYER_1_TOTAL_POINTS_WON, player1TotalPointsWon);
        outState.putInt(Constants.KEY_PLAYER_2_TOTAL_POINTS_WON, player2TotalPointsWon);
        outState.putInt(Constants.KEY_PLAYER_1_TOTAL_SERVES, player1TotalServes);
        outState.putInt(Constants.KEY_PLAYER_2_TOTAL_SERVES, player2TotalServes);
        outState.putInt(Constants.KEY_PLAYER_1_TOTAL_POINTS_WON_ON_SERVES, player1TotalPointsWonOnServes);
        outState.putInt(Constants.KEY_PLAYER_2_TOTAL_POINTS_WON_ON_SERVES, player2TotalPointsWonOnServes);
    }

    private void saveMatchStateInSharedPreferences(Constants.UserScreen userScreen) {

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCE_FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(Constants.KEY_SP_PLAYER_1_NAME, playerOneName);
        editor.putString(Constants.KEY_SP_PLAYER_2_NAME, playerTwoName);
        editor.putInt(Constants.KEY_SP_POINTS_PER_SET, pointsPerSet);
        editor.putInt(Constants.KEY_SP_TOTAL_SETS, totalSets);
        editor.putBoolean(Constants.KEY_SP_HAS_PLAYER_1_SERVED_FIRST_IN_MATCH, hasPlayer1ServedFirstInMatch);

        if (userScreen == Constants.UserScreen.Game) {
            editor.putBoolean(Constants.KEY_SP_IS_PLAYER_1_SERVING, isPlayer1Serving);
            editor.putBoolean(Constants.KEY_SP_HAS_PLAYER_1_SERVED_FIRST_IN_SET, hasPlayer1ServedFirstInSet);
            editor.putBoolean(Constants.KEY_SP_IS_TIE_BREAKER, isTieBreaker);
            editor.putBoolean(Constants.KEY_SP_IS_GAME_OVER, isGameOver);
            editor.putInt(Constants.KEY_SP_PLAYER_1_CURRENT_SCORE, player1CurrentScore);
            editor.putInt(Constants.KEY_SP_PLAYER_2_CURRENT_SCORE, player2CurrentScore);
            editor.putInt(Constants.KEY_SP_TOTAL_SCORE_COUNT, totalScoreCount);
            editor.putInt(Constants.KEY_SP_PLAYER_1_SETS_WON, player1SetsWon);
            editor.putInt(Constants.KEY_SP_PLAYER_2_SETS_WON, player2SetsWon);
            editor.putInt(Constants.KEY_SP_CURRENT_SET, currentSet);
            editor.putInt(Constants.KEY_SP_PLAYER_1_TOTAL_POINTS_WON, player1TotalPointsWon);
            editor.putInt(Constants.KEY_SP_PLAYER_2_TOTAL_POINTS_WON, player2TotalPointsWon);
            editor.putInt(Constants.KEY_SP_PLAYER_1_TOTAL_SERVES, player1TotalServes);
            editor.putInt(Constants.KEY_SP_PLAYER_2_TOTAL_SERVES, player2TotalServes);
            editor.putInt(Constants.KEY_SP_PLAYER_1_TOTAL_POINTS_WON_ON_SERVES, player1TotalPointsWonOnServes);
            editor.putInt(Constants.KEY_SP_PLAYER_2_TOTAL_POINTS_WON_ON_SERVES, player2TotalPointsWonOnServes);
        }

        editor.putString(Constants.KEY_SP_USER_SCREEN, userScreen.toString());

        editor.apply();

        myDatabase.database = myDatabase.openWritableDatabaseInstance();
        for (int i = 0; i < gameTrackerList.size(); i++) {
            myDatabase.insertGameDetails(gameTrackerList.get(i).didPlayer1WinThePoint());
        }
        myDatabase.closeDatabaseConnection();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_player_one_point) {
            if (isPlayer1Serving) {
                player1TotalServes++;
                player1TotalPointsWonOnServes++;
            } else {
                player2TotalServes++;
            }
            player1TotalPointsWon++;
            player1CurrentScore++;
            totalScoreCount++;
            textViewPlayer1Score.setText("" + player1CurrentScore);
            checkGameState();
            GameState gameState = new GameState(true, isTieBreaker);
            gameTrackerList.add(gameState);
        } else if (v.getId() == R.id.button_player_two_point) {
            if (isPlayer1Serving) {
                player1TotalServes++;
            } else {
                player2TotalServes++;
                player2TotalPointsWonOnServes++;
            }
            player2TotalPointsWon++;
            player2CurrentScore++;
            totalScoreCount++;
            textViewPlayer2Score.setText("" + player2CurrentScore);
            checkGameState();
            GameState gameState = new GameState(false, isTieBreaker);
            gameTrackerList.add(gameState);
        }
    }

    private void undo() {
        if (totalScoreCount == 0 || gameTrackerList.size() == 0) {
            Toast.makeText(GameActivity.this, R.string.game_info_not_available, Toast.LENGTH_SHORT).show();
        } else if (totalScoreCount > 0 && gameTrackerList.size() != 0) {
            //GameState previousGameState;
            //previousGameState = gameTrackerList.get(gameTrackerList.size()-2);
            GameState gameState = gameTrackerList.get(gameTrackerList.size() - 1);

            /*if(gameState.didGameEnterInTieBreakerMode() && !previousGameState.didGameEnterInTieBreakerMode()){
                isTieBreaker = false;
            }*/
            if (totalScoreCount == pointsPerSet * 2) {
                isTieBreaker = false;
            }

            if (gameState.didPlayer1WinThePoint()) {
                player1TotalPointsWon--;
                player1CurrentScore--;
                textViewPlayer1Score.setText("" + player1CurrentScore);
                if (isGameOver || isSetOver) {
                    isGameOver = isSetOver = false;
                    player1SetsWon--;
                }
            } else {
                player2TotalPointsWon--;
                player2CurrentScore--;
                textViewPlayer2Score.setText("" + player2CurrentScore);
                if (isGameOver || isSetOver) {
                    isGameOver = isSetOver = false;
                    player2SetsWon--;
                }
            }
            totalScoreCount--;
            gameTrackerList.remove(gameTrackerList.size() - 1);

            checkServiceChangeOnUndo();

            if (isPlayer1Serving) {
                player1TotalServes--;
                if(gameState.didPlayer1WinThePoint()){
                    player1TotalPointsWonOnServes--;
                }
            } else {
                player2TotalServes--;
                if(!gameState.didPlayer1WinThePoint()){
                    player2TotalPointsWonOnServes--;
                }
            }
        }
    }

    /**
     * Check if game has entered tie breaker mode or if
     * any player has won the set and also calls the
     * checkServiceChange() method at the end.
     */
    private void checkGameState() {
        if ((player1CurrentScore == player2CurrentScore) && player1CurrentScore >= pointsPerSet) {
            isTieBreaker = true;
        } else if (player2CurrentScore > pointsPerSet && player2CurrentScore - player1CurrentScore == 1) {
            isTieBreaker = true;
        } else if (player1CurrentScore > pointsPerSet && player1CurrentScore - player2CurrentScore == 1) {
            isTieBreaker = true;
        } else if (player1CurrentScore == pointsPerSet + 1 && !isTieBreaker) {
            player1WinsSet();
        } else if (player2CurrentScore == pointsPerSet + 1 && !isTieBreaker) {
            player2WinsSet();
        } else if (isTieBreaker && player1CurrentScore - player2CurrentScore == 2) {
            player1WinsSet();
        } else if (isTieBreaker && player2CurrentScore - player1CurrentScore == 2) {
            player2WinsSet();
        } else if(pointsPerSet == 10 && player1CurrentScore == 5 && player2CurrentScore == 0){
            player1WinsSet();
        } else if(pointsPerSet == 10 && player2CurrentScore == 5 && player1CurrentScore == 0){
            player2WinsSet();
        }else if(pointsPerSet == 20 && player1CurrentScore == 7 && player2CurrentScore == 0){
            player1WinsSet();
        } else if(pointsPerSet == 20 && player2CurrentScore == 7 && player1CurrentScore == 0){
            player2WinsSet();
        }

        checkServiceChange();
    }

    /**
     * Increments the number of sets won by player 2,
     * and checks if player 2 has won the match.
     * Then calls the alert dialog method.
     */
    private void player2WinsSet() {
        isSetOver = true;
        player2SetsWon++;

        if (player2SetsWon == (totalSets / 2) + 1) {
            isGameOver = true;
            didPlayer1WinGame = false;
            showAlertDialog(playerTwoName, "has won the match. Click on proceed to continue to see the match stats!");
        } else {
            showAlertDialog(playerTwoName, "has won this set. Click on proceed to continue to the next set!");
        }
    }

    /**
     * Increments the number of sets won by player 1,
     * and checks if player 1 has won the match.
     * Then calls the alert dialog method.
     */
    private void player1WinsSet() {
        isSetOver = true;
        player1SetsWon++;

        if (player1SetsWon == (totalSets / 2) + 1) {
            isGameOver = true;
            didPlayer1WinGame = true;
            showAlertDialog(playerOneName, "has won the match. Click on proceed to continue to see the match stats!");
        } else {
            showAlertDialog(playerOneName, "has won this set. Click on proceed to continue to the next set!");
        }
    }

    /**
     * Shows an alert dialog with the name and alert message and then
     * calls the newSet() method if the game is not over. Else calls the
     * GameStats activity and finishes this method.
     *
     * @param name    name of the player
     * @param message message to be displayed in dialog
     */
    private void showAlertDialog(String name, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(name + " " + message);
        alertDialogBuilder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if (!isGameOver) {
                    newSet();
                } else {
                    // New activity
                    // TASK FOR: FRIDAY
                    Intent in = new Intent(GameActivity.this, MatchStatisticsActivity.class);
                    in.putExtra(Constants.KEY_POINTS_PER_SET, pointsPerSet);
                    in.putExtra(Constants.KEY_TOTAL_SETS, totalSets);
                    if(didPlayer1WinGame){
                        in.putExtra(Constants.KEY_WINNER_NAME, playerOneName);
                        in.putExtra(Constants.KEY_WINNER_TOTAL_POINTS, player1TotalPointsWon);
                        in.putExtra(Constants.KEY_WINNER_SETS_WON, player1SetsWon);
                        in.putExtra(Constants.KEY_WINNER_TOTAL_SERVES, player1TotalServes);
                        in.putExtra(Constants.KEY_WINNER_TOTAL_POINTS_WON_ON_SERVES, player1TotalPointsWonOnServes);
                        in.putExtra(Constants.KEY_LOSER_NAME, playerTwoName);
                        in.putExtra(Constants.KEY_LOSER_TOTAL_POINTS, player2TotalPointsWon);
                        in.putExtra(Constants.KEY_LOSER_SETS_WON, player2SetsWon);
                        in.putExtra(Constants.KEY_LOSER_TOTAL_SERVES, player2TotalServes);
                        in.putExtra(Constants.KEY_LOSER_TOTAL_POINTS_WON_ON_SERVES, player2TotalPointsWonOnServes);
                        if(serviceStarts == 1){
                            in.putExtra(Constants.KEY_SERVICE_STARTED_BY_WINNER, 1);
                        } else {
                            in.putExtra(Constants.KEY_SERVICE_STARTED_BY_WINNER, 0);
                        }
                    } else {
                        in.putExtra(Constants.KEY_WINNER_NAME, playerTwoName);
                        in.putExtra(Constants.KEY_WINNER_TOTAL_POINTS, player2TotalPointsWon);
                        in.putExtra(Constants.KEY_WINNER_SETS_WON, player2SetsWon);
                        in.putExtra(Constants.KEY_WINNER_TOTAL_SERVES, player2TotalServes);
                        in.putExtra(Constants.KEY_WINNER_TOTAL_POINTS_WON_ON_SERVES, player2TotalPointsWonOnServes);
                        in.putExtra(Constants.KEY_LOSER_NAME, playerOneName);
                        in.putExtra(Constants.KEY_LOSER_TOTAL_POINTS, player1TotalPointsWon);
                        in.putExtra(Constants.KEY_LOSER_SETS_WON, player1SetsWon);
                        in.putExtra(Constants.KEY_LOSER_TOTAL_SERVES, player1TotalServes);
                        in.putExtra(Constants.KEY_LOSER_TOTAL_POINTS_WON_ON_SERVES, player1TotalPointsWonOnServes);
                        if(serviceStarts == 2){
                            in.putExtra(Constants.KEY_SERVICE_STARTED_BY_WINNER, 1);
                        } else {
                            in.putExtra(Constants.KEY_SERVICE_STARTED_BY_WINNER, 0);
                        }
                    }
                    startActivity(in);
                    saveMatchStateInSharedPreferences(Constants.UserScreen.Statistics);
                    finish();
                    /*Intent in = new Intent(GameActivity.this, HomeActivity.class);
                    startActivity(in);
                    finish();*/
                }
            }
        }).setNegativeButton("Undo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                undo();
            }
        }).setCancelable(false);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * Resets the current score variables and their corresponding
     * text views, and switches the service for the new set.
     */
    private void newSet() {
        gameTrackerList.clear();

        deleteAllGameDetailsFromDatabase();

        isTieBreaker = isSetOver = false;
        player1CurrentScore = 0;
        player2CurrentScore = 0;
        totalScoreCount = 0;
        currentSet++;
        textViewPlayer1Score.setText("" + player1CurrentScore);
        textViewPlayer2Score.setText("" + player2CurrentScore);

        textViewPlayer1SetsWon.setText("" + player1SetsWon);
        textViewPlayer2SetsWon.setText("" + player2SetsWon);
        textViewCurrentSet.setText("" + currentSet + "/" + totalSets);

        if (hasPlayer1ServedFirstInSet) {
            isPlayer1Serving = true;
            hasPlayer1ServedFirstInSet = false;
        } else {
            isPlayer1Serving = false;
            hasPlayer1ServedFirstInSet = true;
        }
        switchService();
    }

    private void deleteAllGameDetailsFromDatabase() {
        myDatabase.database = myDatabase.openWritableDatabaseInstance();
        myDatabase.deleteAllGameDetails();
        myDatabase.closeDatabaseConnection();
    }

    private void restartSet() {
        gameTrackerList.clear();
        deleteAllGameDetailsFromDatabase();

        isTieBreaker = false;
        player1CurrentScore = 0;
        player2CurrentScore = 0;
        totalScoreCount = 0;
        textViewPlayer1Score.setText("" + player1CurrentScore);
        textViewPlayer2Score.setText("" + player2CurrentScore);

        textViewPlayer1SetsWon.setText("" + player1SetsWon);
        textViewPlayer2SetsWon.setText("" + player2SetsWon);
        textViewCurrentSet.setText("" + currentSet + "/" + totalSets);

        if (hasPlayer1ServedFirstInSet) {
            isPlayer1Serving = false;
            hasPlayer1ServedFirstInSet = true;
        } else {
            isPlayer1Serving = true;
            hasPlayer1ServedFirstInSet = false;
        }
        switchService();
    }

    private void restartMatch() {

        gameTrackerList.clear();
        deleteAllGameDetailsFromDatabase();

        isTieBreaker = false;
        player1TotalPointsWon = player2TotalPointsWon = player1CurrentScore = player2CurrentScore =
                player1SetsWon = player2SetsWon = totalScoreCount = 0;
        currentSet = 1;

        textViewPlayer1Score.setText("" + player1CurrentScore);
        textViewPlayer2Score.setText("" + player2CurrentScore);

        textViewPlayer1SetsWon.setText("" + player1SetsWon);
        textViewPlayer2SetsWon.setText("" + player2SetsWon);
        textViewCurrentSet.setText("" + currentSet + "/" + totalSets);

        if (hasPlayer1ServedFirstInMatch) {
            isPlayer1Serving = false;
            hasPlayer1ServedFirstInSet = true;
        } else {
            isPlayer1Serving = true;
            hasPlayer1ServedFirstInSet = false;
        }
        switchService();
    }

    /**
     * Check if the service has to be changed and call
     * the switchService() method.
     */
    private void checkServiceChange() {
        if (pointsPerSet == 20) {
            if (isTieBreaker)
                switchService();
            else if (totalScoreCount % ((pointsPerSet + 5) / 5) == 0) {
                switchService();
            }
        } else {
            if (isTieBreaker)
                switchService();
            else if (totalScoreCount % (pointsPerSet / 5) == 0) {
                switchService();
            }
        }
    }

    private void checkServiceChangeOnUndo() {
        if (pointsPerSet == 20) {
            if (isTieBreaker) {
                switchService();
            } else if ((totalScoreCount + 1) % ((pointsPerSet + 5) / 5) == 0) {
                switchService();
            }
        } else {
            if (isTieBreaker) {
                switchService();
            } else if ((totalScoreCount + 1) % (pointsPerSet / 5) == 0) {
                switchService();
            }
        }
    }

    /**
     * Switches the service from who ever is currently serving
     * and sets the background.
     */
    private void switchService() {
        if (isPlayer1Serving) {
            isPlayer1Serving = false;
            linearLayoutPlayer2.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
            linearLayoutPlayer1.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        } else {
            isPlayer1Serving = true;
            linearLayoutPlayer2.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
            linearLayoutPlayer1.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
        }
    }

    /**
     * Sets the background of the current service holder.
     * Called for the first time in onCreate() and also in onRestoreInstanceState().
     */
    private void setService() {
        if (isPlayer1Serving) {
            linearLayoutPlayer2.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
            linearLayoutPlayer1.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
        } else {
            linearLayoutPlayer2.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
            linearLayoutPlayer1.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        textViewCurrentSet.setText("" + currentSet + "/" + totalSets);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean handled = false;

        switch (item.getItemId()) {
            case R.id.action_undo:
                handled = true;
                undo();
                break;
            case R.id.action_restart_set:
                handled = true;
                restartSet();
                break;
            case R.id.action_restart_match:
                handled = true;
                restartMatch();
                break;
            case android.R.id.home:
                handled = true;
                onHomePressed();
        }
        return handled;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Bundle outState = new Bundle();
        onSaveInstanceState(outState);
    }

    private void onHomePressed() {
        isHomePressed = true;
        saveMatchStateInSharedPreferences(Constants.UserScreen.Home);
        Intent intent = new Intent(GameActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveMatchStateInSharedPreferences(Constants.UserScreen.Game);
    }
}
