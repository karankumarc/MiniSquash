package com.projects.karan.minisquash;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.projects.karan.minisquash.utils.Constants;

public class HomeActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{

    EditText editTextPlayerOne, editTextPlayerTwo;
    RadioGroup radioGroupPoints, radioGroupService;
    RadioButton radioButtonServiceOne, radioButtonServiceTwo;
    AppCompatSpinner spinnerSets;
    Button buttonStart;
    String[] setsArray = {"1","3", "5","7"};
    int pointsPerSet =0, serviceStarts = 0, totalSets = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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

        buttonStart.setOnClickListener(new View.OnClickListener() {
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
                } else {
                    Toast.makeText(HomeActivity.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
                }

            }
        });

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
                serviceStarts = 1;
                break;
            case R.id.rb_service_two:
                serviceStarts = 2;
                break;
            default:
                pointsPerSet = 0;
                serviceStarts = 0;
        }
    }

}
