package com.androidsx.leaderboarddemo.ui.mock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.androidsx.leaderboarddemo.R;
import com.androidsx.leaderboarddemo.data.GlobalState;
import com.androidsx.leaderboarddemo.ui.admin.MainActivity;


public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void play(View view) {
        PlayActivity.startPlayActivity(this, GlobalState.level);
    }

    public void seeLeaderboard(View view) {
        if (GlobalState.isActiveUser()) {
            LeaderboardActivity.startLeaderboardActivity(this, GlobalState.activeRoomId, GlobalState.activeRoomName, GlobalState.level);
        } else {
            Toast.makeText(this, "You don't belong to any leaderboards. Want to create one? (Or have friends invite you.)", Toast.LENGTH_LONG).show();
        }
    }

    public void openOldCode(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }
}