package com.androidsx.leaderboarddemo.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidsx.leaderboarddemo.R;
import com.androidsx.leaderboarddemo.data.BranchHelper;
import com.androidsx.leaderboarddemo.data.GlobalState;
import com.androidsx.leaderboarddemo.data.ParseDao;
import com.androidsx.leaderboarddemo.model.Room;
import com.androidsx.leaderboarddemo.ui.BackgroundJobAwareBaseActivity;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;


public class MainActivity extends BackgroundJobAwareBaseActivity {
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
                GlobalState.activeRoom = null;
                //loginAs(data.getStringExtra("username")); // not active
            }
        } else if (requestCode == PICK_ROOM_REQUEST) {
            if (resultCode == RESULT_OK) {
                GlobalState.activeRoom = new Room(data.getStringExtra("roomId"), data.getStringExtra("roomName"));
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
        ((TextView) findViewById(R.id.picked_room)).setText(GlobalState.activeRoom == null ? "<none>" : GlobalState.activeRoom.getName() + " (" + GlobalState.activeRoom.getObjectId() + ")");
        ((TextView) findViewById(R.id.picked_level)).setText(DEFAULT_PICK.equals(GlobalState.level) ? "<none>" : GlobalState.level);
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
                    Toast.makeText(MainActivity.this, "Logged in anonymously", Toast.LENGTH_SHORT).show();
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
        } else if (GlobalState.activeRoom == null) {
            Toast.makeText(this, "Must select a room first, o que te pensabas?", Toast.LENGTH_LONG).show();
        } else {
            final String username = ParseUser.getCurrentUser().getUsername();
            final String roomName = GlobalState.activeRoom.getName();
            String roomId = GlobalState.activeRoom.getObjectId();

            BranchHelper.generateBranchLink(this, username, roomName, roomId, new Branch.BranchLinkCreateListener() {

                @Override
                public void onLinkCreate(String url, BranchError branchError) {
                    // somthing weird with null, ask Omar or just use the market link :)
//                    if (branchError != null) {
                        String shareBody = "Compete against me in my game room \"" + roomName + "\" of Pencil Gravity: " + url;

                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

                        startActivity(Intent.createChooser(sharingIntent, "Share via"));
//                    } else {
//                        Log.e("MainActivity", "Error while creating the link: " + branchError);
//                        Toast.makeText(MainActivity.this, "Error while creating the link: " + branchError, Toast.LENGTH_LONG).show();
//                    }
                }
            });

        }
    }
}
