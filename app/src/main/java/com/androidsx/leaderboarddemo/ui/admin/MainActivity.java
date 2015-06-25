package com.androidsx.leaderboarddemo.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidsx.leaderboarddemo.R;
import com.androidsx.leaderboarddemo.data.GlobalState;
import com.androidsx.leaderboarddemo.data.ParseDao;
import com.androidsx.leaderboarddemo.ui.mock.LeaderboardActivity;
import com.androidsx.leaderboarddemo.ui.mock.NewRoomActivity;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class MainActivity extends AppCompatActivity {
    private static final String DEFAULT_PICK = "";

    private static final int PICK_USER_REQUEST = 1;
    private static final int PICK_ROOM_REQUEST = 2;
    private static final int PICK_LEVEL_REQUEST = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateUi();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_USER_REQUEST) {
            if (resultCode == RESULT_OK) {
                GlobalState.activeRoomId = DEFAULT_PICK;
                GlobalState.activeRoomName = DEFAULT_PICK;
                loginAs(data.getStringExtra("username"));
            }
        } else if (requestCode == PICK_ROOM_REQUEST) {
            if (resultCode == RESULT_OK) {
                GlobalState.activeRoomId = data.getStringExtra("roomId");
                GlobalState.activeRoomName = data.getStringExtra("roomName");
                updateUi();
            }
        } else if (requestCode == PICK_LEVEL_REQUEST) {
            if (resultCode == RESULT_OK) {
                GlobalState.level = data.getStringExtra("result");
                updateUi();
            }
        } else {
            throw new IllegalArgumentException("Unexpected request code: " + requestCode);
        }
    }

    private void updateUi() {
        ((TextView) findViewById(R.id.current_user)).setText(ParseUser.getCurrentUser() == null ? "<none>" : ParseUser.getCurrentUser().getUsername() + " (" + ParseUser.getCurrentUser().getObjectId() + ")");
        ((TextView) findViewById(R.id.picked_room)).setText(DEFAULT_PICK.equals(GlobalState.activeRoomId) ? "<none>" : GlobalState.activeRoomName + " (" + GlobalState.activeRoomId + ")");
        ((TextView) findViewById(R.id.picked_level)).setText(DEFAULT_PICK.equals(GlobalState.level) ? "<none>" : GlobalState.level);
    }

    public void pickUser(View view) {
        startActivityForResult(new Intent(this, PickUserActivity.class), PICK_USER_REQUEST);
    }

    public void newRoom(View view) {
        if (ParseUser.getCurrentUser() == null) {
            Toast.makeText(this, "Must log in first, pataliebre", Toast.LENGTH_LONG).show();
        } else {
            startActivityForResult(new Intent(this, NewRoomActivity.class), PICK_ROOM_REQUEST);
        }
    }

    public void pickRoom(View view) {
        startActivityForResult(new Intent(this, PickRoomActivity.class), PICK_ROOM_REQUEST);
    }

    public void joinRoom(View view) {
        if (ParseUser.getCurrentUser() ==  null) {
            Toast.makeText(this, "Must log in first, o que te pensabas?", Toast.LENGTH_LONG).show();
        } else {
            startActivityForResult(new Intent(this, JoinRoomActivity.class), PICK_ROOM_REQUEST);
        }
    }

    public void pickLevel(View view) {
        startActivityForResult(new Intent(this, PickLevelActivity.class), PICK_LEVEL_REQUEST);
    }

    public void showLeaderboard(View view) {
        LeaderboardActivity.startLeaderboardActivity(this, GlobalState.activeRoomId, GlobalState.activeRoomName, GlobalState.level);
    }

    public void loginAnonymous(View view) {
        ParseDao.anonymousLogin(MainActivity.this, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    updateUi();
                } else {
                    throw new RuntimeException("Failed to login anonymously or to perform the installation", e);
                }
            }
        });
    }

    public void logout(View view) {
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    updateUi();
                } else {
                    throw new RuntimeException("Failed to login anonymously or to perform the installation", e);
                }
            }
        });
    }

    private void loginAs(final String username) {
        ParseDao.loginAs(this, username, "lala", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    updateUi();
                } else {
                    throw new RuntimeException("Failed to login privately or to perform the installation", e);
                }
            }
        });
    }
}
