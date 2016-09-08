package com.projects.karan.minisquash.data;

import android.provider.BaseColumns;

/**
 * Created by ADMIN on 9/6/2016.
 */
public final class MiniSquashContract {

    public static final class GameStateDetailsEntry implements BaseColumns {
        public static final String TABLE_NAME = "gameStateDetails";
        public static final String COLUMN_DID_PLAYER_1_WIN = "didPlayer1Win";
    }

    public static final class MatchDetailsEntry implements BaseColumns {
        public static final String TABLE_NAME = "matchDetails";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_POINTS_PER_SET = "pointsPerSet";
        public static final String COLUMN_SERVICE_STARTED_BY_WINNER = "serviceStartedByWinner";
        public static final String COLUMN_TOTAL_SETS = "totalSets";
        public static final String COLUMN_WINNER_NAME = "winnerName";
        public static final String COLUMN_WINNER_TOTAL_POINTS_WON = "winnerTotalPointsWon";
        public static final String COLUMN_WINNER_SETS_WON = "winnerSetsWon";
        public static final String COLUMN_WINNER_TOTAL_SERVES = "winnerTotalServes";
        public static final String COLUMN_WINNER_POINTS_WON_IN_SERVICE = "winnerPointsWonInService";
        public static final String COLUMN_LOSER_NAME = "loserName";
        public static final String COLUMN_LOSER_TOTAL_POINTS_WON = "loserTotalPointsWon";
        public static final String COLUMN_LOSER_SETS_WON = "loserSetsWon";
        public static final String COLUMN_LOSER_TOTAL_SERVES = "loserTotalServes";
        public static final String COLUMN_LOSER_POINTS_WON_IN_SERVICE = "loserPointsWonInService";
        public static final String COLUMN_WINNER_ID = "winnerId";
        public static final String COLUMN_LOSER_ID = "loserId";
    }

    public static final class PlayerDetailsEntry implements BaseColumns {
        public static final String TABLE_NAME = "playerDetails";
        public static final String COLUMN_PLAYER_NAME = "playerName";
        public static final String COLUMN_MATCHES_PLAYED = "matchesPlayed";
        public static final String COLUMN_MATCHES_WON = "matchesWon";
    }
}
