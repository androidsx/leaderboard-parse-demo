package com.androidsx.leaderboarddemo;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParseHelper {
    public static <T> ArrayList<T> toListNoDuplicates(List<ParseObject> parseObjects, String field) {
        final Set<T> set = new HashSet<>();
        for (ParseObject parseObject : parseObjects) {
            set.add((T) parseObject.get(field));
        }
        return new ArrayList<>(set);
    }

    /*public static ArrayList<String> toListNoDuplicates(List<ParseObject> parseObjects, String field1, String field2) {
        final Set<String> levelNames = new HashSet<>();
        for (ParseObject parseObject : parseObjects) {
            levelNames.add((T) parseObject.get(field));
        }
        return new ArrayList<>(levelNames);
    }*/

}
