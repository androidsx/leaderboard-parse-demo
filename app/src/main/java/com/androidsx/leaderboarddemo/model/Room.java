package com.androidsx.leaderboarddemo.model;

import com.androidsx.leaderboarddemo.data.DB;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private String objectId;
    private String name;

    private Room(String objectId, String name) {
        this.objectId = objectId;
        this.name = name;
    }

    public String getObjectId() {
        return objectId;
    }

    public String getName() {
        return name;
    }

    /** For convenience for the the listviews and spinners. */
    @Override
    public String toString() {
        return name;
    }

    private static Room fromParseObject(ParseObject roomParseObject) {
        return new Room(roomParseObject.getObjectId(), roomParseObject.getString(DB.Column.ROOM_NAME));
    }

    public static List<Room> fromParseObjectList(List<ParseObject> roomParseObjects) {
        final List<Room> rooms = new ArrayList<>();
        for (ParseObject roomParseObject : roomParseObjects) {
            rooms.add(Room.fromParseObject(roomParseObject));
        }
        return rooms;
    }
}
