package com.androidsx.leaderboarddemo.ui.mock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidsx.leaderboarddemo.R;
import com.androidsx.leaderboarddemo.data.DB;
import com.androidsx.leaderboarddemo.data.ParseDao;
import com.androidsx.leaderboarddemo.data.ParseHelper;
import com.androidsx.leaderboarddemo.model.Room;
import com.androidsx.leaderboarddemo.ui.BackgroundJobAwareBaseActivity;
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
public class LeaderboardActivity extends BackgroundJobAwareBaseActivity {
    private static final String TAG = LeaderboardActivity.class.getSimpleName();

    private String level;

    public static void startLeaderboardActivity(Context context, String level) {
        if (TextUtils.isEmpty(level)) {
            throw new RuntimeException("Missing level parameter");
        }
        Intent intent = new Intent(context, LeaderboardActivity.class);
        intent.putExtra("level", level);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        level = getIntent().getStringExtra("level");

        final Spinner roomSpinner = (Spinner) findViewById(R.id.room_spinner);
        final ListView elementListView = (ListView) findViewById(R.id.leaderboardListView);


        ParseDao.getRoomsForUser(new ParseDao.RoomFindCallback() {
            @Override
            public void done(List<Room> rooms) {
                roomSpinner.setAdapter(new ArrayAdapter<>(LeaderboardActivity.this, R.layout.spinner_row_element, rooms));
                roomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        final Room selectedRoom = (Room) roomSpinner.getAdapter().getItem(position);
                        showLeaderboard(elementListView, selectedRoom.getObjectId());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // No action here
                    }
                });

                if (roomSpinner.getAdapter().getCount() > 0) {
                    final Room firstRoom = (Room) roomSpinner.getAdapter().getItem(0);
                    showLeaderboard(elementListView, firstRoom.getObjectId());
                } else {
                    Toast.makeText(LeaderboardActivity.this, "No rooms", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void showLeaderboard(final ListView elementListView, String roomId) {
        final ParseObject room = ParseObject.createWithoutData(DB.Table.ROOM, roomId);

        final ParseQuery<ParseObject> usersInRoom = ParseQuery.getQuery(DB.Table.USER)
                .include(DB.Column.USER_ROOMS)
                .whereEqualTo(DB.Column.USER_ROOMS, room);

        displayLeaderboard(elementListView, usersInRoom);
    }

    private void displayLeaderboard(final ListView leaderboardListView, ParseQuery<ParseObject> usersInRoomInnerQuery) {
        startBackgroundJob();
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
                            finishBackgroundJob();
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
