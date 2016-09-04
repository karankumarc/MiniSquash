package com.projects.karan.minisquash;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.projects.karan.minisquash.utils.Constants;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout linearLayoutPlayer1, linearLayoutPlayer2;
    TextView textViewPlayer1Name, textViewPlayer2Name, textViewPlayer1Score, textViewPlayer2Score, textViewCurrentSet,
            textViewPlayer1SetsWon, textViewPlayer2SetsWon;
    Button buttonPlayer1Point, buttonPlayer2Point;

    int pointsPerSet = 0, serviceStarts = 0, totalSets = 1, player1CurrentScore = 0, player2CurrentScore = 0,
            totalScoreCount = 0, player1SetsWon = 0, player2SetsWon = 0, currentSet = 1;
    String playerOneName, playerTwoName;
    boolean isPlayer1Serving, hasPlayer1StartedServing;
    private boolean isTieBreaker = false, isGameOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        playerOneName = bundle.getString(Constants.KEY_PLAYER_ONE_NAME);
        playerTwoName = bundle.getString(Constants.KEY_PLAYER_TWO_NAME);
        pointsPerSet = bundle.getInt(Constants.KEY_POINTS_PER_SET);
        serviceStarts = bundle.getInt(Constants.KEY_SERVICE_STARTS);
        totalSets = bundle.getInt(Constants.KEY_TOTAL_SETS);


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

        textViewPlayer1Name.setText(playerOneName);
        textViewPlayer2Name.setText(playerTwoName);


        if (serviceStarts == 1) {
            hasPlayer1StartedServing = true;
            isPlayer1Serving = true;
        } else {
            hasPlayer1StartedServing = false;
            isPlayer1Serving = false;
        }

        setService();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isPlayer1Serving = savedInstanceState.getBoolean(Constants.KEY_IS_PLAYER_1_SERVING);
        hasPlayer1StartedServing = savedInstanceState.getBoolean(Constants.KEY_HAS_PLAYER_1_STARTED_SERVING);
        isTieBreaker = savedInstanceState.getBoolean(Constants.KEY_IS_TIE_BREAKER);
        isGameOver = savedInstanceState.getBoolean(Constants.KEY_IS_GAME_OVER);
        player1CurrentScore = savedInstanceState.getInt(Constants.KEY_PLAYER_ONE_CURRENT_SCORE);
        player2CurrentScore = savedInstanceState.getInt(Constants.KEY_PLAYER_TWO_CURRENT_SCORE);
        totalScoreCount = savedInstanceState.getInt(Constants.KEY_TOTAL_SCORE_COUNT);
        player1SetsWon = savedInstanceState.getInt(Constants.KEY_PLAYER_1_SETS_WON);
        player2SetsWon = savedInstanceState.getInt(Constants.KEY_PLAYER_2_SETS_WON);
        currentSet = savedInstanceState.getInt(Constants.KEY_CURRENT_SET);

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
        outState.putBoolean(Constants.KEY_HAS_PLAYER_1_STARTED_SERVING, hasPlayer1StartedServing);
        outState.putBoolean(Constants.KEY_IS_TIE_BREAKER, isTieBreaker);
        outState.putBoolean(Constants.KEY_IS_GAME_OVER, isGameOver);
        outState.putInt(Constants.KEY_PLAYER_ONE_CURRENT_SCORE, player1CurrentScore);
        outState.putInt(Constants.KEY_PLAYER_TWO_CURRENT_SCORE, player2CurrentScore);
        outState.putInt(Constants.KEY_TOTAL_SCORE_COUNT, totalScoreCount);
        outState.putInt(Constants.KEY_PLAYER_1_SETS_WON, player1SetsWon);
        outState.putInt(Constants.KEY_PLAYER_2_SETS_WON, player2SetsWon);
        outState.putInt(Constants.KEY_CURRENT_SET, currentSet);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_player_one_point) {
            player1CurrentScore++;
            totalScoreCount++;
            textViewPlayer1Score.setText("" + player1CurrentScore);
            checkGameState();

        } else if (v.getId() == R.id.button_player_two_point) {
            player2CurrentScore++;
            totalScoreCount++;
            textViewPlayer2Score.setText("" + player2CurrentScore);
            checkGameState();
        }
    }

    /**
     * Check if game has entered tie breaker mode or if
     * any player has won the set and also calls the
     * checkServiceChange() method at the end.
     */
    private void checkGameState() {
        if ((player1CurrentScore == player2CurrentScore) && player1CurrentScore == pointsPerSet) {
            isTieBreaker = true;
        } else if (player1CurrentScore == pointsPerSet + 1 && isTieBreaker == false) {
            player1WinsSet();
        } else if (player2CurrentScore == pointsPerSet + 1 && isTieBreaker == false) {
            player2WinsSet();
        } else if (isTieBreaker && player1CurrentScore - player2CurrentScore == 2) {
            player1WinsSet();
        } else if (isTieBreaker && player2CurrentScore - player1CurrentScore == 2) {
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
        player2SetsWon++;

        if (player2SetsWon == (totalSets / 2) + 1) {
            isGameOver = true;
            showAlertDialog(playerTwoName, "has won the match. Click on proceed to continue to see the match stats!");
        } else {
            currentSet++;
            showAlertDialog(playerTwoName, "has won this set. Click on proceed to continue to the next set!");
        }
    }

    /**
     * Increments the number of sets won by player 1,
     * and checks if player 1 has won the match.
     * Then calls the alert dialog method.
     */
    private void player1WinsSet() {
        player1SetsWon++;

        if (player1SetsWon == (totalSets / 2) + 1) {
            isGameOver = true;
            showAlertDialog(playerOneName, "has won the match. Click on proceed to continue to see the match stats!");
        } else {
            currentSet++;
            showAlertDialog(playerOneName, "has won this set. Click on proceed to continue to the next set!");
        }
    }

    /** Shows an alert dialog with the name and alert message and then
     * calls the newSet() method if the game is not over. Else calls the
     * GameStats activity and finishes this method.
     * @param name name of the player
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
                    // TASK FOR: 5th September
                    /*Intent in = new Intent(GameActivity.this, HomeActivity.class);
                    startActivity(in);
                    finish();*/
                }
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
        isTieBreaker = false;
        player1CurrentScore = 0;
        player2CurrentScore = 0;
        totalScoreCount = 0;
        textViewPlayer1Score.setText("" + player1CurrentScore);
        textViewPlayer2Score.setText("" + player2CurrentScore);

        textViewPlayer1SetsWon.setText("" + player1SetsWon);
        textViewPlayer2SetsWon.setText("" + player2SetsWon);
        textViewCurrentSet.setText("" + currentSet + "/" + totalSets);

        if (hasPlayer1StartedServing) {
            isPlayer1Serving = true;
            hasPlayer1StartedServing = false;
        } else {
            isPlayer1Serving = false;
            hasPlayer1StartedServing = true;
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

}
