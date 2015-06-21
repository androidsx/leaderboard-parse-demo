package com.androidsx.leaderboarddemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private static final String DEFAULT_USER_ID = "qCDuSRwHFB";
    private static final String DEFAULT_USERNAME = "pau";
    private static final String DEFAULT_LEVEL = "preRelease1";
    public static final String DEFAULT_ROOM = "";

    private static final int PICK_USER_REQUEST = 1;
    private static final int PICK_LEVEL_REQUEST = 2;
    public static final int PICK_ROOM_REQUEST = 3;

    // Current selection through the UI. Yes, static, like a boss XD
    private static String userId = DEFAULT_USER_ID;
    private static String username = DEFAULT_USERNAME;
    private static String level = DEFAULT_LEVEL;
    private static String roomName = DEFAULT_ROOM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateUi();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_USER_REQUEST) {
            if (resultCode == RESULT_OK) {
                userId = data.getStringExtra("userId");
                username = data.getStringExtra("username");
                roomName = DEFAULT_ROOM;
                updateUi();
            }
        } else if (requestCode == PICK_LEVEL_REQUEST) {
            if (resultCode == RESULT_OK) {
                level = data.getStringExtra("result");
                roomName = DEFAULT_ROOM;
                updateUi();
            }
        } else if (requestCode == PICK_ROOM_REQUEST) {
            if (resultCode == RESULT_OK) {
                roomName = data.getStringExtra("result");
                updateUi();
            }
        } else {
            throw new IllegalArgumentException("Unexpected request code: " + requestCode);
        }
    }

    private void updateUi() {
        ((TextView) findViewById(R.id.picked_user)).setText(username);
        ((TextView) findViewById(R.id.picked_level)).setText(level);
        ((TextView) findViewById(R.id.picked_room)).setText(roomName);
    }

    public void pickUser(View view) {
        startActivityForResult(new Intent(this, PickUserActivity.class), PICK_USER_REQUEST);
    }

    public void pickLevel(View view) {
        startActivityForResult(new Intent(this, PickLevelActivity.class), PICK_LEVEL_REQUEST);
    }

    public void pickRoom(View view) {
        Intent intent = new Intent(this, PickRoomActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("level", level);
        startActivityForResult(intent, PICK_ROOM_REQUEST);
    }

    public void playNewGame(View view) {
        Intent intent = new Intent(this, NewGameActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("username", username);
        intent.putExtra("level", level);
        startActivity(intent);
    }

    public void showLeaderboard(View view) {
        Intent intent = new Intent(this, LeaderboardActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("username", username);
        intent.putExtra("level", level);
        intent.putExtra("roomName", roomName);
        startActivity(intent);
    }
}
