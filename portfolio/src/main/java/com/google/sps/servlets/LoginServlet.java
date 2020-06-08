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

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    response.setContentType("text/html;");
    PrintWriter out = response.getWriter();

    UserService userService = UserServiceFactory.getUserService();
     // Only logged-in users can see the form
    if (userService.isUserLoggedIn()) {
      out.println("<p>Hello " + userService.getCurrentUser().getEmail() + "!</p>");
      String logoutUrl = userService.createLogoutURL("/login");
      out.println("<p>Logout <a href=\"" + logoutUrl + "\">here</a>.</p>");
      out.println("<p>Type a message and click submit:</p>");
      out.println("<form method=\"POST\" action=\"/login\">");
      out.println("<textarea name=\"text\"></textarea>");
      out.println("<br/>");
      out.println("<button>Submit</button>");
      out.println("</form>");
    } else {
      String loginUrl = userService.createLoginURL("/login");
      out.println("<p>Login <a href=\"" + loginUrl + "\">here</a>.</p>");
      return;
    }

    // Everybody can see the messages
    out.println("<ul>");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("Message").addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
      String text = (String) entity.getProperty("text");
      String email = (String) entity.getProperty("email");
      out.println("<li>" + email + ": " + text + "</li>");
    }
    out.println("</ul>");

  }

 @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    UserService userService = UserServiceFactory.getUserService();

    // Only logged-in users can post messages
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/login");
      return;
    }

    String text = request.getParameter("text");
    String email = userService.getCurrentUser().getEmail();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity messageEntity = new Entity("Message");
    messageEntity.setProperty("text", text);
    messageEntity.setProperty("email", email);
    messageEntity.setProperty("timestamp", System.currentTimeMillis());
    datastore.put(messageEntity);

    response.sendRedirect("/login");
   
  }
}