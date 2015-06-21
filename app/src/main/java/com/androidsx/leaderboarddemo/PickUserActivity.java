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


public class PickUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_element);

        final ListView userListView = (ListView) findViewById(R.id.element_list_view);
        fillListViewInBackground(userListView);
    }

    private void fillListViewInBackground(final ListView userListView) {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    final List<String> userNames = new ArrayList<>();
                    for (ParseObject parseObject : parseObjects) {
                        userNames.add((String) parseObject.get("username"));
                    }

                    configureListView(userListView, userNames);
                } else {
                    throw new RuntimeException("Failed to retrieve users", e);
                }
            }
        });
    }

    private void configureListView(ListView userListView, final List<String> userNames) {
        userListView.setAdapter(new ArrayAdapter<>(PickUserActivity.this, R.layout.row_element, R.id.element_name, userNames));
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                returnResult(userNames.get(position));
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
