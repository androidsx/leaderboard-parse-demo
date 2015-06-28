package com.androidsx.leaderboarddemo.data;

import android.util.Log;

import com.androidsx.leaderboarddemo.model.Level;
import com.androidsx.leaderboarddemo.model.Score;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the local scores. The only score that really matters is the highest score so far
 * (aka highscore), but let's keep them all just in case.
 */
public class ScoreManager {

    private static ScoreManager INSTANCE;

    /** Results for the current user. Note that they are local: changing user will reset these. */
    private final Map<Level, List<Score>> scores = new HashMap<>();

    public static ScoreManager getScoreManager() {
        // read from shared prefs
        if (INSTANCE == null) {
            INSTANCE = new ScoreManager();
        }

        return INSTANCE;
    }

    /** Use {@link #getScoreManager()}} */
    private ScoreManager() {
    }

    /** Did this user play any games on any level. */
    public boolean anyScores() {
        return scores.size() > 0;
    }

    /**
     * Adds this new score for this level.
     *
     * @return is this the highest score so far for this level?
     */
    public boolean addScore(Level level, int score) {
        final boolean isHighestScore = score > getHighestScore(level);

        List<Score> scoresForLevel = scores.get(level);
        if (scoresForLevel == null) {
            scoresForLevel = new ArrayList<>();
        }
        scoresForLevel.add(new Score(new Date(), score));

        scores.put(level, scoresForLevel);
        return isHighestScore;
    }

    /** This is pretty dirty, really. We just got this score, so we should pass it around. */
    public int getLatestScore(Level level) {
        Score latestScore = null;
        Date latestFound = new Date(0);

        List<Score> scoresForLevel = scores.get(level);
        if (scoresForLevel != null) {
            for (Score score : scoresForLevel) {
                if (score.getGameDate().after(latestFound)) {
                    latestScore = score;
                }
            }
        }

        // 0 should never happen at the moment. See #anyScores
        return latestScore == null ? 0 : latestScore.getScore();
    }

    public int getHighestScore(Level level) {
        int highestScore = 0;
        List<Score> scoresForLevel = scores.get(level);
        if (scoresForLevel != null) {
            for (Score score : scoresForLevel) {
                if (score.getScore() > highestScore) {
                    highestScore = score.getScore();
                }
            }
        }

        // again, 0 by default
        return highestScore;
    }

}
