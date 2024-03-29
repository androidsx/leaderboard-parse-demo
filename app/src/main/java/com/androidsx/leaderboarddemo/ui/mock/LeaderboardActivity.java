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
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.androidsx.leaderboarddemo.R;
import com.androidsx.leaderboarddemo.data.local.ActiveRoomManager;
import com.androidsx.leaderboarddemo.deeplink.BranchHelper;
import com.androidsx.leaderboarddemo.data.remote.DB;
import com.androidsx.leaderboarddemo.data.remote.ParseDao;
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
 * - Incoming: userId, username, levelName and roomName.
 * - Outfoing: nothing.
 */
public class LeaderboardActivity extends BackgroundJobAwareBaseActivity {
    private static final String TAG = LeaderboardActivity.class.getSimpleName();

    private boolean all;
    private String level;

    /**
     * @param all see ALL the leaderboards in Parse. Ignoring "level"
     */
    public static void startLeaderboardActivity(Context context, boolean all, String level) {
        if (!all && TextUtils.isEmpty(level)) {
            throw new RuntimeException("Missing levelName parameter");
        }
        Intent intent = new Intent(context, LeaderboardActivity.class);
        intent.putExtra("all", all);
        intent.putExtra("levelName", level);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        all = getIntent().getBooleanExtra("all", false);
        level = getIntent().getStringExtra("levelName");

        final Spinner roomSpinner = (Spinner) findViewById(R.id.room_spinner);
        final ListView elementListView = (ListView) findViewById(R.id.leaderboardListView);

        // Populate the spinner
        final RoomsForSpinnerCallback callback = new RoomsForSpinnerCallback(roomSpinner, elementListView);
        if (all) {
            ParseDao.getAllRooms(callback);
        } else {
            ParseDao.getRoomsForUser(callback);
        }
    }

    private int contains(SpinnerAdapter adapter, Room room) {
        if (room != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                if ((adapter.getItem(i)).equals(room)) {
                    return i;
                }
            }
            return 0;
        } else {
            return 0;
        }
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


        final ParseQuery<ParseObject> query = ParseQuery.getQuery(DB.Table.HIGHSCORE)
                .whereMatchesQuery(DB.Column.HIGHSCORE_USER, usersInRoomInnerQuery);

        if (!all) {
            query.whereEqualTo(DB.Column.HIGHSCORE_LEVEL, level);
        }

        query.include(DB.Column.HIGHSCORE_USER)
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

    public void inviteMoreFriends(View view) {
        if (ParseUser.getCurrentUser() ==  null) {
            Toast.makeText(this, "Must log in first, o que te pensabas?", Toast.LENGTH_LONG).show();
        } else if (ActiveRoomManager.getActiveRoom(this) == null) {
            Toast.makeText(this, "Must select a room first, o que te pensabas?", Toast.LENGTH_LONG).show();
        } else {
            final String username = ParseUser.getCurrentUser().getUsername();
            final String roomName = ActiveRoomManager.getActiveRoom(this).getName();
            String roomId = ActiveRoomManager.getActiveRoom(this).getObjectId();

            BranchHelper.generateBranchLink(this, username, roomName, roomId);
        }
    }

    public void createNewRoom(View view) {
        startActivity(new Intent(this, NewRoomActivity.class));
    }

    private class RoomsForSpinnerCallback implements ParseDao.RoomFindCallback {
        private final Spinner roomSpinner;
        private final ListView elementListView;

        public RoomsForSpinnerCallback(Spinner roomSpinner, ListView elementListView) {
            this.roomSpinner = roomSpinner;
            this.elementListView = elementListView;
        }

        @Override
        public void done(List<Room> rooms) {
            roomSpinner.setAdapter(new ArrayAdapter<>(LeaderboardActivity.this, R.layout.spinner_row_element, rooms));
            roomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    final Room selectedRoom = (Room) roomSpinner.getAdapter().getItem(position);
                    ActiveRoomManager.saveActiveRoom(LeaderboardActivity.this, selectedRoom);
                    showLeaderboard(elementListView, selectedRoom.getObjectId());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // No action here
                }
            });

            if (roomSpinner.getAdapter().getCount() > 0) {
                roomSpinner.setSelection(contains(roomSpinner.getAdapter(), ActiveRoomManager.getActiveRoom(LeaderboardActivity.this)));
            } else {
                Toast.makeText(LeaderboardActivity.this, "No rooms", Toast.LENGTH_LONG).show();
            }
        }
    }
}
