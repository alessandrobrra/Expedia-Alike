package hotelapp;

import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import server.jettyServer.*;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HotelServer {

  public static final int PORT = 8080;
  public static final String HOTEL_FILE = "input/hotels/hotels.json";
  public static final String REVIEW_FILE = "input/reviewsTiny";
  public static final String STOP_WORD_FILE = "input/stop_words.txt";
  public static final int NUM_THREADS = 4;

  public static void main(String[] args) throws Exception {
    // build the hotels in the maps
    ThreadSafeHotelAndReviewFinder hotelAndReviewFinder = new ThreadSafeHotelAndReviewFinder();
    GsonParser gsonParser = new MultithreadedGsonParser(hotelAndReviewFinder, NUM_THREADS);
    InvertedIndex invertedIndex = new ThreadSafeInvertedIndex();
    InvertedIndexBuilder invertedIndexBuilder = new ThreadSafeInvertedIndexBuilder(invertedIndex);

    try {
      gsonParser.parseHotel(HOTEL_FILE);
      invertedIndexBuilder.populateStopWords(STOP_WORD_FILE);
      gsonParser.parseReviewDirectory(Path.of(REVIEW_FILE));
      ((MultithreadedGsonParser) gsonParser).shutdown();
      hotelAndReviewFinder.setInvertedIndex(invertedIndex);
    } catch (FileNotFoundException e) {
      System.out.println(e.getMessage());
      return;
    }

    // start the server
    DatabaseHandler dbHandler = DatabaseHandler.getInstance();
    dbHandler.createTable();
    Server server = new Server(PORT);
    String path = Paths.get("DOM").toString();
    ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
    handler.setContextPath("/");
    handler.setResourceBase(path);
    handler.addServlet(WelcomePageServlet.class, "/welcome");
    handler.addServlet(RegisterServlet.class, "/register");
    handler.addServlet(LogoutServlet.class, "/logout");
    handler.addServlet(LoginServlet.class, "/login");
    handler.addServlet(HotelSearchServlet.class, "/hotel-search");
    handler.addServlet(UserProfileServlet.class, "/user-profile");
    handler.addServlet(ClearLinksServlet.class, "/clear-links");
    handler.addServlet(
        new ServletHolder(new HotelResultServlet(hotelAndReviewFinder)), "/hotel-result");
    handler.addServlet(
        new ServletHolder(new HotelDetailServlet(hotelAndReviewFinder)), "/hotel-detail");
    handler.addServlet(
        new ServletHolder(new AddReviewServlet(hotelAndReviewFinder)), "/add-review");
    handler.addServlet(
        new ServletHolder(new EditReviewServlet(hotelAndReviewFinder)), "/edit-review");
    handler.addServlet(
        new ServletHolder(new DeleteReviewServlet(hotelAndReviewFinder)), "/delete-review");
    ServletHolder holder = new ServletHolder("default", DefaultServlet.class);
    holder.setInitParameter("dirAllowed", "true");
    holder.setInitParameter("resourceBase", "./DOM");
    handler.addServlet(holder, "/");

    VelocityEngine velocityEngine = new VelocityEngine();
    velocityEngine.init();
    handler.setAttribute("templateEngine", velocityEngine);
    server.setHandler(handler);

    try {
      server.start();
      server.join();
    } catch (Exception e) {
      System.out.println(e);
    }
  }
}
