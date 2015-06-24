package com.androidsx.leaderboarddemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;


public class NewRoomActivity extends AppCompatActivity {

    // Coming from the calling activity through the extras
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_room);

        userId = getIntent().getStringExtra("userId");
    }

    public void createRoom(View view) {
        final ParseObject roomParseObject = new ParseObject(DB.Table.ROOM);
        roomParseObject.put(DB.Column.ROOM_NAME, ((EditText) findViewById(R.id.new_room_name)).getText().toString());
        roomParseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    ParseQuery.getQuery(DB.Table.USER)
                            .getInBackground(userId, new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject userParseObject, ParseException e) {
                                    if (e == null) {
                                        userParseObject.addUnique(DB.Column.USER_ROOMS, roomParseObject);
                                        userParseObject.saveInBackground(new ParseHelper.ToastAndFinishSaveCallback(NewRoomActivity.this));
                                    } else {
                                        throw new RuntimeException("Failed to get the user", e);
                                    }
                                }
                            });
                } else {
                    throw new RuntimeException("Failed to create the room", e);
                }
            }
        });
    }
}
