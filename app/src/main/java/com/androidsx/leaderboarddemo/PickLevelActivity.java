package com.androidsx.leaderboarddemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;


/**
 * Lets the user pick a level out of a hardcoded list.
 *
 * - Incoming: nothing.
 * - Outfoing: name of the level that was picked.
 */
public class PickLevelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_element);

        final List<String> levelNames = fillData();
        final ListView levelListView = (ListView) findViewById(R.id.element_list_view);
        levelListView.setAdapter(new ArrayAdapter<>(this, R.layout.row_element, R.id.element_name, levelNames));
        levelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                returnResult(levelNames.get(position));
            }
        });
    }

    private List<String> fillData() {
        return Arrays.asList("preRelease1", "preRelease2");
    }

    private void returnResult(String pickedLevelName) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", pickedLevelName);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
