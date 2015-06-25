package com.androidsx.leaderboarddemo.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.androidsx.leaderboarddemo.R;
import com.androidsx.leaderboarddemo.data.DB;
import com.androidsx.leaderboarddemo.data.ParseHelper;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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

    private String roomId;
    private String roomName;
    private String level;

    public static void startLeaderboardActivity(Context context, String roomId, String roomName, String level) {
        if (TextUtils.isEmpty(roomId) || TextUtils.isEmpty(roomName) || TextUtils.isEmpty(level)) {
            throw new RuntimeException("Missing room or level parameter");
        }
        Intent intent = new Intent(context, LeaderboardActivity.class);
        intent.putExtra("roomId", roomId);
        intent.putExtra("roomName", roomName);
        intent.putExtra("level", level);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        roomId = getIntent().getStringExtra("roomId");
        roomName = getIntent().getStringExtra("roomName");
        level = getIntent().getStringExtra("level");

        ((TextView) findViewById(R.id.room_name)).setText(Html.fromHtml("Room <i>" + roomName + "</i>"));

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
                                if (thisUser.getObjectId().equals(ParseUser.getCurrentUser())) {
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
