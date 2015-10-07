package com.androidsx.leaderboarddemo.data.remote;

import android.content.Context;
import android.util.Log;

import com.androidsx.leaderboarddemo.model.Room;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class ParseDao {
    private static final String TAG = ParseDao.class.getSimpleName();

    public static void anonymousLogin(final SaveCallback saveCallback) {
        Log.d(TAG, "Performing anonymous login...");
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Anonymous login performed. Will now assign this user (" + user.getUsername() + ") to the parse installation");
                    assignUserToInstallation(user, saveCallback);
                } else {
                    throw new RuntimeException("Failed to log in anonymously", e);
                }
            }
        });
    }

    @Deprecated public static void loginAs(final Context context, String username, String password, final SaveCallback saveCallback) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Private login performed. Will now assign this user (" + user.getUsername() + ") to the parse installation");
                    assignUserToInstallation(user, saveCallback);
                } else {
                    throw new RuntimeException("Failed to log in", e);
                }
            }
        });
    }

    private static void assignUserToInstallation(ParseUser user, SaveCallback saveCallback) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("user", user);
        installation.saveInBackground(saveCallback);
    }

    public static void changeUsename(ParseUser user, String newUsername, SaveCallback saveCallback) {
        user.put(DB.Column.USER_NAME, newUsername);
        user.saveInBackground(saveCallback);
    }

    public interface RoomFindCallback {
        void done(List<Room> rooms);
    }

    public static void getAllRooms(final RoomFindCallback allRoomsCallback) {
        try {
            final List<ParseObject> roomParseObjects = ParseQuery.getQuery(DB.Table.ROOM).find();
            allRoomsCallback.done(Room.fromParseObjectList(roomParseObjects));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static void getRoomsForUser(final RoomFindCallback roomsForUserCallback) {
        ParseUser.getCurrentUser()
                .fetchInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if (e == null) {
                            final List<ParseObject> roomParseObjects = parseObject.getList(DB.Column.USER_ROOMS);
                            try {
                                ParseObject.fetchAll(roomParseObjects);
                            } catch (ParseException e1) {
                                throw new RuntimeException(e1);
                            }
                            roomsForUserCallback.done(Room.fromParseObjectList(roomParseObjects));
                        } else {
                            throw new RuntimeException("Failed to retrieve users", e);
                        }
                    }
                });
    }

    // TODO: accept a "Room", and do the parse fetch here
    public static void joinRoom(ParseUser user, final ParseObject roomParsePointer, final SaveCallback saveCallback) {
        Log.d(TAG, "Add room " + roomParsePointer.getObjectId() + " to the user " + user.getUsername());
        user.fetchInBackground(new GetCallback<ParseObject>() { // TODO: can it be fetchIfNeededInBackground?
            @Override
            public void done(ParseObject userParseObject, ParseException e) {
                if (e == null) {
                    final List<Object> alreadyJoinedRooms = userParseObject.getList(DB.Column.USER_ROOMS);
                    if (alreadyJoinedRooms == null) {
                        throw new RuntimeException("No way, alreadyJoinedRooms is null. Is it not an empty array?");
                    }
                    Log.i(TAG, "This user, " + userParseObject.getString(DB.Column.USER_NAME) + ", already has " + alreadyJoinedRooms.size() + " rooms: adding this one");
                    userParseObject.addUnique(DB.Column.USER_ROOMS, roomParsePointer);
                    userParseObject.saveInBackground(saveCallback);
                } else {
                    throw new RuntimeException("Failed to get the user", e);
                }
            }
        });
    }

    public static void createHighscore(final ParseUser user, final String levelName, final int score, final SaveCallback saveCallback) {
        ParseQuery.getQuery(DB.Table.HIGHSCORE)
                .whereEqualTo(DB.Column.HIGHSCORE_LEVEL, levelName)
                .whereEqualTo(DB.Column.HIGHSCORE_USER, user)
                .findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> parseObjects, ParseException e) {
                        if (e == null) {
                            if (parseObjects.size() == 0) {
                                saveNewHighscore(user, levelName, score, saveCallback);
                            } else if (parseObjects.size() == 1) {
                                updateHighscore(score, parseObjects.get(0), saveCallback);
                            } else {
                                throw new RuntimeException("Oops, there's more than one highscore for this user in this level");
                            }
                        } else {
                            throw new RuntimeException("Failed to get highscores", e);
                        }
                    }
                });
    }

    private static void saveNewHighscore(ParseObject user, String levelName, int newScore, SaveCallback saveCallback) {
        ParseObject userScore = new ParseObject(DB.Table.HIGHSCORE);
        userScore.put("user", user);
        userScore.put("levelName", levelName);
        userScore.put("score", newScore);
        userScore.saveInBackground(saveCallback);
    }

    private static void updateHighscore(int newScore, ParseObject highscore, SaveCallback saveCallback) {
        final int highestSoFar = highscore.getInt(DB.Column.HIGHSCORE_SCORE);
        if (newScore > highestSoFar) {
            highscore.put(DB.Column.HIGHSCORE_SCORE, newScore);
            highscore.saveInBackground(saveCallback);
        } else {
            Log.i(TAG, "New score (" + newScore + "), but not the highest (" + highestSoFar + "). Ignored"); // TODO: could be 0 if called from admin interface
            saveCallback.done(null);
        }
    }
}
