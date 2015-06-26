package com.androidsx.leaderboarddemo.ui.mock;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.androidsx.leaderboarddemo.R;
import com.androidsx.leaderboarddemo.data.DB;
import com.androidsx.leaderboarddemo.data.GlobalState;
import com.androidsx.leaderboarddemo.data.ParseDao;
import com.androidsx.leaderboarddemo.data.ScoreManager;
import com.androidsx.leaderboarddemo.ui.BackgroundJobAwareBaseActivity;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class NewRoomActivity extends BackgroundJobAwareBaseActivity {
    private static final String TAG = NewRoomActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_room);
    }

    public void createRoom(View view) {
        final String roomName = ((EditText) findViewById(R.id.new_room_name)).getText().toString();

        startBackgroundJob();
        if (ParseUser.getCurrentUser() == null) {
            Log.i(TAG, "No Parse user exists. Will login now");
            ParseDao.anonymousLogin(this, new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.i(TAG, "Logged in. Now let's create the room");
                        createRoomAfterLogin(roomName);
                    } else {
                        throw new RuntimeException("Failed to log in", e);
                    }
                }
            });
        } else {
            Log.i(TAG, "A Parse user exists already (" + ParseUser.getCurrentUser().getUsername() + ".Will login now");
                    createRoomAfterLogin(roomName);
        }
    }

    private void createRoomAfterLogin(String roomName) {
        final ParseObject roomParseObject = new ParseObject(DB.Table.ROOM);
        roomParseObject.put(DB.Column.ROOM_NAME, roomName);
        roomParseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    ParseDao.addRoomToUser(ParseUser.getCurrentUser(), roomParseObject, new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            GlobalState.activeRoomId = roomParseObject.getObjectId();
                            GlobalState.activeRoomName = roomParseObject.getString(DB.Column.ROOM_NAME);
                            createHighscore();
                        }
                    });
                } else {
                    throw new RuntimeException("Failed to create the room", e);
                }
            }
        });
    }

    private void createHighscore() {
        ParseDao.createHighscore(ParseUser.getCurrentUser(), GlobalState.level, ScoreManager.getHighestScore(), new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.i(TAG, "The highscore has been saved");
                Toast.makeText(NewRoomActivity.this, "Room is created, and highscore submitted", Toast.LENGTH_SHORT).show();
                finishBackgroundJob();
                finish();
            }
        });
    }
}
