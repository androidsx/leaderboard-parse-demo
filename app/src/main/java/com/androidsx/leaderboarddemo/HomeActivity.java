package com.androidsx.leaderboarddemo;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void play(View view) {
        startActivity(new Intent(this, PlayActivity.class));
    }

    public void seeLeaderboard(View view) {
        Toast.makeText(this, "Not yet: open leaderboard(s)", Toast.LENGTH_LONG).show();
    }

    public void openOldCode(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }
}
