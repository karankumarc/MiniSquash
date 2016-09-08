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
import com.projects.karan.minisquash.data.MiniSquashContract.MatchDetailsEntry;
import com.projects.karan.minisquash.R;
import com.projects.karan.minisquash.data.MyDatabase;
import com.projects.karan.minisquash.model.Match;

import java.util.ArrayList;

public class GameHistoryActivity extends AppCompatActivity {

    MyDatabase myDatabase;

    ListView listView;
    MyAdapter myAdapter;
    ArrayList<Match> matchArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_history);

        myDatabase = new MyDatabase(this);

        listView = (ListView) findViewById(R.id.list_view_game_history);
        myAdapter = new MyAdapter();

        myDatabase.database = myDatabase.openReadableDatabaseInstance();
        try {
            Cursor c = myDatabase.getHistory();
            if(c.moveToFirst()){
                do{
                    String date = c.getString(c.getColumnIndex(MatchDetailsEntry.COLUMN_DATE));
                    //String stringDate = Utils.SIMPLE_DATE_FORMAT.format(date.getTime());
                    Match match = new Match(c.getInt(c.getColumnIndex(MatchDetailsEntry._ID)),c.getInt(c.getColumnIndex(MatchDetailsEntry.COLUMN_WINNER_SETS_WON)),
                            c.getInt(c.getColumnIndex(MatchDetailsEntry.COLUMN_LOSER_SETS_WON)), c.getString(c.getColumnIndex(MatchDetailsEntry.COLUMN_WINNER_NAME)),
                            c.getString(c.getColumnIndex(MatchDetailsEntry.COLUMN_LOSER_NAME)),
                            date);
                    matchArrayList.add(match);
                }while (c.moveToNext());
            }
            myDatabase.closeDatabaseConnection();
        } catch (Exception e) {
            e.printStackTrace();
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

    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return matchArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return matchArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Match match = matchArrayList.get(position);

            View v = getLayoutInflater().inflate(R.layout.history_match_row, parent, false);

            TextView textViewTime = (TextView) v.findViewById(R.id.text_view_timestamp_row);
            TextView textViewWinnerName = (TextView) v.findViewById(R.id.text_view_winner_name_row);
            TextView textViewLoserName = (TextView) v.findViewById(R.id.text_view_loser_name_row);
            TextView textViewFinalScore = (TextView) v.findViewById(R.id.text_view_final_score_row);

            textViewTime.setText(match.getTime());
            textViewWinnerName.setText(match.getWinnerName());
            textViewLoserName.setText(match.getLoserName());
            textViewFinalScore.setText(""+match.getWinnerSetsWon()+" - "+match.getLoserSetsWon());

            return v;
        }
    }
}
