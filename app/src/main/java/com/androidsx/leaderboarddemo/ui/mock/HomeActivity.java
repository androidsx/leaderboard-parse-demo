package com.androidsx.leaderboarddemo.ui.mock;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.androidsx.leaderboarddemo.R;
import com.androidsx.leaderboarddemo.data.local.LevelManager;
import com.androidsx.leaderboarddemo.deeplink.BranchHelper;
import com.androidsx.leaderboarddemo.ui.BackgroundJobAwareBaseActivity;
import com.androidsx.leaderboarddemo.ui.admin.AdminActivity;
import com.parse.ParseUser;

import io.branch.referral.Branch;


public class HomeActivity extends BackgroundJobAwareBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Branch.getInstance(getApplicationContext()).closeSession();
    }

    @Override
    protected void onStart() {
        super.onStart();
        BranchHelper.showJoinRoomDialogIfDeeplink(this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent); // Branch.io
    }

    public void play(View view) {
        PlayActivity.startPlayActivity(this, LevelManager.levelName);
    }

    public void seeLeaderboard(View view) {
        if (ParseUser.getCurrentUser() == null) {
            Toast.makeText(this, "You don't belong to any leaderboards. Want to create one? (Or have friends invite you.)", Toast.LENGTH_LONG).show();
        } else {
            LeaderboardActivity.startLeaderboardActivity(this, false, LevelManager.levelName);
        }
    }

    public void seeAllLeaderboards(View view) {
        LeaderboardActivity.startLeaderboardActivity(this, true, LevelManager.levelName);
    }

    public void openOldCode(View view) {
        startActivity(new Intent(this, AdminActivity.class));
    }
}
