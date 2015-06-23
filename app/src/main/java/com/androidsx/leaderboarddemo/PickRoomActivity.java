package com.androidsx.leaderboarddemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


/**
 * Pick a room among those that the provided user belongs to.
 *
 * - Incoming: userId.
 * - Outfoing: room name for the room that was picked.
 */
public class PickRoomActivity extends AppCompatActivity {

    // Coming from the calling activity through the extras
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_element);

        userId = getIntent().getStringExtra("userId");

        final ListView elementListView = (ListView) findViewById(R.id.element_list_view);
        fillListViewInBackground(elementListView);
    }

    private void fillListViewInBackground(final ListView elementListView) {
        ParseQuery.getQuery(DB.Table.USER)
                .include(DB.Column.USER_ROOMS)
                .getInBackground(userId, new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if (e == null) {
                            final List<ParseObject> rooms = parseObject.getList(DB.Column.USER_ROOMS);
                            final List<String> roomNames = ParseHelper.toListNoDuplicates(rooms, DB.Column.ROOM_NAME);
                            configureListView(elementListView, roomNames);
                        } else {
                            throw new RuntimeException("Failed to retrieve users", e);
                        }
                    }
                });
    }

    private void configureListView(ListView userListView, final List<String> roomNames) {
        userListView.setAdapter(new ArrayAdapter<>(PickRoomActivity.this, R.layout.row_element, R.id.element_name, roomNames));
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                returnResult(roomNames.get(position));
            }
        });
    }

    private void returnResult(String pickedLevelName) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", pickedLevelName);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}