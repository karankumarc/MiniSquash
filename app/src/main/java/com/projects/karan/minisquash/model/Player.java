package com.projects.karan.minisquash.model;

/**
 * Created by ADMIN on 9/7/2016.
 */
public class Player {
    private int id;
    private String name;
    private int matchesPlayed;
    private int matchesWon;

    public Player(int id, String name, int matchesPlayed, int matchesWon) {
        this.id = id;
        this.name = name;
        this.matchesPlayed = matchesPlayed;
        this.matchesWon = matchesWon;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getMatchesPlayed() {
        return matchesPlayed;
    }

    public int getMatchesWon() {
        return matchesWon;
    }
}
