package com.androidsx.leaderboarddemo.data.local;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.androidsx.leaderboarddemo.model.Score;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the local scores. The only score that really matters is the highest score so far
 * (aka highscore), but let's keep them all just in case.
 *
 * They are persisted in JSON in the shared preferences.
 */
public class ScoreManager {
    private static final String TAG = ScoreManager.class.getSimpleName();

    private static ScoreManager INSTANCE;
    private SharedPreferences prefs;

    /**
     * Results for the current user. Note that they are local: changing user will reset these. It
     * contains the list of (timestamped) scores for every level (we probably have only one).
     */
    private final Map<String, List<Score>> scores;

    public static ScoreManager getScoreManager(Context context) {
        // read from shared prefs
        if (INSTANCE == null) {
            final SharedPreferences prefs = context.getSharedPreferences("prefs", Activity.MODE_PRIVATE);
            INSTANCE = new ScoreManager(prefs, readFromStorage(prefs));
        }

        return INSTANCE;
    }

    /** Use {@link #getScoreManager(Context)}} */
    private ScoreManager(SharedPreferences prefs, Map<String, List<Score>> scores) {
        this.prefs = prefs;
        Log.i("Pablo", "init scores with\n" + scores);
        this.scores = scores;
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
    public boolean addScore(String level, int score) {
        final boolean isHighestScore = score > getHighestScore(level);

        List<Score> scoresForLevel = scores.get(level);
        if (scoresForLevel == null) {
            scoresForLevel = new ArrayList<>();
        }
        scoresForLevel.add(new Score(new Date(), score));

        scores.put(level, scoresForLevel);
        saveToStorage(prefs, scores);

        return isHighestScore;
    }

    /** This is pretty dirty, really. We just got this score, so we should pass it around. */
    public int getLatestScore(String level) {
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

    public int getHighestScore(String level) {
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

    private static Map<String, List<Score>> readFromStorage(SharedPreferences prefs) {
        if (prefs.contains("scores")) {
            final String scoresAsJson = prefs.getString("scores", "");
            Map<String, List<Score>> scores = new Gson().fromJson(scoresAsJson, new TypeToken<Map<String, List<Score>>>() {}.getType());
            Log.i(TAG, "Restored all scores from storage:\n" + scores);
            return scores;
        } else {
            Log.i(TAG, "No scores found in storage. Looks like a new user");
            return new HashMap<>();
        }
    }

    /**
     * Save ALL scores in the shared preferences, overwriting it.
     *
     * TODO: Should do in a separate thread. There's no need to wait for its completion.
     */
    public static void saveToStorage(SharedPreferences prefs, Map<String, List<Score>> scores) {
        final String scoresAsJson = new Gson().toJson(scores);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString("scores", scoresAsJson);
        editor.apply();
    }
}
