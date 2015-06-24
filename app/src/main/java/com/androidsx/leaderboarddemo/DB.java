package com.androidsx.leaderboarddemo;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

public class DB {
    static class Table {
        public static final String USER = "_User";
        public static final String ROOM = "Room";
        public static final String HIGHSCORE = "HighScore";
    }

    static class Column {
        public static final String USER_NAME = "username";
        public static final String USER_ROOMS = "rooms";

        public static final String ROOM_NAME = "name";

        public static final String HIGHSCORE_LEVEL = "level";
        public static final String HIGHSCORE_USER = "user";
        public static final String HIGHSCORE_SCORE = "score";
    }

    static class Value {
        public static final String LEVEL_1 = "preRelease1";
        public static final String LEVEL_2 = "preRelease2";
    }

    /** Do not call from the main thread. */
    public static void deleteTable(final String tableName) throws ParseException {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        List<ParseObject> parseObjects = query.find();
        for (ParseObject parseObject : parseObjects) {
            parseObject.delete();
        }
    }

    /** Do not call from the main thread. */
    public static ParseObject saveRoom(String name) throws ParseException {
        final ParseObject room = new ParseObject(DB.Table.ROOM);
        room.put(DB.Column.ROOM_NAME, name);
        room.save();

        return room;
    }

    /** Do not call from the main thread. */
    public static ParseObject saveUser(String name, ParseObject... rooms) throws ParseException {
        final ParseUser user = new ParseUser();
        user.setUsername(name);
        user.setPassword("aaaa");

        user.put(DB.Column.USER_ROOMS, Arrays.asList(rooms));
        user.signUp();

        return user;
    }

    /** Do not call from the main thread. */
    public static void saveHighscore(String level, ParseObject user, Number score) throws ParseException {
        final ParseObject highscore = new ParseObject(DB.Table.HIGHSCORE);
        highscore.put(DB.Column.HIGHSCORE_LEVEL, level);
        highscore.put(DB.Column.HIGHSCORE_USER, user);
        highscore.put(DB.Column.HIGHSCORE_SCORE, score);
        highscore.save();
    }
}
