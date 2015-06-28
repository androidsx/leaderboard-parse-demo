package com.androidsx.leaderboarddemo.model;

public class Level {
    private String name;

    /** Arg-less constructor for GSON. */
    public Level() {
    }

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
