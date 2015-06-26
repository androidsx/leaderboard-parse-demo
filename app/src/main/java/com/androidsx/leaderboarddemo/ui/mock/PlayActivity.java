package com.androidsx.leaderboarddemo.ui.mock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;

import com.androidsx.leaderboarddemo.R;
import com.androidsx.leaderboarddemo.data.GlobalState;
import com.androidsx.leaderboarddemo.data.ParseDao;
import com.androidsx.leaderboarddemo.data.ScoreManager;
import com.androidsx.leaderboarddemo.ui.BackgroundJobAwareBaseActivity;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class PlayActivity extends BackgroundJobAwareBaseActivity {
    private NumberPicker scorePicker;

    private String levelName;

    public static void startPlayActivity(Context context, String level) {
        Intent intent = new Intent(context, PlayActivity.class);
        intent.putExtra("level", level);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        levelName = getIntent().getStringExtra("level");

        scorePicker = (NumberPicker) findViewById(R.id.score_picker);
        scorePicker.setMinValue(0);
        scorePicker.setMaxValue(10000);
        scorePicker.setValue(1000);
        scorePicker.setWrapSelectorWheel(false);
    }

    public void endGame(View view) {
        final boolean isHighest = ScoreManager.addScore(scorePicker.getValue());
        if (GlobalState.isActiveUser() && isHighest) {
            startBackgroundJob();
            ParseDao.createHighscore(ParseUser.getCurrentUser(), levelName, scorePicker.getValue(), new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        GameOverActivity.startGameOverActivity(PlayActivity.this, isHighest);
                        PlayActivity.super.finishBackgroundJob();
                    } else {
                        throw new RuntimeException(e);
                    }
                }
            });
        } else {
            GameOverActivity.startGameOverActivity(PlayActivity.this, isHighest);
        }
    }
}
