package com.androidsx.leaderboarddemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


/**
 * Shows a leaderboard.
 *
 * - Incoming: userId, username, level and roomName.
 * - Outfoing: nothing.
 */
public class LeaderboardActivity extends AppCompatActivity {
    private static final String TAG = LeaderboardActivity.class.getSimpleName();

    // Coming from the calling activity through the extras
    private String userId;
    private String username;
    private String level;
    private String roomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        userId = getIntent().getStringExtra("userId");
        username = getIntent().getStringExtra("username");
        level = getIntent().getStringExtra("level");
        roomName = getIntent().getStringExtra("roomName");

        showLeaderboard();
    }

    private void showLeaderboard() {
        ParseQuery.getQuery(DbTables.ROOM).findInBackground(new FindCallback<ParseObject>() {
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
                                                final List<Pair<String, Number>> leaderboardData = new ArrayList<>();
                                                for (ParseObject scoreParseObject : scoreParseObjects) {
                                                    final String thisUsername = scoreParseObject.getParseObject("user").getString("username");
                                                    final Number thisScore = scoreParseObject.getNumber("score");

                                                    leaderboardData.add(new Pair<String, Number>(thisUsername, thisScore) {
                                                        @Override
                                                        public String toString() {
                                                            if (thisUsername.equals(username)) {
                                                                return "# *" + thisUsername + "* [" + thisScore + "]";
                                                            } else {
                                                                return "# " + thisUsername + " [" + thisScore + "]";
                                                            }
                                                        }
                                                    });

                                                    final ListView leaderboardListView = (ListView) findViewById(R.id.leaderboardListView);
                                                    leaderboardListView.setAdapter(new ArrayAdapter<>(LeaderboardActivity.this, R.layout.row_element, R.id.element_name, leaderboardData));

                                                    Log.i(TAG, "# " + thisUsername + " [" + thisScore + "]");
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
