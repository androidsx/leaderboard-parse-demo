package com.androidsx.leaderboarddemo.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.androidsx.leaderboarddemo.R;
import com.androidsx.leaderboarddemo.data.DB;
import com.androidsx.leaderboarddemo.data.GlobalState;
import com.androidsx.leaderboarddemo.data.ParseDao;
import com.androidsx.leaderboarddemo.data.ParseHelper;
import com.androidsx.leaderboarddemo.data.ScoreManager;
import com.androidsx.leaderboarddemo.model.Level;
import com.androidsx.leaderboarddemo.ui.BackgroundJobAwareBaseActivity;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

/**
 * Join an existing room. This is what would happen if the user clicks a link shared by a friend.
 *
 * Same view as {@link PickRoomActivity}, but here we show all rooms, and also we do modify the
 * user object to add the current user to the roomm, and even send our local highscore.
 *
 * - Incoming: none.
 * - Outfoing: room id and name for the room that was picked.
 */
public class JoinRoomActivity extends BackgroundJobAwareBaseActivity {
    private static final String TAG = JoinRoomActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_element);

        final ListView elementListView = (ListView) findViewById(R.id.element_list_view);
        startBackgroundJob();
        ParseQuery.getQuery(DB.Table.ROOM)
                .findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(final List<ParseObject> rooms, ParseException e) {
                        if (e == null) {
                            finishBackgroundJob();
                            configureListView(elementListView, rooms);
                        } else {
                            throw new RuntimeException("Failed to retrieve rooms", e);
                        }
                    }
                });
    }

    private void configureListView(ListView userListView, final List<ParseObject> rooms) {
        final List<String> roomNames = ParseHelper.toListKeepOrder(rooms, DB.Column.ROOM_NAME);
        userListView.setAdapter(new ArrayAdapter<>(this, R.layout.row_element, R.id.element_name, roomNames));
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ParseObject selectedRoom = rooms.get(position);
                ParseDao.joinRoom(ParseUser.getCurrentUser(), selectedRoom, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (ScoreManager.anyScores()) {
                            startBackgroundJob();
                            sendMyHighestScoreIfNeeded(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        finishBackgroundJob();
                                        returnResult(selectedRoom.getObjectId(), selectedRoom.getString(DB.Column.ROOM_NAME));
                                    } else {
                                        throw new RuntimeException("Failed to send my highest score", e);
                                    }
                                }
                            });
                        } else {
                            finishBackgroundJob();
                            Log.i(TAG, "We have no local scores, so let's just return the room");
                            returnResult(selectedRoom.getObjectId(), selectedRoom.getString(DB.Column.ROOM_NAME));
                        }
                    }
                });
            }
        });
    }

    /**
     * I was just invited into a room, and I happen to have some local highscores. There are two
     * possible scenarios:
     * - I was already registered in Parse: then, nothing to do. We assume that, if there's one
     *   highscore in Parse, everything is up to date
     * - I never joined Parse: then it's fair that we submit our local highest score right now
     */
    private void sendMyHighestScoreIfNeeded(final SaveCallback saveCallback) {
        ParseQuery.getQuery(DB.Table.HIGHSCORE)
                .whereEqualTo(DB.Column.HIGHSCORE_USER, ParseUser.getCurrentUser())
                .countInBackground(new CountCallback() {
                    @Override
                    public void done(int submittedHighscores, ParseException e) {
                        if (submittedHighscores > 0) {
                            Log.i(TAG, "We have some local scores, but we assume Parse it up to date");
                            saveCallback.done(null);
                        } else {
                            Log.i(TAG, "We have some local scores, let's submit the highest");
                            ParseDao.createHighscore(ParseUser.getCurrentUser(), GlobalState.level, // TODO: fuck me!
                                    ScoreManager.getHighestScore(new Level(GlobalState.level)), saveCallback);
                        }
                    }
                });

    }

    private void returnResult(String roomId, String roomName) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("roomId", roomId);
        returnIntent.putExtra("roomName", roomName);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
