package com.projects.karan.minisquash.ui;

import android.database.Cursor;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.projects.karan.minisquash.R;
import com.projects.karan.minisquash.data.MiniSquashContract;
import com.projects.karan.minisquash.data.MyDatabase;
import com.projects.karan.minisquash.model.Match;
import com.projects.karan.minisquash.model.Player;
import com.projects.karan.minisquash.utils.Utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class PlayerHistoryActivity extends AppCompatActivity {

    MyDatabase myDatabase;

    ListView listView;
    MyAdapter myAdapter;
    ArrayList<Player> playerArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_history);

        setTitle("Player History");

        myDatabase = new MyDatabase(this);

        listView = (ListView) findViewById(R.id.list_view_player_history);
        myAdapter = new MyAdapter();

        myDatabase.database = myDatabase.openReadableDatabaseInstance();
        try {
            Cursor c = myDatabase.getPlayerHistory();
            if(c.moveToFirst()){
                do{
                    //String stringDate = Utils.SIMPLE_DATE_FORMAT.format(date.getTime());
                    Player player = new Player(c.getInt(0), c.getString(1), c.getInt(2), c.getInt(3));
                    playerArrayList.add(player);
                }while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            myDatabase.closeDatabaseConnection();
        }
        listView.setAdapter(myAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return playerArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return playerArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Player player = playerArrayList.get(position);

            View v = getLayoutInflater().inflate(R.layout.history_player_row, parent, false);

            TextView textViewName = (TextView) v.findViewById(R.id.text_view_player_name);
            TextView textViewMatchesPlayed = (TextView) v.findViewById(R.id.text_view_player_matches_played);
            TextView textViewWinPercent = (TextView) v.findViewById(R.id.text_view_player_matches_won);

            textViewName.setText(player.getName());
            textViewMatchesPlayed.setText(""+player.getMatchesPlayed());
            if((double)player.getMatchesPlayed()==0){
                textViewWinPercent.setText(""+0);
            } else {
                textViewWinPercent.setText(""+ Utils.round(((double)player.getMatchesWon()/(double)player.getMatchesPlayed())*100,2));
            }

            return v;
        }
    }


}
