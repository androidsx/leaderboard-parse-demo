package com.androidsx.leaderboarddemo.ui.mock;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidsx.leaderboarddemo.R;
import com.androidsx.leaderboarddemo.data.local.ActiveRoomManager;
import com.androidsx.leaderboarddemo.data.remote.DB;
import com.androidsx.leaderboarddemo.data.GlobalState;
import com.androidsx.leaderboarddemo.data.remote.ParseDao;
import com.androidsx.leaderboarddemo.data.local.ScoreManager;
import com.androidsx.leaderboarddemo.model.Room;
import com.androidsx.leaderboarddemo.ui.BackgroundJobAwareBaseActivity;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class NewRoomActivity extends BackgroundJobAwareBaseActivity {
    private static final String TAG = NewRoomActivity.class.getSimpleName();

    TextView nicknameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_room);

        findViewById(R.id.nickname_text_view).setVisibility(ParseUser.getCurrentUser() == null ? View.VISIBLE : View.GONE);
        nicknameEditText = (TextView) findViewById(R.id.nickname_edit_text);
        nicknameEditText.setVisibility(ParseUser.getCurrentUser() == null ? View.VISIBLE : View.GONE);
    }

    public void createRoom(View view) {
        final String roomName = ((EditText) findViewById(R.id.new_room_name)).getText().toString();

        startBackgroundJob();
        if (ParseUser.getCurrentUser() == null) {
            Log.i(TAG, "No Parse user exists. Will login now");
            ParseDao.anonymousLogin(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.i(TAG, "Logged in. Now let's update the username and also create the room");
                        final ParseUser currentUser = ParseUser.getCurrentUser();
                        final String newNickname = nicknameEditText.getText().toString();
                        ParseDao.changeUsename(currentUser, newNickname, new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(NewRoomActivity.this, "Named changed to " + newNickname, Toast.LENGTH_SHORT).show();
                                    createRoomAfterLogin(roomName); // well, there was no need to chain these two calls
                                } else {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
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
                    ParseDao.joinRoom(ParseUser.getCurrentUser(), roomParseObject, new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            ActiveRoomManager.saveActiveRoom(NewRoomActivity.this, Room.fromParseObject(roomParseObject));
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
        final int highscore = ScoreManager.getScoreManager(this).getHighestScore(GlobalState.level);
        ParseDao.createHighscore(ParseUser.getCurrentUser(), GlobalState.level, highscore, new SaveCallback() {
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
