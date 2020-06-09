package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

  private UserService userService = UserServiceFactory.getUserService();
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    response.setContentType("application/json");
    String json = "{";
    String loginURL = userService.createLoginURL("/");
    String logoutURL = userService.createLogoutURL("/");

    // Sends json formatted url and user email 
    if (userService.isUserLoggedIn()) {
      json += "\"url\":" + "\"" + logoutURL + "\",";
      json += "\"email\":" + "\"" + userService.getCurrentUser().getEmail() + "\""; 
    } else {
      json += "\"url\":" + "\"" + loginURL + "\",";
      json += "\"email\":null";
    }
    json += "}";
    response.getWriter().println(json);
  }
}