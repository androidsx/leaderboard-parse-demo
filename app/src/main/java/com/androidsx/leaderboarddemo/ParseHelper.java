package com.androidsx.leaderboarddemo;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParseHelper {

    public static <T> ArrayList<T> toListNoDuplicates(List<ParseObject> parseObjects, String field) {
        if (parseObjects == null) {
            parseObjects = Collections.emptyList();
        }
        final Set<T> set = new HashSet<>();
        for (ParseObject parseObject : parseObjects) {
            set.add((T) parseObject.get(field));
        }
        return new ArrayList<>(set);
    }

    public static <T> List<T> toListKeepOrder(List<ParseObject> parseObjects, String field) {
        if (parseObjects == null) {
            parseObjects = Collections.emptyList();
        }
        final List<T> list = new ArrayList<>();
        for (ParseObject parseObject : parseObjects) {
            list.add((T) parseObject.get(field));
        }
        return list;
    }

    static class ToastAndFinishSaveCallback implements SaveCallback {
        private final Activity activity;

        public ToastAndFinishSaveCallback(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void done(ParseException e) {
            if (e == null) {
                Toast.makeText(activity, "Success", Toast.LENGTH_SHORT).show();
                activity.finish();
            } else {
                throw new RuntimeException("Fail", e);
            }
        }
    }

    static class ToastSaveCallback implements SaveCallback {
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

    static class ToastLogOutCallback implements LogOutCallback {
        private final Context context;

        public ToastLogOutCallback(Context context) {
            this.context = context;
        }

        @Override
        public void done(ParseException e) {
            if (e == null) {
                Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show();
            } else {
                throw new RuntimeException("Fail", e);
            }
        }
    }
}
