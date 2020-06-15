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
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/** Servlet that returns comment data */
@WebServlet("/data")
public class CommentDataServlet extends HttpServlet {

  private UserService userService = UserServiceFactory.getUserService();
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    String commentOrder = request.getParameter("order");
    
    Query query;
    boolean isDescending = commentOrder != null && commentOrder.equals("newest");
    query = new Query("Comment").addSort("timestamp", isDescending ? SortDirection.DESCENDING : SortDirection.ASCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Comment> comments = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      
      String comment = (String) entity.getProperty("comment");
      long timestamp = (long) entity.getProperty("timestamp");
      String name = (String) entity.getProperty("name");
      String mood = (String) entity.getProperty("mood");
      String email = (String) entity.getProperty("email");
      long id = entity.getKey().getId();
      String imgURL = (String) entity.getProperty("imageURL");
      Comment userComment = new Comment(name, comment, timestamp, mood, id, email, imgURL);
      comments.add(userComment);
    }
    // Convert to json
    response.setContentType("application/json;");
    response.getWriter().println(new Gson().toJson(comments));
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // Receives submitted comment 
    String comment = request.getParameter("text-input");
    String name = request.getParameter("name-input");
    String mood = request.getParameter("mood");
    String email = userService.getCurrentUser().getEmail();
    String imageURL = getUploadedFileUrl(request, "image");
    
    long timestamp = System.currentTimeMillis();

    // Creates entity with submitted data
    Entity taskEntity = new Entity("Comment");
    taskEntity.setProperty("comment", comment);
    taskEntity.setProperty("timestamp", timestamp);
    taskEntity.setProperty("name", name);
    taskEntity.setProperty("mood", mood);
    taskEntity.setProperty("email", email);
    taskEntity.setProperty("imageURL", imageURL);

    // Adds entity to database 
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(taskEntity);

    // Redirect back to the HTML page.
    response.sendRedirect("/index.html#comments-section");
  }
  
  /** Returns a key that points to the uploaded file, or null if the user didn't upload a file. */
  private String getUploadedFileUrl(HttpServletRequest request, String formInputElementName) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get("image");

    String blobKey;
    if (blobKeys == null || blobKeys.isEmpty()) {
      blobKey = null;
    } else {
      blobKey = blobKeys.get(0).getKeyString();
    }
    return blobKey;
  }

}
