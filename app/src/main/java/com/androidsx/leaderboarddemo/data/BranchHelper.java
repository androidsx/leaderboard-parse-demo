package com.androidsx.leaderboarddemo.data;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidsx.leaderboarddemo.R;
import com.androidsx.leaderboarddemo.ui.mock.HomeActivity;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;

/**
 * Created by ompemi on 6/26/15.
 */
public class BranchHelper {

    private static final String TAG = HomeActivity.class.getSimpleName();

    public static void showJoinRoomDialogFromInviteIfNeeded(final Activity context) {
        // For the future, when login we can make this to ssociate a userId with branchId:
        //      Branch.getInstance(getApplicationContext()).setIdentity("your user identity");
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
                        Log.v(TAG, "Opened link from branch -> channel: " + referringParams.optString("~channel", "") +
                                " , feature: " + referringParams.optString("~feature", "") +
                                " , tags: " + referringParams.optString("~tags", "") +
                                " , campaign: " + referringParams.optString("~campaign", "") +
                                " , stage: " + referringParams.optString("~stage", "") +
                                " , creation_source: " + referringParams.optString("~creation_source", "") +
                                " , referrer: " + referringParams.optString("+referrer", "") +
                                " , is_first_session: " + referringParams.optBoolean("+is_first_session", false) +
                                " , clicked_branch_link: " + referringParams.optBoolean("+clicked_branch_link", false));

                        Log.v(TAG, "All branch properties: " + referringParams.toString());
                        if (!username.equals("") && !roomName.equals("") && !roomId.equals("")) {
                            Log.i(TAG, "Opened join room link from branch -> username: " + username + " , room: " + roomName + " , roomId: " + roomId);

                            BranchHelper.showJoinRoomDialogFromInvite(context, username, roomName, roomId);
                        }
                    } else {
                        Log.d(TAG, "Branch link not opened: " + referringParams.toString());
                    }
                } else {
                    Log.e(TAG, error.getMessage());
                }
            }
        }, context.getIntent().getData(), context);
    }

    public static void generateBranchLink(final Context context, String username, String roomName, String roomId,
                                          Branch.BranchLinkCreateListener callback) {
        Branch branch = Branch.getInstance(context.getApplicationContext());

        JSONObject obj = new JSONObject();
        try {
            obj.put("username", username);
            obj.put("roomName", roomName);
            obj.put("roomId", roomId);
            branch.getShortUrl(obj, callback);
        } catch (JSONException e) {
            Log.e(TAG, "Could not generate branch link");
            callback.onLinkCreate("JSON error", new BranchError());
        }
    }

    private static void showJoinRoomDialogFromInvite(final Context context, String username, final String roomName, final String roomId) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_join_room_from_invite);

        final TextView inviteText = (TextView) dialog.findViewById(R.id.join_room_text);
        final Button dialogButton = (Button) dialog.findViewById(R.id.join_room_from_invite_button);
        final EditText usernameEditText = (EditText) dialog.findViewById(R.id.join_room_text_username);
        final TextView usernameText = (TextView) dialog.findViewById(R.id.join_room_text);

        // we do not need the username of the user if it is already a parse user
        if (ParseUser.getCurrentUser() != null) {
            usernameText.setText("(username input hidden)");
            usernameEditText.setVisibility(View.GONE);
        }

        inviteText.setText("\"" + username + "\" invited you to compete in the room \"" + roomName + "\". Let's beat him!");

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newUserUsername = usernameEditText.getText().toString();
                if (usernameEditText.getVisibility() == View.VISIBLE && newUserUsername.equals("")) {
                    Toast.makeText(context, "Input the username", Toast.LENGTH_LONG).show();
                    return;
                }

                if (ParseUser.getCurrentUser() != null) {
                    // join room
                    Toast.makeText(context, "Not implemented. We should join the existing user " + ParseUser.getCurrentUser().getUsername() + " to the room", Toast.LENGTH_LONG).show();
                } else {
                    // create user + join room
                    Toast.makeText(context, "Not implemented. We should create the user and join to the room", Toast.LENGTH_LONG).show();
                }

                dialog.cancel();
            }
        });

        dialog.show();
    }
}
