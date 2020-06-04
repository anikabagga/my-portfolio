package com.google.sps.data;

/** An item on a todo list. */
public final class Comment {

  private final String name;
  private final String comment;
  private final long timestamp;
  private final String mood;

  public Comment(String name, String comment, long timestamp, String mood) {
    this.comment = comment;
    this.timestamp = timestamp;
    this.name = name;
    this.mood = mood;
  }
}