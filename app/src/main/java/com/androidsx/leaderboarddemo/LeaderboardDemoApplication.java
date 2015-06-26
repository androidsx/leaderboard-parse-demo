package com.androidsx.leaderboarddemo;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.SaveCallback;

import io.branch.referral.Branch;

public class LeaderboardDemoApplication extends Application {
    private static final String TAG = LeaderboardDemoApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        // Branch.io deep link
        Branch.getInstance(this);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "HEO6qBMPVW2VrQYmQdZn9cgbflOuKY99NIJDa3aE", "xDLxdnhe3rEY1d7zSrVhGMMLBWGTlE5cY9wkhFlx");

        final ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i(TAG, "Performed the Parse installation");
                } else {
                    throw new RuntimeException("Failed to install Parse", e);
                }
            }
        });
    }
}
