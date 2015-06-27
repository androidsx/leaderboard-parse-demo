package com.androidsx.leaderboarddemo.data;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidsx.leaderboarddemo.R;
import com.androidsx.leaderboarddemo.model.Room;
import com.androidsx.leaderboarddemo.ui.mock.HomeActivity;
import com.androidsx.leaderboarddemo.ui.mock.NewRoomActivity;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONObject;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;

public class BranchHelper {

    private static final String TAG = HomeActivity.class.getSimpleName();

    public static void showJoinRoomDialogIfDeeplink(final Activity context) {
        // For the future, after login we can make this to associate a userId with branchId:
        //      Branch.getInstance(getApplicationContext()).setIdentity(ParseUser.getCurrentUser().id);
        Branch branch = Branch.getInstance(context.getApplicationContext());
        branch.initSession(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                if (error == null) {
                    // params are the deep linked params associated with the link that the user clicked -> was re-directed to this app
                    // params will be empty if no data found
                    String username = referringParams.optString("username", "");
                    String roomName = referringParams.optString("roomName", "");
                    String roomId = referringParams.optString("roomId", "");
                    if (referringParams.length() != 0 && referringParams.optBoolean("+clicked_branch_link", false)) {
                        // https://dev.branch.io/recipes/quickstart_guide/android/#branch-provided-data-parameters-in-initsession-callback
                        Log.d(TAG, "Opened link from branch: " + referringParams.toString());
                        if (!username.equals("") && !roomName.equals("") && !roomId.equals("")) {
                            Log.i(TAG, "Opened join room link from branch -> username: " + username + " , room: " + roomName + " , roomId: " + roomId);

                            BranchHelper.showJoinRoomDialog(context, username, roomName, roomId);
                        }
                    } else {
                        Log.d(TAG, "Branch link not opened: " + referringParams.toString());
                    }
                } else {
                    throw new RuntimeException(error.getMessage());
                }
            }
        }, context.getIntent().getData(), context);
    }

    public static void generateBranchLink(final Context context, String username, final String roomName, String roomId) {
        try {
            Branch branch = Branch.getInstance(context.getApplicationContext());

            JSONObject obj = new JSONObject();
            obj.put("username", username);
            obj.put("roomName", roomName);
            obj.put("roomId", roomId);

            branch.getShortUrl(obj, new Branch.BranchLinkCreateListener() {

                @Override
                public void onLinkCreate(String url, BranchError branchError) {
                    if (branchError == null) {
                        String shareBody = "Compete against me in my game room \"" + roomName + "\" of Pencil Gravity: " + url;

                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

                        context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
                    } else {
                        Log.e("MainActivity", "Error while creating the link after the callback: " + branchError);
                        Toast.makeText(context, "Error while creating the link: " + branchError, Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Could not generate branch link", e);
            Toast.makeText(context, "Error while creating the link: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private static void showJoinRoomDialog(final Context context, String username, final String roomName, final String roomId) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_join_room_from_invite);

        final TextView inviteText = (TextView) dialog.findViewById(R.id.join_room_text);
        final Button dialogButton = (Button) dialog.findViewById(R.id.join_room_from_invite_button);
        final EditText usernameEditText = (EditText) dialog.findViewById(R.id.join_room_text_username);
        final TextView usernameText = (TextView) dialog.findViewById(R.id.username_text);

        usernameText.setVisibility(ParseUser.getCurrentUser() == null ? View.VISIBLE : View.GONE);
        usernameEditText.setVisibility(ParseUser.getCurrentUser() == null ? View.VISIBLE : View.GONE);

        inviteText.setText("\"" + username + "\" invited you to compete in the room \"" + roomName + "\". Let's beat him!");

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String newUserUsername = usernameEditText.getText().toString();
                if (usernameEditText.getVisibility() == View.VISIBLE && newUserUsername.equals("")) {
                    Toast.makeText(context, "Input the username", Toast.LENGTH_LONG).show();
                    return;
                }

                if (ParseUser.getCurrentUser() == null) {
                    Log.i(TAG, "No Parse user exists. Will login now, then change username and then join room");
                    ParseDao.anonymousLogin(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(context, "Created user as it didn't exist", Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "Logged in. Now let's update the username and also create the room");
                                final ParseUser currentUser = ParseUser.getCurrentUser();
                                final String newNickname = newUserUsername;
                                ParseDao.changeUsename(currentUser, newNickname, new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Toast.makeText(context, "Username changed to " + newNickname, Toast.LENGTH_SHORT).show();
                                            ParseDao.joinRoom(ParseUser.getCurrentUser(), ParseObject.createWithoutData(DB.Table.ROOM, roomId), new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e == null) {
                                                        Toast.makeText(context, "Joined room " + roomName + " and set as default", Toast.LENGTH_SHORT).show();
                                                        Log.i(TAG, "Joined room");

                                                        // Setup this new room as default
                                                        GlobalState.activeRoom = new Room(roomId, roomName);

                                                        dialog.cancel();
                                                    } else {
                                                        throw new RuntimeException("Failed to join room", e);
                                                    }
                                                }
                                            });
                                        } else {
                                            throw new RuntimeException("Failed to update username", e);
                                        }
                                    }
                                });
                            } else {
                                throw new RuntimeException("Failed to log in", e);
                            }
                        }
                    });
                } else {
                    Log.i(TAG, "A Parse user exists already (" + ParseUser.getCurrentUser().getUsername() + ", let's join room");
                    ParseDao.joinRoom(ParseUser.getCurrentUser(), ParseObject.createWithoutData(DB.Table.ROOM, roomId), new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(context, "Joined room " + roomName + " and set as default", Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "Joined room. We do not send any highscore (if any). As it may repeat the push with same highscore to other rooms of this user");

                                // Setup this new room as default
                                GlobalState.activeRoom = new Room(roomId, roomName);

                                dialog.cancel();
                            } else {
                                throw new RuntimeException("Failed to join room", e);
                            }
                        }
                    });
                }
            }
        });

        dialog.show();
    }
}
