package com.androidsx.leaderboarddemo.model;

public class Level {
    private final String name;

    public Level(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Level level = (Level) o;

        return name.equals(level.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
