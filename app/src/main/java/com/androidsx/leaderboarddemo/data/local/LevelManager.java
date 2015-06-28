package com.androidsx.leaderboarddemo.data.local;


/**
 * Silly manager for the level. Since our game has only one level, we just hardcode it here. The
 * admin interface allows us to simulate more than one, though.
 *
 * TODO: is there a way to store an identifier for a randomly generated level (the seed?). In that
 *       case, we should turn the level name into a Level object, and store that seed. It would be
 *       great to study some particular level generations, or to allow to compite in the same exact
 *       conditions.
 */
public class LevelManager {
    public static String levelName = "preRelease1";
}
