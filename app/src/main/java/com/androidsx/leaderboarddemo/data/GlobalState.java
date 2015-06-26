package com.androidsx.leaderboarddemo.data;


import com.androidsx.leaderboarddemo.model.Room;

/**
 * Global state in public static variables like a boss.
 *
 * TODO: persist across sessions
 * TODO: when logging in, download the highest score from Parse?
 */
public class GlobalState {
    /** We have only one level, so there's no need to worry too much about this. */
    public static String level = "preRelease1";

    /** It's just the latest room the user saw. We assume he is competing against these guys. */
    public static Room activeRoom;
}
