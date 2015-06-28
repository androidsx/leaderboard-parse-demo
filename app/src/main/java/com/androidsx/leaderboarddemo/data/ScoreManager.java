package com.androidsx.leaderboarddemo.data;

import com.androidsx.leaderboarddemo.model.Level;
import com.androidsx.leaderboarddemo.model.Score;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Global, static state that will not persist across sessions. Don't try this at home.
 *
 * FIXME: make it a map by level
 */
public class ScoreManager {

    /** Results for the current user. Note that they are local: changing user will reset these. */
    private static final Map<Level, List<Score>> scores = new HashMap<>();

    /** Did this user play any games on any level. */
    public static boolean anyScores() {
        return scores.size() > 0;
    }

    /**
     * Adds this new score for this level.
     *
     * @return is this the highest score so far for this level?
     */
    public static boolean addScore(Level level, int score) {
        final boolean isHighestScore = score > getHighestScore(level);

        List<Score> scoresForLevel = ScoreManager.scores.get(level);
        if (scoresForLevel == null) {
            scoresForLevel = new ArrayList<>();
        }
        scoresForLevel.add(new Score(new Date(), score));

        ScoreManager.scores.put(level, scoresForLevel);
        return isHighestScore;
    }

    /** This is pretty dirty, really. We just got this score, so we should pass it around. */
    public static int getLatestScore(Level level) {
        Score latestScore = null;
        Date latestFound = new Date(0);

        List<Score> scoresForLevel = ScoreManager.scores.get(level);
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

    public static int getHighestScore(Level level) {
        int highestScore = 0;
        List<Score> scoresForLevel = ScoreManager.scores.get(level);
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
