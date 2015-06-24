package com.androidsx.leaderboarddemo;

import com.parse.ParseUser;


/**
 * GLobal state in public static variables like a boss.
 */
public class GlobalState {
    /** We have only one level, so there's no need to worry too much about this. */
    public static final String level = "preRelease1";

    /** Current user. The guy submitting highscores and getting pushes. Not necessarily logged in. */
    static ParseUser me = null;
}
