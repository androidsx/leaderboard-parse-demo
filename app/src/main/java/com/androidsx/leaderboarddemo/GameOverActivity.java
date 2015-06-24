package com.androidsx.leaderboarddemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class GameOverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        configureUi();
    }

    private void configureUi() {
        final View view = findViewById(R.id.see_leaderboard_button);
        view.setVisibility(GlobalState.me == null ? View.GONE : View.VISIBLE); // Maybe you are a user but belong to no rooms?

        final TextView latestScoreTextView = (TextView) findViewById(R.id.latest_score);
        latestScoreTextView.setText(String.valueOf(ScoreManager.getLatestScore()));

        final TextView highestScoreTextView = (TextView) findViewById(R.id.highest_score);
        highestScoreTextView.setText(String.valueOf(ScoreManager.getHighestScore()));
    }

    public void shareResult(View view) {
        Toast.makeText(this, "Not yet: create room and join it", Toast.LENGTH_LONG).show();
    }

    public void playAgain(View view) {
        startActivity(new Intent(this, PlayActivity.class));
    }

    public void rate(View view) {
        Toast.makeText(this, "Rate in the App Store", Toast.LENGTH_SHORT).show();
    }

    public void seeLeaderboard(View view) {
        Toast.makeText(this, "Not yet: open leaderboard(s)", Toast.LENGTH_LONG).show();
    }
}
