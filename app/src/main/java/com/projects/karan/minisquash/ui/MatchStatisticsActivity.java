package com.projects.karan.minisquash.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.projects.karan.minisquash.R;
import com.projects.karan.minisquash.data.MyDatabase;
import com.projects.karan.minisquash.model.GameState;
import com.projects.karan.minisquash.utils.Constants;
import com.projects.karan.minisquash.utils.Utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

public class MatchStatisticsActivity extends AppCompatActivity {

    MyDatabase myDatabase;
    TextView textViewWinnerName, textViewLoserName, textViewWinnerPointsWonInService, textViewLoserPointsWonInService,
            textViewWinnerPointsWonInReceiving, textViewLoserPointsWonInReceiving, textViewWinnerTotalPointsWon,
            textViewLoserTotalPointsWon, textViewWinnerSetsWon, textViewLoserSetsWon;
    Button buttonNewMatch;
    private int pointsPerSet, totalSets, winnerTotalPointsWon, winnerSetsWon, winnerTotalServes, winnerTotalPointsWonOnServes,
        loserTotalPointsWon, loserSetsWon, loserTotalServes, loserTotalPointsWonOnServes, serviceStartedByWinner;
    private String winnerName, loserName;
    long unitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_statistics);

        changeSharedPreferenceState(Constants.UserScreen.Home);

        myDatabase = new MyDatabase(this);

        setTitle("Match Statistics");

        //region Initialization
        textViewWinnerName = (TextView) findViewById(R.id.text_view_winner_name);
        textViewLoserName = (TextView) findViewById(R.id.text_view_loser_name);
        textViewWinnerPointsWonInService = (TextView) findViewById(R.id.text_view_winner_points_won_in_service);
        textViewLoserPointsWonInService = (TextView) findViewById(R.id.text_view_loser_points_won_in_service);
        textViewWinnerPointsWonInReceiving = (TextView) findViewById(R.id.text_view_winner_points_won_in_receiving);
        textViewLoserPointsWonInReceiving = (TextView) findViewById(R.id.text_view_loser_points_won_in_receiving);
        textViewWinnerTotalPointsWon = (TextView) findViewById(R.id.text_view_winner_total_points_won);
        textViewLoserTotalPointsWon = (TextView) findViewById(R.id.text_view_loser_total_points_won);
        textViewWinnerSetsWon = (TextView) findViewById(R.id.text_view_winner_sets_won);
        textViewLoserSetsWon = (TextView) findViewById(R.id.text_view_loser_sets_won);
        buttonNewMatch = (Button) findViewById(R.id.button_new_match);
        //endregion

        Intent in = getIntent();
        Bundle bundle = in.getExtras();

        pointsPerSet = bundle.getInt(Constants.KEY_POINTS_PER_SET);
        totalSets = bundle.getInt(Constants.KEY_TOTAL_SETS);
        winnerName = bundle.getString(Constants.KEY_WINNER_NAME);
        winnerTotalPointsWon = bundle.getInt(Constants.KEY_WINNER_TOTAL_POINTS);
        winnerSetsWon = bundle.getInt(Constants.KEY_WINNER_SETS_WON);
        winnerTotalServes = bundle.getInt(Constants.KEY_WINNER_TOTAL_SERVES);
        winnerTotalPointsWonOnServes = bundle.getInt(Constants.KEY_WINNER_TOTAL_POINTS_WON_ON_SERVES);
        loserName = bundle.getString(Constants.KEY_LOSER_NAME);
        loserTotalPointsWon = bundle.getInt(Constants.KEY_LOSER_TOTAL_POINTS);
        loserSetsWon = bundle.getInt(Constants.KEY_LOSER_SETS_WON);
        loserTotalServes = bundle.getInt(Constants.KEY_LOSER_TOTAL_SERVES);
        loserTotalPointsWonOnServes = bundle.getInt(Constants.KEY_LOSER_TOTAL_POINTS_WON_ON_SERVES);
        serviceStartedByWinner = bundle.getInt(Constants.KEY_SERVICE_STARTED_BY_WINNER);

        double winnerServicePercentageWon = round(((double) winnerTotalPointsWonOnServes/ (double) winnerTotalServes)* 100,2);
        double loserServicePercentageWon = round(((double) loserTotalPointsWonOnServes/ (double) loserTotalServes)* 100,2);
        double winnerReceivingPercentageWon = round(100 - loserServicePercentageWon,2);
        double loserReceivingPercentageWon = round(100 - winnerServicePercentageWon,2);

        textViewWinnerName.setText(winnerName);
        textViewLoserName.setText(loserName);
        textViewWinnerPointsWonInService.setText(""+winnerServicePercentageWon+"%");
        textViewLoserPointsWonInService.setText(""+loserServicePercentageWon+"%");
        textViewWinnerPointsWonInReceiving.setText(""+winnerReceivingPercentageWon+"%");
        textViewLoserPointsWonInReceiving.setText(""+loserReceivingPercentageWon+"%");
        textViewWinnerTotalPointsWon.setText(""+winnerTotalPointsWon);
        textViewLoserTotalPointsWon.setText(""+loserTotalPointsWon);
        textViewWinnerSetsWon.setText(""+winnerSetsWon);
        textViewLoserSetsWon.setText(""+loserSetsWon);

        /*Date currentDate = new Date();
        unitTime = currentDate.getTime() / 1000L;*/
        Date d=new Date();
        Utils.SIMPLE_DATE_FORMAT.format(d);

        myDatabase.database = myDatabase.openWritableDatabaseInstance();

        long winnerId = myDatabase.getIdIfPlayerExists(winnerName);
        long loserId = myDatabase.getIdIfPlayerExists(loserName);

        int[] winnerDetails = myDatabase.getPlayerMatchesPlayedAndWon(winnerId);
        myDatabase.setPlayerMatchesPlayedAndWon(winnerId, ++winnerDetails[0], ++winnerDetails[1]);
        int[] loserDetails = myDatabase.getPlayerMatchesPlayedAndWon(loserId);
        myDatabase.setPlayerMatchesPlayedAndWon(loserId, ++loserDetails[0], loserDetails[1]);

        if(winnerId != -1 || loserId != -1){
            myDatabase.insertMatchDetails(Utils.SIMPLE_DATE_FORMAT.format(d),Utils.SIMPLE_HOUR_FORMAT.format(d) ,pointsPerSet, serviceStartedByWinner, totalSets,
                    winnerName, winnerTotalPointsWon, winnerSetsWon, winnerTotalServes, winnerTotalPointsWonOnServes,
                    loserName,loserTotalPointsWon, loserSetsWon, loserTotalServes, loserTotalPointsWonOnServes, winnerId, loserId);
        } else {
            Toast.makeText(MatchStatisticsActivity.this, "Did not save match details due to error!", Toast.LENGTH_SHORT).show();
        }
        myDatabase.closeDatabaseConnection();

        buttonNewMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MatchStatisticsActivity.this, HomeActivity.class);
                changeSharedPreferenceState(Constants.UserScreen.Home);
                startActivity(in);
                finish();
            }
        });
    }

    private void changeSharedPreferenceState(Constants.UserScreen userScreen) {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCE_FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.KEY_SP_USER_SCREEN, userScreen.toString());
        editor.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        changeSharedPreferenceState(Constants.UserScreen.Home);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
