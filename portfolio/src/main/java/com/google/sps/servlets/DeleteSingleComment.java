package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/delete-single-comment")
public class DeleteSingleComment extends HttpServlet {

  private UserService userService = UserServiceFactory.getUserService();
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long id = Long.parseLong(request.getParameter("id"));
    String commentEmail = request.getParameter("email");
    String userEmail = (String) userService.getCurrentUser().getEmail();
    System.out.println(commentEmail);
    System.out.println(userEmail);

    if (commentEmail.equals(userEmail)) {
      System.out.println("yay! you are allowed to delete your comment");
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Key deleteEntry = KeyFactory.createKey("Comment", id);
      datastore.delete(deleteEntry);
    } else {
      System.out.println("you are not allowed to delete this comment");
    }
  }
}