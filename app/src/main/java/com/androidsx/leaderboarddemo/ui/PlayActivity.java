package com.androidsx.leaderboarddemo.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.NumberPicker;

import com.androidsx.leaderboarddemo.R;
import com.androidsx.leaderboarddemo.data.GlobalState;
import com.androidsx.leaderboarddemo.data.ParseDao;
import com.androidsx.leaderboarddemo.data.ScoreManager;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class PlayActivity extends AppCompatActivity {
    private NumberPicker scorePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        scorePicker = (NumberPicker) findViewById(R.id.score_picker);
        scorePicker.setMinValue(0);
        scorePicker.setMaxValue(10000);
        scorePicker.setValue(1000);
        scorePicker.setWrapSelectorWheel(false);
    }

    public void endGame(View view) {
        final boolean isHighest = ScoreManager.addScore(scorePicker.getValue());
        if (isHighest) {
            ParseDao.createHighscore(ParseUser.getCurrentUser(), GlobalState.level, scorePicker.getValue(), new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        GameOverActivity.startGameOverActivity(PlayActivity.this, true);
                    }
                }
            });
        } else {
            GameOverActivity.startGameOverActivity(PlayActivity.this, false);
        }
    }
}
