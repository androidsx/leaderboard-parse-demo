package com.androidsx.leaderboarddemo;

import android.app.Application;

import com.parse.Parse;

public class LeaderboardDemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "HEO6qBMPVW2VrQYmQdZn9cgbflOuKY99NIJDa3aE", "xDLxdnhe3rEY1d7zSrVhGMMLBWGTlE5cY9wkhFlx");
    }
}
