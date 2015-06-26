package com.androidsx.leaderboarddemo.ui;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.androidsx.leaderboarddemo.R;

/**
 * Base activity that keeps track of an icon that indicates when a background job is active.
 */
public class BackgroundJobAwareBaseActivity extends AppCompatActivity {
    private Menu menu;

    private Drawable pendingMenuStatusUpdate = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_background_job_indicator, menu);
        this.menu = menu;
        if (pendingMenuStatusUpdate != null) {
            this.menu.getItem(0).setIcon(pendingMenuStatusUpdate);
            pendingMenuStatusUpdate = null;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.acton_parse_status) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected final void startBackgroundJob() {
        if (menu == null) {
            pendingMenuStatusUpdate = getResources().getDrawable(R.drawable.ic_background_job_on);
        } else {
            pendingMenuStatusUpdate = null;
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_background_job_on));
        }
    }

    protected final void finishBackgroundJob() {
        if (menu == null) {
            pendingMenuStatusUpdate = getResources().getDrawable(R.drawable.ic_background_job_off);
        } else {
            pendingMenuStatusUpdate = null;
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_background_job_off));
        }
    }
}
