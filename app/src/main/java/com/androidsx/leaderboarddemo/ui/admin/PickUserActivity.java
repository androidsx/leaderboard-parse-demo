package com.androidsx.leaderboarddemo.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.androidsx.leaderboarddemo.R;
import com.androidsx.leaderboarddemo.data.remote.DB;
import com.androidsx.leaderboarddemo.ui.BackgroundJobAwareBaseActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


/**
 * Lets the user pick a user out of all registered users in Parse.
 *
 * - Incoming: nothing.
 * - Outfoing: username for the user that was picked.
 */
public class PickUserActivity extends BackgroundJobAwareBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_element);

        final ListView elementListView = (ListView) findViewById(R.id.element_list_view);
        fillListViewInBackground(elementListView);
    }

    private void fillListViewInBackground(final ListView elementListView) {
        startBackgroundJob();
        final ParseQuery<ParseObject> query = ParseQuery.getQuery(DB.Table.USER);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    finishBackgroundJob();
                    final List<Pair<String, String>> users = new ArrayList<>();
                    for (ParseObject parseObject : parseObjects) {
                        users.add(new Pair<String, String>(parseObject.getObjectId(), (String) parseObject.get(DB.Column.USER_NAME)) {
                            @Override
                            public String toString() {
                                return second + " (" + first + ")";
                            }
                        });
                    }

                    configureListView(elementListView, users);
                } else {
                    throw new RuntimeException("Failed to retrieve users", e);
                }
            }
        });
    }

    private void configureListView(ListView userListView, final List<Pair<String, String>> users) {
        userListView.setAdapter(new ArrayAdapter<>(PickUserActivity.this, R.layout.row_element, R.id.element_name, users));
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                returnResult(users.get(position).first, users.get(position).second);
            }
        });
    }

    private void returnResult(String userId, String pickedUserName) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("userId", userId);
        returnIntent.putExtra("username", pickedUserName);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
