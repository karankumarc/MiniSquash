package com.projects.karan.minisquash.data;

import android.provider.BaseColumns;

/**
 * Created by ADMIN on 9/6/2016.
 */
public final class MiniSquashContract {

    public static final class GameStateDetailsEntry implements BaseColumns{
        public static final String TABLE_NAME = "gameStateDetails";
        public static final String COLUMN_DID_PLAYER_1_WIN = "didPlayer1Win";
    }
}
