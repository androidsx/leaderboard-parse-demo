package com.androidsx.leaderboarddemo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;


public class MainActivity extends AppCompatActivity {
    public static final String DEFAULT_PICK = "";

    private static final int PICK_USER_REQUEST = 1;
    public static final int PICK_ROOM_REQUEST = 2;
    private static final int PICK_LEVEL_REQUEST = 3;

    // Current selection through the UI. Yes, static, like a boss XD
    private static String userId = DEFAULT_PICK;
    private static String username = DEFAULT_PICK;
    private static String roomName = DEFAULT_PICK;
    private static String level = DEFAULT_PICK;

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
                userId = data.getStringExtra("userId");
                username = data.getStringExtra("username");
                roomName = DEFAULT_PICK;
                updateUi();
            }
        } else if (requestCode == PICK_ROOM_REQUEST) {
            if (resultCode == RESULT_OK) {
                roomName = data.getStringExtra("result");
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
        ((TextView) findViewById(R.id.picked_user)).setText(username);
        ((TextView) findViewById(R.id.picked_room)).setText(roomName);
        ((TextView) findViewById(R.id.picked_level)).setText(level);
    }

    public void resetDbData(View view) throws ParseException {
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

                    final ParseObject pau = DB.saveUser("pau");
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
    }

    public void pickUser(View view) {
        startActivityForResult(new Intent(this, PickUserActivity.class), PICK_USER_REQUEST);
    }

    public void pickRoom(View view) {
        Intent intent = new Intent(this, PickRoomActivity.class);
        intent.putExtra("userId", userId);
        startActivityForResult(intent, PICK_ROOM_REQUEST);
    }

    public void pickLevel(View view) {
        startActivityForResult(new Intent(this, PickLevelActivity.class), PICK_LEVEL_REQUEST);
    }

    public void playNewGame(View view) {
        Intent intent = new Intent(this, NewGameActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("username", username);
        intent.putExtra("level", level);
        startActivity(intent);
    }

    public void showLeaderboard(View view) {
        Intent intent = new Intent(this, LeaderboardActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("username", username);
        intent.putExtra("roomName", roomName);
        intent.putExtra("level", level);
        startActivity(intent);
    }
}
