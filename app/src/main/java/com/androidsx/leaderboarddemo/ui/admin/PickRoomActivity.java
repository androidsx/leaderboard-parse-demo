package com.androidsx.leaderboarddemo.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.androidsx.leaderboarddemo.R;
import com.androidsx.leaderboarddemo.data.ParseDao;
import com.androidsx.leaderboarddemo.model.Room;
import com.androidsx.leaderboarddemo.ui.BackgroundJobAwareBaseActivity;

import java.util.List;


/**
 * Pick a room among those that the user belongs to.
 *
 * - Incoming: none.
 * - Outfoing: room name for the room that was picked.
 */
public class PickRoomActivity extends BackgroundJobAwareBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_element);

        final ListView elementListView = (ListView) findViewById(R.id.element_list_view);
        fillListViewInBackground(elementListView);
    }

    private void fillListViewInBackground(final ListView elementListView) {
        startBackgroundJob();
        ParseDao.getRoomsForUser(new ParseDao.RoomFindCallback() {
            @Override
            public void done(List<Room> rooms) {
                finishBackgroundJob();
                configureListView(elementListView, rooms);
            }
        });
    }

    private void configureListView(ListView userListView, final List<Room> rooms) {
        userListView.setAdapter(new ArrayAdapter<>(this, R.layout.row_element, R.id.element_name, rooms));
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                returnResult(rooms.get(position).getObjectId(), rooms.get(position).getName());
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