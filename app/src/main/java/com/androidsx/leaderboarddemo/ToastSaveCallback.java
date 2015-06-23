package com.androidsx.leaderboarddemo;

import android.content.Context;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.SaveCallback;

/**
 * The simplest save callback.
 */
class ToastSaveCallback implements SaveCallback {
    private final Context context;

    public ToastSaveCallback(Context context) {
        this.context = context;
    }

    @Override
    public void done(ParseException e) {
        if (e == null) {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
        } else {
            throw new RuntimeException("Fail", e);
        }
    }
}
