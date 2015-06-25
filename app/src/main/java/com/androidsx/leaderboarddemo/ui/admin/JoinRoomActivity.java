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
import com.androidsx.leaderboarddemo.data.ParseDao;
import com.androidsx.leaderboarddemo.data.ParseHelper;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

/**
 * Join an existing room. This is what would happen if the user clicks a link shared by a friend.
 *
 * Same view as {@link PickRoomActivity}, but here we show all rooms, and also we do modify the
 * user object to add the current user to the roomm.
 *
 * - Incoming: none.
 * - Outfoing: room name for the room that was picked.
 */
public class JoinRoomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_element);

        final ListView elementListView = (ListView) findViewById(R.id.element_list_view);
        fillListViewInBackground(elementListView);
    }

    private void fillListViewInBackground(final ListView elementListView) {
        ParseQuery.getQuery(DB.Table.ROOM)
                .findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> rooms, ParseException e) {
                        if (e == null) {
                            configureListView(elementListView, rooms);
                        } else {
                            throw new RuntimeException("Failed to retrieve rooms", e);
                        }
                    }
                });
    }

    private void configureListView(ListView userListView, final List<ParseObject> rooms) {
        final List<String> roomNames = ParseHelper.toListKeepOrder(rooms, DB.Column.ROOM_NAME);
        userListView.setAdapter(new ArrayAdapter<>(this, R.layout.row_element, R.id.element_name, roomNames));
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ParseObject selectedRoom = rooms.get(position);
                ParseDao.addRoomToUser(ParseUser.getCurrentUser(), selectedRoom, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        returnResult(selectedRoom.getObjectId(), selectedRoom.getString(DB.Column.ROOM_NAME));
                    }
                });
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