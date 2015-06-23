package com.androidsx.leaderboarddemo;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.util.List;

public class DB {
    static class Table {
        public static final String USER = "User2";
        public static final String ROOM = "Room2";
        public static final String HIGHSCORE = "Highscore2";
    }

    static class Column {
        public static final String USER_NAME = "name";
        public static final String USER_ROOMS = "rooms";

        public static final String ROOM_NAME = "name";
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
        final ParseObject user = new ParseObject(DB.Table.USER);
        user.put(DB.Column.USER_NAME, name);
        ParseRelation<ParseObject> relation = user.getRelation(DB.Column.USER_ROOMS);
        for (ParseObject room : rooms) {
            relation.add(room);
        }
        user.save();

        return user;
    }

    /** Do not call from the main thread. */
    public static void saveHighscore(String level, ParseObject user, Number score) throws ParseException {
        final ParseObject highscore = new ParseObject(DB.Table.HIGHSCORE);
        highscore.put("level", level);
        highscore.put("user", user);
        highscore.put("highscore", score);
        highscore.save();
    }
}
