package com.androidsx.leaderboarddemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidsx.leaderboarddemo.R;
import com.androidsx.leaderboarddemo.data.GlobalState;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        //((TextView) findViewById(R.id.picked_user)).setText(DEFAULT_PICK.equals(userId) ? "<none>" : username + " (" + userId + ")");
        ((TextView) findViewById(R.id.picked_room)).setText(DEFAULT_PICK.equals(GlobalState.activeRoomId) ? "<none>" : GlobalState.activeRoomName + " (" + GlobalState.activeRoomId + ")");
        ((TextView) findViewById(R.id.picked_level)).setText(DEFAULT_PICK.equals(GlobalState.level) ? "<none>" : GlobalState.level);
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
                    updateUi();
                    Toast.makeText(MainActivity.this, "Welcome, " + ParseUser.getCurrentUser().getUsername(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "Welcome back, " + ParseUser.getCurrentUser().getUsername(), Toast.LENGTH_SHORT).show();
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
        startActivityForResult(new Intent(this, PickRoomActivity.class), PICK_ROOM_REQUEST);
    }

    public void joinRoom(View view) {
        startActivityForResult(new Intent(this, JoinRoomActivity.class), PICK_ROOM_REQUEST);
    }

    public void pickLevel(View view) {
        startActivityForResult(new Intent(this, PickLevelActivity.class), PICK_LEVEL_REQUEST);
    }

    public void playNewGame(View view) {
        Intent intent = new Intent(this, NewGameActivity.class);
        intent.putExtra("level", GlobalState.level);
        startActivity(intent);
    }

    public void showLeaderboard(View view) {
        LeaderboardActivity.startLeaderboardActivity(this, GlobalState.activeRoomId, GlobalState.activeRoomName, GlobalState.level);
    }

    private void loginAs(final String username) {
        if (ParseUser.getCurrentUser() == null) {
            ParseUser.logInInBackground(username, "lala", new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e == null) {
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
