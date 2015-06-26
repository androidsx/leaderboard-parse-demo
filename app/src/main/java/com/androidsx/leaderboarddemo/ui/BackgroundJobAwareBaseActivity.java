package com.androidsx.leaderboarddemo.ui;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.androidsx.leaderboarddemo.R;

/**
 * Base activity that keeps track of an icon that indicates when a background job is active:
 *
 * - red: not connected to Parse
 * - white: connected, but currently no activity
 * - green: processing some background operation for Parse
 */
public class BackgroundJobAwareBaseActivity extends AppCompatActivity {
    private Menu menu;

    private Drawable pendingMenuStatusUpdate = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_background_job_indicator, menu);
        this.menu = menu;
        if (pendingMenuStatusUpdate != null) {
            updateIcon(pendingMenuStatusUpdate);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.background_job_status) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected final void startBackgroundJob() {
        updateIcon(getResources().getDrawable(R.drawable.ic_background_job_on));
    }

    protected final void finishBackgroundJob() {
        updateIcon(getResources().getDrawable(R.drawable.ic_background_job_off));
    }

    private void updateIcon(Drawable icon) {
        if (menu == null) {
            pendingMenuStatusUpdate = icon;
        } else {
            menu.getItem(0).setIcon(icon);
            pendingMenuStatusUpdate = null;
        }
    }
}
