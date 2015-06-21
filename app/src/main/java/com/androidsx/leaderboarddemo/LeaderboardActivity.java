package com.androidsx.leaderboarddemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


/**
 * Lets the user pick a room where they are registered, and then see the leaderboard for it.
 *
 * - Incoming: userId, username and level.
 * - Outfoing: nothing.
 */
public class LeaderboardActivity extends AppCompatActivity {
    private static final String TAG = LeaderboardActivity.class.getSimpleName();
    private static final String DEFAULT_ROOM_NAME = "generatedroom0.19474775600247085";

    public static final int PICK_ROOM_REQUEST = 1;

    private String roomName = DEFAULT_ROOM_NAME;

    // Coming from the calling activity through the extras
    private String userId;
    private String username;
    private String level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        userId = getIntent().getStringExtra("userId");
        username = getIntent().getStringExtra("username");
        level = getIntent().getStringExtra("level");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_ROOM_REQUEST) {
            if (resultCode == RESULT_OK) {
                roomName = data.getStringExtra("result");
                updateUi();
                showLeaderboardInConsole();
            }
        } else {
            throw new IllegalArgumentException("Unexpected request code: " + requestCode);
        }
    }

    private void updateUi() {
        ((TextView) findViewById(R.id.picked_room)).setText(roomName);
    }

    public void pickRoom(View view) {
        Intent intent = new Intent(this, PickRoomActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("level", level);
        startActivityForResult(intent, PICK_ROOM_REQUEST);
    }

    private void showLeaderboardInConsole() {
        ParseQuery.getQuery("Room").findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> roomParseObjects, ParseException e) {
                if (e == null) {
                    for (ParseObject roomParseObject : roomParseObjects) {
                        if (roomParseObject.get("name").equals(roomName)) {
                            Log.i(TAG, "Found the room " + roomName);

                            roomParseObject.getRelation("scores").getQuery()
                                    .include("user")
                                    .orderByDescending("score")
                                    .findInBackground(new FindCallback<ParseObject>() {
                                        @Override
                                        public void done(List<ParseObject> scoreParseObjects, ParseException e) {
                                            if (e == null) {
                                                for (ParseObject scoreParseObject : scoreParseObjects) {
                                                    Log.i(TAG, "# " + scoreParseObject.getParseObject("user").getString("username") + ": " + scoreParseObject.getNumber("score"));
                                                }
                                            } else {
                                                throw new RuntimeException("Failed to retrieve the scores for a room", e);
                                            }
                                        }
                                    });
                        }
                    }
                } else {
                    throw new RuntimeException("Failed to retrieve the room", e);
                }
            }
        });
    }
}
