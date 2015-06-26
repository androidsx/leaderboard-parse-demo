package com.androidsx.leaderboarddemo.ui.mock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidsx.leaderboarddemo.R;
import com.androidsx.leaderboarddemo.data.GlobalState;
import com.androidsx.leaderboarddemo.data.ScoreManager;
import com.androidsx.leaderboarddemo.ui.BackgroundJobAwareBaseActivity;
import com.parse.ParseUser;


public class GameOverActivity extends BackgroundJobAwareBaseActivity {

    public static void startGameOverActivity(Context context, boolean justGotHighestScore) {
        Intent intent = new Intent(context, GameOverActivity.class);
        intent.putExtra("justGotHighestScore", justGotHighestScore);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

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
        findViewById(R.id.see_leaderboard_button).setVisibility(ParseUser.getCurrentUser() == null ? View.GONE : View.VISIBLE);

        final TextView latestScoreTextView = (TextView) findViewById(R.id.latest_score);
        latestScoreTextView.setText(String.valueOf(ScoreManager.getLatestScore()));

        final TextView highestScoreTextView = (TextView) findViewById(R.id.highest_score);
        highestScoreTextView.setText(String.valueOf(ScoreManager.getHighestScore()));
    }

    public void shareResult(View view) {
        startActivity(new Intent(this, NewRoomActivity.class));
    }

    public void playAgain(View view) {
        PlayActivity.startPlayActivity(this, GlobalState.level);
    }

    public void rate(View view) {
        Toast.makeText(this, "Rate in the App Store", Toast.LENGTH_SHORT).show();
    }

    public void seeLeaderboard(View view) {
        LeaderboardActivity.startLeaderboardActivity(this, GlobalState.level);
    }
}
