package com.google.sps.data;

/** An item on a todo list. */
public final class Comment {

    private final String name;
    private final String comment;
    private final long timestamp;
    private final String mood;
    private final long id;

    public Comment(String name, String comment, long timestamp, String mood, long id) {
        this.comment = comment;
        this.timestamp = timestamp;
        this.name = name;
        this.mood = mood;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public String getMood() {
        return mood;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getId() {
        return id;
    }
}