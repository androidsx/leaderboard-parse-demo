package com.androidsx.leaderboarddemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


/**
 * Pick a room out of all the rooms that the provided user belongs to. [for the provided level]
 *
 * - Incoming: username.
 * - Outfoing: room name for the room that was picked.
 */
public class PickRoomActivity extends AppCompatActivity {

    // Coming from the calling activity through the extras
    private String username;
    private String level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_element);

        username = getIntent().getStringExtra("username");
        level = getIntent().getStringExtra("level");

        final ListView elementListView = (ListView) findViewById(R.id.element_list_view);
        fillListViewInBackground(elementListView);
    }

    private void fillListViewInBackground(final ListView elementListView) {

        final ParseQuery<ParseObject> innerQuery = ParseQuery.getQuery("UserScore");
        innerQuery.whereEqualTo("level", level);

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Room");
        query.whereMatchesQuery("scores", innerQuery);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    final List<String> roomNames = new ArrayList<>();
                    for (ParseObject parseObject : parseObjects) {
                        roomNames.add((String) parseObject.get("name"));
                    }

                    configureListView(elementListView, roomNames);
                } else {
                    throw new RuntimeException("Failed to retrieve rooms", e);
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