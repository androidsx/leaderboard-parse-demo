package com.androidsx.leaderboarddemo.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.androidsx.leaderboarddemo.R;
import com.androidsx.leaderboarddemo.data.DB;
import com.androidsx.leaderboarddemo.data.ParseHelper;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;


/**
 * Pick a room among those that the user belongs to.
 *
 * - Incoming: none.
 * - Outfoing: room name for the room that was picked.
 */
public class PickRoomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_element);

        final ListView elementListView = (ListView) findViewById(R.id.element_list_view);
        fillListViewInBackground(elementListView);
    }

    private void fillListViewInBackground(final ListView elementListView) {
        ParseUser.getCurrentUser()
                .fetchInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if (e == null) {
                            final List<ParseObject> rooms = parseObject.getList(DB.Column.USER_ROOMS);
                            fetchRoomObjects(rooms);
                            configureListView(elementListView, rooms);
                        } else {
                            throw new RuntimeException("Failed to retrieve users", e);
                        }
                    }
                });
    }

    private void fetchRoomObjects(List<ParseObject> rooms) {
        for (ParseObject room : rooms) {
            // Fetch the room name, right here in the UI thread. Don't do this in Live
            try {
                room.fetch();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void configureListView(ListView userListView, final List<ParseObject> rooms) {
        final List<String> roomNames = ParseHelper.toListKeepOrder(rooms, DB.Column.ROOM_NAME);
        userListView.setAdapter(new ArrayAdapter<>(this, R.layout.row_element, R.id.element_name, roomNames));
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                returnResult(rooms.get(position).getObjectId(), rooms.get(position).getString(DB.Column.ROOM_NAME));
            }
        });
    }

    private void returnResult(String roomId, String roomName) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("roomId", roomId);
        returnIntent.putExtra("roomName", roomName);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}