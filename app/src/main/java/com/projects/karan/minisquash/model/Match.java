package com.projects.karan.minisquash.model;

/**
 * Created by ADMIN on 9/6/2016.
 */
public class Match {

    private int id, winnerSetsWon, loserSetsWon;
    private String winnerName, loserName, time;

    public Match(int id, int winnerSetsWon, int loserSetsWon, String winnerName, String loserName, String time) {
        this.id = id;
        this.winnerSetsWon = winnerSetsWon;
        this.loserSetsWon = loserSetsWon;
        this.winnerName = winnerName;
        this.loserName = loserName;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public int getWinnerSetsWon() {
        return winnerSetsWon;
    }

    public int getLoserSetsWon() {
        return loserSetsWon;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public String getLoserName() {
        return loserName;
    }

    public String getTime() {
        return time;
    }
}
