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
 * Lets the user pick a level. We don't have a "Level" table, so we'll loop over all scores. This
 * query will never have to be done in the actual game, so that's fine.
 *
 * - Incoming: nothing.
 * - Outfoing: name of the level that was picked.
 */
public class PickLevelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_element);

        final ListView elementListView = (ListView) findViewById(R.id.element_list_view);
        fillListViewInBackground(elementListView);
    }

    private void fillListViewInBackground(final ListView elementListView) {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery(DB.Table.HIGHSCORE);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    ArrayList<String> levelNames = ParseHelper.toListNoDuplicates(parseObjects, DB.Column.HIGHSCORE_LEVEL);
                    configureListView(elementListView, levelNames);
                } else {
                    throw new RuntimeException("Failed to retrieve users", e);
                }
            }
        });
    }

    private void configureListView(ListView userListView, final List<String> levelNames) {
        userListView.setAdapter(new ArrayAdapter<>(PickLevelActivity.this, R.layout.row_element, R.id.element_name, levelNames));
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                returnResult(levelNames.get(position));
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
