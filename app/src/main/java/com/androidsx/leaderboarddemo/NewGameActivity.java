package com.androidsx.leaderboarddemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


public class NewGameActivity extends AppCompatActivity {
    private NumberPicker scorePicker;

    private String userId;
    private String levelName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        userId = getIntent().getStringExtra("userId");
        String username = getIntent().getStringExtra("username");
        levelName = getIntent().getStringExtra("level");

        ((TextView) findViewById(R.id.new_game_username)).setText("Player: " + username);
        ((TextView) findViewById(R.id.new_game_level)).setText("Level: " + levelName);

        scorePicker = (NumberPicker) findViewById(R.id.score_picker);
        scorePicker.setMinValue(0);
        scorePicker.setMaxValue(10000);
        scorePicker.setValue(1000);
        scorePicker.setWrapSelectorWheel(false);
    }

    public void createScore(View view) {
        final ParseObject user = ParseObject.createWithoutData(DB.Table.USER, userId);

        ParseQuery.getQuery(DB.Table.HIGHSCORE)
                .whereEqualTo(DB.Column.HIGHSCORE_LEVEL, levelName)
                .whereEqualTo(DB.Column.HIGHSCORE_USER, user)
                .findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> parseObjects, ParseException e) {
                        if (e == null) {
                            if (parseObjects.size() == 0) {
                                saveNewHighscore(scorePicker.getValue(), user);
                            } else if (parseObjects.size() == 1) {
                                updateHighscore(scorePicker.getValue(), parseObjects.get(0));
                            } else {
                                throw new RuntimeException("Oops, there's more than one highscore for this user in this level");
                            }
                        } else {
                            throw new RuntimeException("Failed to get highscores", e);
                        }
                    }
                });
    }

    private void saveNewHighscore(int newScore, ParseObject user) {
        ParseObject userScore = new ParseObject(DB.Table.HIGHSCORE);
        userScore.put("user", user);
        userScore.put("level", levelName);
        userScore.put("score", newScore);
        userScore.saveInBackground(new ParseHelper.ToastAndFinishSaveCallback(this));
    }

    private void updateHighscore(int newScore, ParseObject highscore) {
        final int highestSoFar = highscore.getInt(DB.Column.HIGHSCORE_SCORE);
        if (newScore > highestSoFar) {
            highscore.put(DB.Column.HIGHSCORE_SCORE, newScore);
            highscore.saveInBackground(new ParseHelper.ToastAndFinishSaveCallback(this));
        } else {
            Toast.makeText(this,
                    "New score (" + newScore + "), but not the highest (" + highestSoFar + "). Ignored",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
