package com.androidsx.leaderboarddemo.data;


import com.parse.ParseUser;

/**
 * GLobal state in public static variables like a boss.
 */
public class GlobalState {
    /** We have only one level, so there's no need to worry too much about this. */
    public static String level = "preRelease1";

    /** The latest room that the user saw. We expect most people to have only one room, anyway. */
    public static String activeRoomId = null;
    public static String activeRoomName = null;

    public static boolean isActiveUser() {
        return ParseUser.getCurrentUser() != null && activeRoomId != null;
    }
}
