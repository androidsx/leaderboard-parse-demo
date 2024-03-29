package com.androidsx.leaderboarddemo.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidsx.leaderboarddemo.R;
import com.androidsx.leaderboarddemo.data.local.ActiveRoomManager;
import com.androidsx.leaderboarddemo.data.local.LevelManager;
import com.androidsx.leaderboarddemo.deeplink.BranchHelper;
import com.androidsx.leaderboarddemo.data.remote.ParseDao;
import com.androidsx.leaderboarddemo.model.Room;
import com.androidsx.leaderboarddemo.ui.BackgroundJobAwareBaseActivity;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class AdminActivity extends BackgroundJobAwareBaseActivity {
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
                ActiveRoomManager.saveActiveRoom(this, null);
                //loginAs(data.getStringExtra("username")); // not active
            }
        } else if (requestCode == PICK_ROOM_REQUEST) {
            if (resultCode == RESULT_OK) {
                ActiveRoomManager.saveActiveRoom(this, new Room(data.getStringExtra("roomId"), data.getStringExtra("roomName")));
                updateUi();
            }
        } else if (requestCode == PICK_LEVEL_REQUEST) {
            if (resultCode == RESULT_OK) {
                LevelManager.levelName = data.getStringExtra("result");
                updateUi();
            }
        } else {
            throw new IllegalArgumentException("Unexpected request code: " + requestCode);
        }
    }

    private void updateUi() {
        ((TextView) findViewById(R.id.current_user)).setText(ParseUser.getCurrentUser() == null ? "<none>" : ParseUser.getCurrentUser().getUsername() + " (" + ParseUser.getCurrentUser().getObjectId() + ")");
        ((TextView) findViewById(R.id.picked_room)).setText(ActiveRoomManager.getActiveRoom(this) == null ? "<none>" : ActiveRoomManager.getActiveRoom(this).getName() + " (" + ActiveRoomManager.getActiveRoom(this).getObjectId() + ")");
        ((TextView) findViewById(R.id.picked_level)).setText(DEFAULT_PICK.equals(LevelManager.levelName) ? "<none>" : LevelManager.levelName);
    }

    public void pickUser(View view) {
        startActivityForResult(new Intent(this, PickUserActivity.class), PICK_USER_REQUEST);
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

    public void loginAnonymous(View view) {
        startBackgroundJob();
        ParseDao.anonymousLogin(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    finishBackgroundJob();
                    Toast.makeText(AdminActivity.this, "Logged in anonymously", Toast.LENGTH_SHORT).show();
                    updateUi();
                } else {
                    throw new RuntimeException("Failed to login anonymously or to perform the installation", e);
                }
            }
        });
    }

    public void logout(View view) {
        startBackgroundJob();
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    finishBackgroundJob();
                    updateUi();
                } else {
                    throw new RuntimeException("Failed to login anonymously or to perform the installation", e);
                }
            }
        });
    }

    public void shareRoom(View view) {
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
}
