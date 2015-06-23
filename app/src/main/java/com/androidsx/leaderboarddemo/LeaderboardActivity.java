package com.androidsx.leaderboarddemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
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
    private String roomName;
    private String level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        userId = getIntent().getStringExtra("userId");
        username = getIntent().getStringExtra("username");
        roomName = getIntent().getStringExtra("roomName");
        level = getIntent().getStringExtra("level");

        final ListView elementListView = (ListView) findViewById(R.id.leaderboardListView);
        showLeaderboard(elementListView);
    }

    private void showLeaderboard(final ListView elementListView) {
        ParseQuery.getQuery(DB.Table.HIGHSCORE)
                //.whereMatchesQuery(DB.Column.HIGHSCORE_USER, userInRoomInnerQuery)
                .whereEqualTo(DB.Column.HIGHSCORE_LEVEL, level)
                .include(DB.Column.HIGHSCORE_USER)
                .findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> scoreParseObjects, ParseException e) {
                        if (e == null) {
                            final List<String> leaderboardRows = new ArrayList<>();
                            for (ParseObject scoreParseObject : scoreParseObjects) {
                                final ParseObject thisUser = scoreParseObject.getParseObject(DB.Column.HIGHSCORE_USER);
                                final String thisUsername = thisUser.getString(DB.Column.USER_NAME);
                                final Number thisScore = scoreParseObject.getNumber(DB.Column.HIGHSCORE_SCORE);

                                final String row;
                                if (thisUser.getObjectId().equals(userId)) {
                                    row = "# *" + thisUsername + "* (" + thisScore + ")";
                                } else {
                                    row = "# " + thisUsername + " (" + thisScore + ")";
                                }
                                leaderboardRows.add(row);
                                Log.i(TAG, row);
                            }

                            configureListView(elementListView, leaderboardRows);


                        } else {
                            throw new RuntimeException("Failed to get the leaderboard", e);
                        }
                    }
                });


        /*
        ParseQuery.getQuery(DB.Table.ROOM).findInBackground(new FindCallback<ParseObject>() {
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
        */
    }

    private void configureListView(ListView leaderboardListView, final List<String> leaderboardRows) {
        leaderboardListView.setAdapter(new ArrayAdapter<>(this, R.layout.row_element, R.id.element_name, leaderboardRows));
    }
}
