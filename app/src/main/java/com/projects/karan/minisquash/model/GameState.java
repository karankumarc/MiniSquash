package com.projects.karan.minisquash.model;

/**
 * Created by ADMIN on 9/5/2016.
 */
public class GameState {

    private boolean didPlayer1WinThePoint;
    private boolean didGameEnterInTieBreakerMode;

    public GameState(int didPlayer1WinThePoint) {
        if(didPlayer1WinThePoint == 1)
            this.didPlayer1WinThePoint = true;
        else
            this.didPlayer1WinThePoint = false;
    }

    public GameState(boolean didPlayer1WinThePoint, boolean didGameEnterInTieBreakerMode) {
        this.didPlayer1WinThePoint = didPlayer1WinThePoint;
        this.didGameEnterInTieBreakerMode = didGameEnterInTieBreakerMode;
    }

    public boolean didPlayer1WinThePoint() {
        return didPlayer1WinThePoint;
    }

    public boolean didGameEnterInTieBreakerMode() {
        return didGameEnterInTieBreakerMode;
    }
}
