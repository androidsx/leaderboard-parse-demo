package com.androidsx.leaderboarddemo.ui.mock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidsx.leaderboarddemo.R;
import com.androidsx.leaderboarddemo.data.local.LevelManager;
import com.androidsx.leaderboarddemo.data.local.ScoreManager;
import com.parse.ParseUser;


/**
 * This activity has no toolbar.
 */
public class GameOverActivity extends AppCompatActivity {

    public static void startGameOverActivity(Context context, boolean justGotHighestScore) {
        Intent intent = new Intent(context, GameOverActivity.class);
        intent.putExtra("justGotHighestScore", justGotHighestScore);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        getSupportActionBar().hide();

        if (getIntent().getBooleanExtra("justGotHighestScore", false)) {
            Toast.makeText(this, "Congrats on your high score :o)", Toast.LENGTH_SHORT).show();
        }
    }

    /** Reconfigure the UI on resume: a leaderboard may have just been created. */
    @Override
    protected void onResume() {
        super.onResume();

        configureUi();
    }

    private void configureUi() {
        // The leaderboard already contain a button to create a new room, so when the user have one there
        // is no need for the Create room button in this screen
        findViewById(R.id.see_leaderboard_button).setVisibility(ParseUser.getCurrentUser() == null ? View.GONE : View.VISIBLE);
        findViewById(R.id.create_room_button).setVisibility(ParseUser.getCurrentUser() == null ? View.VISIBLE : View.GONE);

        final ScoreManager scoreManager = ScoreManager.getScoreManager(GameOverActivity.this);
        final TextView latestScoreTextView = (TextView) findViewById(R.id.latest_score);
        latestScoreTextView.setText(String.valueOf(scoreManager.getLatestScore(LevelManager.levelName)));
        final TextView highestScoreTextView = (TextView) findViewById(R.id.highest_score);
        highestScoreTextView.setText(String.valueOf(scoreManager.getHighestScore(LevelManager.levelName)));
    }

    public void createRoom(View view) {
        startActivity(new Intent(this, NewRoomActivity.class));
    }

    public void playAgain(View view) {
        PlayActivity.startPlayActivity(this, LevelManager.levelName);
    }

    public void rate(View view) {
        Toast.makeText(this, "Rate in the App Store", Toast.LENGTH_SHORT).show();
    }

    public void seeLeaderboard(View view) {
        LeaderboardActivity.startLeaderboardActivity(this, false, LevelManager.levelName);
    }
}
