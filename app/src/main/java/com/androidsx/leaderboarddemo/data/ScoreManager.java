package com.androidsx.leaderboarddemo.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Global, static state that will not persist across sessions. Don't try this at home.
 *
 * FIXME: make it a map by level
 */
public class ScoreManager {

    /** Results for the current user. Note that they are local: changing user will reset these. */
    private static final List<Score> scores = new ArrayList<>();

    /** Did this user play any games. */
    public static boolean anyScores() {
        return scores.size() > 0;
    }

    /**
     * @return is this the highest score so far?
     */
    public static boolean addScore(int score) {
        final boolean isHighestScore = score > getHighestScore();
        scores.add(new Score(new Date(), score));
        return isHighestScore;
    }

    /** This is pretty dirty, really. We just got this score, so we should pass it around. */
    public static int getLatestScore() {
        Score latestScore = null;
        Date latestFound = new Date(0);
        for (Score score : scores) {
            if (score.getGameDate().after(latestFound)) {
                latestScore = score;
            }
        }

        // 0 should never happen at the moment. See #anyScores
        return latestScore == null ? 0 : latestScore.getScore();
    }

    public static int getHighestScore() {
        int highestScore = 0;
        for (Score score : scores) {
            if (score.getScore() > highestScore) {
                highestScore = score.getScore();
            }
        }

        // again, 0 by default
        return highestScore;
    }

    private static class Score {
        private final int score;
        private final Date gameDate;

        public Score(Date gameDate, int score) {
            this.score = score;
            this.gameDate = gameDate;
        }

        public int getScore() {
            return score;
        }

        public Date getGameDate() {
            return gameDate;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Score that = (Score) o;

            return gameDate.equals(that.gameDate);

        }

        @Override
        public int hashCode() {
            return gameDate.hashCode();
        }
    }
}

