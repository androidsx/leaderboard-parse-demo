package com.androidsx.leaderboarddemo.ui.admin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.androidsx.leaderboarddemo.R;
import com.androidsx.leaderboarddemo.data.ParseDao;
import com.androidsx.leaderboarddemo.data.ParseHelper;
import com.androidsx.leaderboarddemo.data.ScoreManager;
import com.parse.ParseUser;


public class NewGameActivity extends AppCompatActivity {
    private NumberPicker scorePicker;

    private String levelName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        levelName = getIntent().getStringExtra("level");

        ((TextView) findViewById(R.id.new_game_username)).setText("Player: " + ParseUser.getCurrentUser().getUsername());
        ((TextView) findViewById(R.id.new_game_level)).setText("Level: " + levelName);

        scorePicker = (NumberPicker) findViewById(R.id.score_picker);
        scorePicker.setMinValue(0);
        scorePicker.setMaxValue(10000);
        scorePicker.setValue(1000);
        scorePicker.setWrapSelectorWheel(false);
    }

    public void createScore(View view) {
        ScoreManager.addScore(scorePicker.getValue());
        ParseDao.createHighscore(ParseUser.getCurrentUser(), levelName, scorePicker.getValue(), new ParseHelper.ToastAndFinishSaveCallback(this));
    }
}
