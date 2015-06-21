package com.androidsx.leaderboarddemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.NumberPicker;
import android.widget.TextView;


public class NewGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        String userId = getIntent().getStringExtra("userId");
        String username = getIntent().getStringExtra("username");
        String level = getIntent().getStringExtra("level");

        ((TextView) findViewById(R.id.new_game_username)).setText("Player: " + username);
        ((TextView) findViewById(R.id.new_game_level)).setText("Level: " + level);

        NumberPicker hundredsNumberPicker = (NumberPicker) findViewById(R.id.score_picker);
        hundredsNumberPicker.setMinValue(0);
        hundredsNumberPicker.setMaxValue(500);
        hundredsNumberPicker.setWrapSelectorWheel(false);
    }
}
