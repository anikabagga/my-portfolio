package com.google.sps.data;

/** An item on a todo list. */
public final class Comment {

  private final String comment;
  private final long timestamp;

  public Comment(String comment, long timestamp) {
    this.comment = comment;
    this.timestamp = timestamp;
  }
}