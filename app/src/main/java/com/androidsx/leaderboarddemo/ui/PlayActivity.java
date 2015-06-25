package com.androidsx.leaderboarddemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.NumberPicker;

import com.androidsx.leaderboarddemo.R;
import com.androidsx.leaderboarddemo.data.ScoreManager;


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
        ScoreManager.addScore(scorePicker.getValue());
        startActivity(new Intent(this, GameOverActivity.class));
    }
}
