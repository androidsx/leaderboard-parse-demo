package com.androidsx.leaderboarddemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    private static final int PICK_LEVEL_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_LEVEL_REQUEST) {
            if (resultCode == RESULT_OK) {
                String pickedLevel = data.getStringExtra("result");
                ((TextView) findViewById(R.id.picked_level)).setText(pickedLevel);
            } else if (resultCode == RESULT_CANCELED) {
                // No changes
            } else {
                throw new IllegalStateException("Result for pick level was " + resultCode);
            }
        } else {
            throw new IllegalArgumentException("Unexpected request code: " + requestCode);
        }
    }

    public void pickLevel(View view) {
        startActivityForResult(new Intent(this, PickLevelActivity.class), PICK_LEVEL_REQUEST);
    }
}
