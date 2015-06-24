package com.androidsx.leaderboarddemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
    private String roomId;
    private String level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        userId = getIntent().getStringExtra("userId");
        roomId = getIntent().getStringExtra("roomId");
        level = getIntent().getStringExtra("level");

        final ListView elementListView = (ListView) findViewById(R.id.leaderboardListView);
        showLeaderboard(elementListView);
    }

    private void showLeaderboard(final ListView elementListView) {
        final ParseObject room = ParseObject.createWithoutData(DB.Table.ROOM, roomId);

        final ParseQuery<ParseObject> usersInRoom = ParseQuery.getQuery(DB.Table.USER)
                .include(DB.Column.USER_ROOMS)
                .whereEqualTo(DB.Column.USER_ROOMS, room);

        displayLeaderboard(elementListView, usersInRoom);
    }

    /** Just to test the inner query. */
    private void displayUsersInRoom(final ListView leaderboardListView, ParseQuery<ParseObject> usersInRoom) {
        usersInRoom.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> userParseObjects, ParseException e) {
                if (e == null) {
                    final List<String> userNames = ParseHelper.toListKeepOrder(userParseObjects, DB.Column.USER_NAME);
                    configureListView(leaderboardListView, userNames);
                } else {
                    throw new RuntimeException("Could not find the users in this room", e);
                }
            }
        });
    }

    private void displayLeaderboard(final ListView leaderboardListView, ParseQuery<ParseObject> usersInRoomInnerQuery) {
        ParseQuery.getQuery(DB.Table.HIGHSCORE)
                .whereMatchesQuery(DB.Column.HIGHSCORE_USER, usersInRoomInnerQuery)
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

                            configureListView(leaderboardListView, leaderboardRows);
                        } else {
                            throw new RuntimeException("Failed to get the leaderboard", e);
                        }
                    }
                });

    }

    private void configureListView(ListView leaderboardListView, final List<String> leaderboardRows) {
        leaderboardListView.setAdapter(new ArrayAdapter<>(this, R.layout.row_element, R.id.element_name, leaderboardRows));
    }
}
