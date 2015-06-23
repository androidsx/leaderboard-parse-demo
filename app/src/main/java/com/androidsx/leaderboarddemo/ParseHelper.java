package com.androidsx.leaderboarddemo;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParseHelper {
    public static ArrayList<String> toListNoDuplicates(List<ParseObject> parseObjects, String field) {
        final Set<String> levelNames = new HashSet<>();
        for (ParseObject parseObject : parseObjects) {
            levelNames.add((String) parseObject.get(field));
        }
        return new ArrayList<>(levelNames);
    }
}
