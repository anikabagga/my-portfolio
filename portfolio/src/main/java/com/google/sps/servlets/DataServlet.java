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
public class DataServlet extends HttpServlet {

  private UserService userService = UserServiceFactory.getUserService();
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
    if (commentOrder != null && commentOrder.equals("newest")) {
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
      String email = (String) entity.getProperty("email");
      long id = entity.getKey().getId();
      String imgURL = "";

      Comment userComment = new Comment(name, comment, timestamp, mood, id, email, imgURL);
      comments.add(userComment);
      amount -= 1;
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

  
  /** Returns a URL that points to the uploaded file, or null if the user didn't upload a file. */
  private String getUploadedFileUrl(HttpServletRequest request, String formInputElementName) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get(formInputElementName);

    // User submitted form without selecting a file, so we can't get a URL. (dev server)
    if (blobKeys == null || blobKeys.isEmpty()) {
      return null;
    }

    // Our form only contains a single file input, so get the first index.
    BlobKey blobKey = blobKeys.get(0);

    // User submitted form without selecting a file, so we can't get a URL. (live server)
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return null;
    }

    // We could check the validity of the file here, e.g. to make sure it's an image file
    // https://stackoverflow.com/q/10779564/873165

    // Use ImagesService to get a URL that points to the uploaded file.
    ImagesService imagesService = ImagesServiceFactory.getImagesService();
    ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);

    // To support running in Google Cloud Shell with AppEngine's dev server, we must use the relative
    // path to the image, rather than the path returned by imagesService which contains a host.
    try {
      URL url = new URL(imagesService.getServingUrl(options));
      return url.getPath();
    } catch (MalformedURLException e) {
      return imagesService.getServingUrl(options);
    }
  }



}
