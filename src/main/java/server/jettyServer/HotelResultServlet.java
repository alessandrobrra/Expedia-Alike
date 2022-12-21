package server.jettyServer;

import hotelapp.Hotel;
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
import java.util.Comparator;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

public class HotelResultServlet extends HttpServlet {
  private final ThreadSafeHotelAndReviewFinder hotelAndReviewFinder;

  public HotelResultServlet(ThreadSafeHotelAndReviewFinder hotelAndReviewFinder) {
    this.hotelAndReviewFinder = hotelAndReviewFinder;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");
    response.setStatus(HttpServletResponse.SC_OK);
    String query = request.getParameter("query");
    if (query == null || query.isEmpty()) {
      query = "";
    }
    query = StringEscapeUtils.escapeHtml4(query);
    Set<Hotel> result;
    if (query.isEmpty()) {
      result = hotelAndReviewFinder.getHotels();
    } else {
      result = hotelListHelper(query);
    }
    PrintWriter out = response.getWriter();
    String name = (String) request.getSession().getAttribute("username");
    if (name == null || name.isEmpty()) {
      response.sendRedirect("/login?loginError=true");
    }
    name = StringEscapeUtils.escapeHtml4(name);
    VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
    VelocityContext context = new VelocityContext();
    Template template = ve.getTemplate("src/main/templates/hotel-result.html");
    context.put("hotels", result);
    context.put("username", name);
    StringWriter writer = new StringWriter();
    template.merge(context, writer);
    out.println(writer);
  }

  /**
   * Helper method to get the set of hotels that match the query.
   *
   * @param query the query to match
   * @return the list of hotels that match the query
   */
  public Set<Hotel> hotelListHelper(String query) {
    Set<Hotel> hotels = hotelAndReviewFinder.getHotels();
    Set<Hotel> result = new TreeSet<>(Comparator.comparing(Hotel::getHotelName));
    for (Hotel hotel : hotels) {
      if (hotel.getHotelName().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT))) {
        result.add(hotel);
      }
    }
    return result;
  }
}
