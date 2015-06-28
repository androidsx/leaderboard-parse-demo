package com.androidsx.leaderboarddemo.model;

import java.util.Date;


/**
 * A score after playing a game (in a level). Not necessarily the highscore. This object does not
 * really exist in Parse: there, we only save the highest score.
 */
public class Score {
    private int score;
    private Date gameDate;

    /** Arg-less constructor for GSON. */
    public Score() {
    }

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
    public String toString() {
        return String.valueOf(score);
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
