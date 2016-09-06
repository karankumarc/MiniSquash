package com.projects.karan.minisquash.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.projects.karan.minisquash.R;
import com.projects.karan.minisquash.utils.Constants;

public class HomeActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener{

    EditText editTextPlayerOne, editTextPlayerTwo;
    RadioGroup radioGroupPoints, radioGroupService;
    AppCompatSpinner spinnerSets;
    Button buttonStart;
    String[] setsArray = {"1","3", "5","7"};
    int pointsPerSet =0, serviceStarts = 0, totalSets = 1;
    private boolean hasPlayer1ServedFirstInMatch = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if(checkIfGameScreenShouldBeOpened()){
            Intent intent = new Intent(HomeActivity.this, GameActivity.class);
            startActivity(intent);
            finish();
        }

        editTextPlayerOne = (EditText) findViewById(R.id.edit_text_player_one);
        editTextPlayerTwo = (EditText) findViewById(R.id.edit_text_player_two);

        radioGroupPoints = (RadioGroup) findViewById(R.id.rg_points);
        radioGroupService = (RadioGroup) findViewById(R.id.rg_service);

        radioGroupService.setOnCheckedChangeListener(this);
        radioGroupPoints.setOnCheckedChangeListener(this);

        spinnerSets = (AppCompatSpinner) findViewById(R.id.spinnerSets);

        buttonStart = (Button) findViewById(R.id.button_start_game);

        spinnerSets.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, setsArray));

        spinnerSets.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                totalSets = Integer.parseInt(setsArray[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                totalSets = 1;
            }
        });

        buttonStart.setOnClickListener(this);

        restoreFromSharedPreferences();
    }

    private boolean checkIfGameScreenShouldBeOpened() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCE_FILE_NAME, MODE_PRIVATE);

        try {
            if(sharedPreferences.getString(Constants.KEY_SP_USER_SCREEN,null).equals(Constants.UserScreen.Game.toString())){
                return true;
            } else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void restoreFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCE_FILE_NAME, MODE_PRIVATE);
        //SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            if(sharedPreferences.getString(Constants.KEY_SP_USER_SCREEN,null).equals(Constants.UserScreen.Home.toString())){
                editTextPlayerOne.setText(sharedPreferences.getString(Constants.KEY_SP_PLAYER_1_NAME, null));
                editTextPlayerTwo.setText(sharedPreferences.getString(Constants.KEY_SP_PLAYER_2_NAME, null));
                int pointsPerSet = sharedPreferences.getInt(Constants.KEY_SP_POINTS_PER_SET, 0);
                restorePointsPerSet(pointsPerSet);
                int totalSets = sharedPreferences.getInt(Constants.KEY_SP_TOTAL_SETS, 0);
                restoreTotalSets(totalSets);
                boolean hasPlayer1ServedFirstInMatch = sharedPreferences.getBoolean(Constants.KEY_SP_HAS_PLAYER_1_SERVED_FIRST_IN_MATCH, true);
                restoreService(hasPlayer1ServedFirstInMatch);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void restoreService(boolean hasPlayer1ServedFirstInMatch) {
        if(hasPlayer1ServedFirstInMatch)
            radioGroupService.check(R.id.rb_service_one);
        else
            radioGroupService.check(R.id.rb_service_two);
    }

    private void restoreTotalSets(int totalSets) {
        switch (totalSets){
            case 1:
                spinnerSets.setSelection(0);
                break;
            case 3:
                spinnerSets.setSelection(1);
                break;
            case 5:
                spinnerSets.setSelection(2);
                break;
            case 7:
                spinnerSets.setSelection(3);
                break;
        }
    }

    private void restorePointsPerSet(int pointsPerSet) {
        switch (pointsPerSet){
            case 5:
                radioGroupPoints.check(R.id.rb_points_five);
                break;
            case 10:
                radioGroupPoints.check(R.id.rb_points_ten);
                break;
            case 20:
                radioGroupPoints.check(R.id.rb_points_twenty);
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.rb_points_five:
                pointsPerSet = 5;
                break;
            case R.id.rb_points_ten:
                pointsPerSet = 10;
                break;
            case R.id.rb_points_twenty:
                pointsPerSet = 20;
                break;
            case R.id.rb_service_one:
                hasPlayer1ServedFirstInMatch = true;
                serviceStarts = 1;
                break;
            case R.id.rb_service_two:
                hasPlayer1ServedFirstInMatch = false;
                serviceStarts = 2;
                break;
            default:
                pointsPerSet = 0;
                serviceStarts = 0;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveMatchStateInSharedPreferences(Constants.UserScreen.Home);
    }

    private void saveMatchStateInSharedPreferences(Constants.UserScreen userScreen) {

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCE_FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(Constants.KEY_SP_PLAYER_1_NAME, editTextPlayerOne.getText().toString());
        editor.putString(Constants.KEY_SP_PLAYER_2_NAME, editTextPlayerTwo.getText().toString());
        editor.putInt(Constants.KEY_SP_POINTS_PER_SET, pointsPerSet);
        editor.putInt(Constants.KEY_SP_TOTAL_SETS, totalSets);
        editor.putBoolean(Constants.KEY_SP_HAS_PLAYER_1_SERVED_FIRST_IN_MATCH, hasPlayer1ServedFirstInMatch);

        editor.putString(Constants.KEY_SP_USER_SCREEN, userScreen.toString());

        editor.apply();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_clear_data:
                clearData();
                break;
            case R.id.action_quit:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearData() {
        editTextPlayerOne.requestFocus();
        editTextPlayerOne.setText("");
        editTextPlayerTwo.setText("");
        radioGroupPoints.clearCheck();
        radioGroupService.clearCheck();
        spinnerSets.setSelection(0);
    }

    @Override
    public void onClick(View v) {
        String player1Name = editTextPlayerOne.getText().toString().trim();
        String player2Name = editTextPlayerTwo.getText().toString().trim();

        if(!player1Name.isEmpty() && !player2Name.isEmpty() && serviceStarts != 0 && pointsPerSet != 0){
            Intent intent = new Intent(HomeActivity.this, GameActivity.class);
            intent.putExtra(Constants.KEY_PLAYER_ONE_NAME,player1Name);
            intent.putExtra(Constants.KEY_PLAYER_TWO_NAME,player2Name);
            intent.putExtra(Constants.KEY_POINTS_PER_SET, pointsPerSet);
            intent.putExtra(Constants.KEY_SERVICE_STARTS, serviceStarts);
            intent.putExtra(Constants.KEY_TOTAL_SETS, totalSets);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(HomeActivity.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
        }

    }
}
