package com.androidsx.leaderboarddemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidsx.leaderboarddemo.R;
import com.androidsx.leaderboarddemo.data.ParseHelper;
import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class MainActivity extends AppCompatActivity {
    public static final String DEFAULT_PICK = "";

    private static final int PICK_USER_REQUEST = 1;
    public static final int PICK_ROOM_REQUEST = 2;
    private static final int PICK_LEVEL_REQUEST = 3;

    /** @deprecated is this not redundant with ParseUser.getCurrentUser? */
    @Deprecated ParseUser me;

    // Current selection. Yes, static, like a boss XD
    private static String roomId = DEFAULT_PICK;
    private static String roomName = DEFAULT_PICK;
    private static String level = DEFAULT_PICK;
    @Deprecated private static String username = DEFAULT_PICK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ParseUser.getCurrentUser() != null) {
            me = ParseUser.getCurrentUser();
        }
        updateUi();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_USER_REQUEST) {
            if (resultCode == RESULT_OK) {
                username = data.getStringExtra("username");
                roomId = DEFAULT_PICK;
                roomName = DEFAULT_PICK;
                loginAs(username);
            }
        } else if (requestCode == PICK_ROOM_REQUEST) {
            if (resultCode == RESULT_OK) {
                roomId = data.getStringExtra("roomId");
                roomName = data.getStringExtra("roomName");
                updateUi();
            }
        } else if (requestCode == PICK_LEVEL_REQUEST) {
            if (resultCode == RESULT_OK) {
                level = data.getStringExtra("result");
                updateUi();
            }
        } else {
            throw new IllegalArgumentException("Unexpected request code: " + requestCode);
        }
    }

    private void updateUi() {
        ((TextView) findViewById(R.id.current_user)).setText(me == null ? "<none>" : me.getUsername() + " (" + me.getObjectId() + ")");
        //((TextView) findViewById(R.id.picked_user)).setText(DEFAULT_PICK.equals(userId) ? "<none>" : username + " (" + userId + ")");
        ((TextView) findViewById(R.id.picked_room)).setText(DEFAULT_PICK.equals(roomId) ? "<none>" : roomName + " (" + roomId + ")");
        ((TextView) findViewById(R.id.picked_level)).setText(DEFAULT_PICK.equals(level) ? "<none>" : level);
    }

    public void resetDbData(View view) throws ParseException {
        Toast.makeText(this, "Out of service. Use the Parse Cloud script", Toast.LENGTH_LONG).show();

        /*
        // TODO: Show confirmation dialog
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    DB.deleteTable(DB.Table.USER);
                    DB.deleteTable(DB.Table.ROOM);
                    DB.deleteTable(DB.Table.HIGHSCORE);

                    final ParseObject roomWithPauEspinchi = DB.saveRoom("roomWithPauEspinchi");
                    final ParseObject roomWithOmarJosepPocho = DB.saveRoom("roomWithOmarJosepPocho");
                    final ParseObject roomWithLuisEspinchi = DB.saveRoom("roomWithLuisEspinchi");

                    final ParseObject pau = DB.saveUser("pau", roomWithPauEspinchi);
                    final ParseObject espinchi = DB.saveUser("espinchi", roomWithPauEspinchi, roomWithLuisEspinchi);
                    final ParseObject omar = DB.saveUser("ompemi", roomWithOmarJosepPocho);
                    final ParseObject josep = DB.saveUser("josep", roomWithOmarJosepPocho);
                    final ParseObject pocho = DB.saveUser("pocho", roomWithOmarJosepPocho);
                    final ParseObject luis = DB.saveUser("luis", roomWithLuisEspinchi);


                    DB.saveHighscore(DB.Value.LEVEL_1, espinchi, 2000);
                    DB.saveHighscore(DB.Value.LEVEL_1, pau, 999);
                    DB.saveHighscore(DB.Value.LEVEL_1, omar, 100);
                    DB.saveHighscore(DB.Value.LEVEL_1, josep, 301);
                    DB.saveHighscore(DB.Value.LEVEL_1, pocho, 300);
                    DB.saveHighscore(DB.Value.LEVEL_1, luis, 120);

                    DB.saveHighscore(DB.Value.LEVEL_2, espinchi, 540);
                    DB.saveHighscore(DB.Value.LEVEL_2, luis, 550);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }
        }.execute();
        */
    }

    public void login(View view) {
        final ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            ParseHelper.anonymousLogin(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    me = ParseUser.getCurrentUser();
                    updateUi();
                    Toast.makeText(MainActivity.this, "Welcome, " + me.getUsername(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            me = currentUser;
            Toast.makeText(MainActivity.this, "Welcome back, " + me.getUsername(), Toast.LENGTH_SHORT).show();
            updateUi();
        }
    }

    public void logout(View view) {
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                updateUi(); // We ignore potential exceptions here
            }
        });
    }

    public void pickUser(View view) {
        startActivityForResult(new Intent(this, PickUserActivity.class), PICK_USER_REQUEST);
    }

    public void newRoom(View view) {
        startActivityForResult(new Intent(this, NewRoomActivity.class), PICK_ROOM_REQUEST);
    }

    public void pickRoom(View view) {
        Intent intent = new Intent(this, PickRoomActivity.class);
        intent.putExtra("userId", me.getObjectId());
        startActivityForResult(intent, PICK_ROOM_REQUEST);
    }

    public void joinRoom(View view) {
        Toast.makeText(this, "Not yet implemented ;)", Toast.LENGTH_LONG).show();
    }

    public void pickLevel(View view) {
        startActivityForResult(new Intent(this, PickLevelActivity.class), PICK_LEVEL_REQUEST);
    }

    public void playNewGame(View view) {
        Intent intent = new Intent(this, NewGameActivity.class);
        intent.putExtra("username", me.getUsername());
        intent.putExtra("level", level);
        startActivity(intent);
    }

    public void showLeaderboard(View view) {
        Intent intent = new Intent(this, LeaderboardActivity.class);
        intent.putExtra("userId", me.getObjectId());
        intent.putExtra("roomId", roomId);
        intent.putExtra("level", level);
        startActivity(intent);
    }

    private void loginAs(final String username) {
        if (ParseUser.getCurrentUser() == null) {
            ParseUser.logInInBackground(username, "lala", new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e == null) {
                        me = user;
                        ParseHelper.assignUserToInstallation(new ParseHelper.ToastSaveCallback(MainActivity.this));
                        Toast.makeText(MainActivity.this, "Welcome, " + user.getUsername(), Toast.LENGTH_SHORT).show();
                        updateUi();
                    } else {
                        throw new RuntimeException("Failed to log in as espinchi", e);
                    }
                }
            });
        } else {
            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        loginAs(username); // let's hope we don't end up in an infinite loop
                    } else {
                        throw new RuntimeException("Failed to log out", e);
                    }
                }
            });
        }
    }
}
