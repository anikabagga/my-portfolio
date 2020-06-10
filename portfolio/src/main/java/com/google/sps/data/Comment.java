package com.google.sps.data;

public final class Comment {

    private final String name;
    private final String comment;
    private final long timestamp;
    private final String mood;
    private final long id;
    private final String email;
    private final String imageURL;

    public Comment(String name, String comment, long timestamp, String mood, long id, String email, String imageURL) {
        this.comment = comment;
        this.timestamp = timestamp;
        this.name = name;
        this.mood = mood;
        this.id = id;
        this.email = email;
        this.imageURL = imageURL;
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

    public String getEmail() {
        return email;
    }

    public String getImageURL() {
        return imageURL;
    }
}