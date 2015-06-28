package com.androidsx.leaderboarddemo.data.local;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.androidsx.leaderboarddemo.model.Room;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


/**
 * Keeps track of the latest room that the user checked. We assume that's the leaderboard that he
 * is more interested in.
 *
 * Anyway, usually most users will have 0 or 1 rooms.
 *
 * Persisted in the shared preferences.
 */
public class ActiveRoomManager {
    private static Room activeRoom;

    /** Returns the active room, or null if none exists, or none were set yet. */
    public static Room getActiveRoom(Context context) {
        if (activeRoom == null) {
            final SharedPreferences prefs = context.getSharedPreferences("prefs", Activity.MODE_PRIVATE);
            final String scoresAsJson = prefs.getString("activeRoom", "");
            activeRoom = new Gson().fromJson(scoresAsJson, new TypeToken<Room>() {}.getType());
            return activeRoom;
        } else {
            return activeRoom;
        }
    }

    /** Saves the currently active room. Use {@code null} to unset. */
    public static void saveActiveRoom(Context context, Room room) {
        activeRoom = room;

        final SharedPreferences prefs = context.getSharedPreferences("prefs", Activity.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        if (room == null) {
            editor.putString("roms", null);
        } else {
            editor.putString("rooms", new Gson().toJson(room));
        }
        editor.apply();
    }
}
