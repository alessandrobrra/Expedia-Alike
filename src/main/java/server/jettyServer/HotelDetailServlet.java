package server.jettyServer;

import hotelapp.DatabaseHandler;
import hotelapp.Hotel;
import hotelapp.Review;
import hotelapp.ThreadSafeHotelAndReviewFinder;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class HotelDetailServlet extends HttpServlet {
  private final ThreadSafeHotelAndReviewFinder hotelAndReviewFinder;

  public HotelDetailServlet(ThreadSafeHotelAndReviewFinder hotelAndReviewFinder) {
    this.hotelAndReviewFinder = hotelAndReviewFinder;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");
    response.setStatus(HttpServletResponse.SC_OK);
    PrintWriter out = response.getWriter();
    String name = (String) request.getSession().getAttribute("username");
    if (name == null || name.isEmpty()) {
      response.sendRedirect("/login?loginError=true");
    }
    name = StringEscapeUtils.escapeHtml4(name);
    String hotelId = request.getParameter("hotelId");
    if (hotelId == null || hotelId.isEmpty() || hotelAndReviewFinder.findHotel(hotelId) == null) {
      response.sendRedirect("/hotel-search?invalid=true");
      return;
    }
    hotelId = StringEscapeUtils.escapeHtml4(hotelId);
    Hotel hotel = hotelAndReviewFinder.findHotel(hotelId);
    List<Review> hotelReviews = hotelAndReviewFinder.findReviews(hotelId);
    if (hotelReviews == null) {
      hotelReviews = new ArrayList<>();
    }
    hotelReviews.sort(new Review.ReviewDateComparator().reversed());
    double rating = 0;
    for (Review review : hotelReviews) {
      rating += review.getRating();
    }
    if (hotelReviews.size() != 0) {
      rating /= (double) hotelReviews.size();
    }
    rating = Math.round(rating * 10.0) / 10.0;
    System.out.println(hotelReviews.size());

    VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
    Template template = ve.getTemplate("src/main/templates/hotel-detail.html");
    VelocityContext context = new VelocityContext();
    context.put("username", name);
    context.put("hotel", hotel);
    context.put("reviews", hotelReviews);
    context.put("rating", rating);
    if (request.getParameter("editReviewError") != null) {
      context.put("edit-review-error", true);
    } else {
      context.put("edit-review-error", false);
    }
    StringWriter writer = new StringWriter();
    template.merge(context, writer);
    out.println(writer);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    String name = (String) request.getSession().getAttribute("username");
    if (name == null || name.isEmpty()) {
      response.sendRedirect("/welcome");
      return;
    }
    name = StringEscapeUtils.escapeHtml4(name);
    String link = (String) request.getParameter("expedia");
    link = StringEscapeUtils.escapeHtml4(link);
    String visited = (String) request.getParameter("isVisited");
    visited = StringEscapeUtils.escapeHtml4(visited);
    String hotelName = (String) request.getParameter("hotelName");
    hotelName = StringEscapeUtils.escapeHtml4(hotelName);

    if (visited != null) {
      DatabaseHandler db = DatabaseHandler.getInstance();
      if (db.addExpediaLink(name, hotelName, link)) {
        response.sendRedirect("/hotel-detail?hotelId=" + request.getParameter("hotelId"));
      }
    }
  }
}
