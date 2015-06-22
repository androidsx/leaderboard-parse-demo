package com.androidsx.leaderboarddemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;


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
        ParseQuery<ParseObject> getUserQuery = ParseQuery.getQuery("_User");
        getUserQuery.getInBackground(userId, new GetCallback<ParseObject>() {
            public void done(ParseObject user, ParseException e) {
                if (e == null) {
                    ParseObject userScore = new ParseObject("UserScore");
                    userScore.put("user", user);
                    userScore.put("level", levelName);
                    userScore.put("score", scorePicker.getValue());
                    userScore.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(NewGameActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                throw new RuntimeException("Failed to save user", e);
                            }
                        }
                    });
                } else {
                    throw new RuntimeException("Failed to retrieve user", e);
                }
            }
        });
    }
}
