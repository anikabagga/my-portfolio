// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    String quantityChosen = request.getParameter("number");
    String commentOrder = request.getParameter("order");
    int amount;
    try {
        amount = Integer.parseInt(quantityChosen);
    } catch (NumberFormatException e) {
        amount = 1;
    }

    Query query;
    if (commentOrder != null && commentOrder.equals("newest")){
        query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    } else {
        query = new Query("Comment").addSort("timestamp", SortDirection.ASCENDING);
    }
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Comment> comments = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
        
      if (amount == 0) {
          break;
      }
      String comment = (String) entity.getProperty("comment");
      long timestamp = (long) entity.getProperty("timestamp");
      String name = (String) entity.getProperty("name");
      String mood = (String) entity.getProperty("mood");
      long id = entity.getKey().getId();

      Comment userComment = new Comment(name, comment, timestamp, mood, id);
      comments.add(userComment);
      amount -= 1;
    }
    //Convert to json
    response.setContentType("application/json;");
    response.getWriter().println(new Gson().toJson(comments));
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    //Receives submitted comment 
    String comment = request.getParameter("text-input");
    String name = request.getParameter("name-input");
    String mood = request.getParameter("mood");
    long timestamp = System.currentTimeMillis();

    //Creates entity with submitted data
    Entity taskEntity = new Entity("Comment");
    taskEntity.setProperty("comment", comment);
    taskEntity.setProperty("timestamp", timestamp);
    taskEntity.setProperty("name", name);
    taskEntity.setProperty("mood", mood);

    //Adds entity to database 
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(taskEntity);

    //Redirect back to the HTML page.
    response.sendRedirect("/index.html#comments-section");
  }
}
