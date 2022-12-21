package server.jettyServer;

import hotelapp.Review;
import hotelapp.ThreadSafeHotelAndReviewFinder;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.UUID;

public class AddReviewServlet extends HttpServlet {
  private final ThreadSafeHotelAndReviewFinder hotelAndReviewFinder;

  public AddReviewServlet(ThreadSafeHotelAndReviewFinder hotelAndReviewFinder) {
    this.hotelAndReviewFinder = hotelAndReviewFinder;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    HttpSession session = request.getSession();
    response.setContentType("text/html");
    response.setStatus(HttpServletResponse.SC_OK);
    PrintWriter out = response.getWriter();
    String name = (String) session.getAttribute("username");
    if (name == null || name.isEmpty()) {
      response.sendRedirect("/login?loginError=true");
    }
    name = StringEscapeUtils.escapeHtml4(name);

    String hotelId = request.getParameter("hotelId");
    if (hotelId == null || hotelId.isEmpty()) {
      response.sendRedirect("/hotel-search?invalid=true");
      return;
    }
    hotelId = StringEscapeUtils.escapeHtml4(hotelId);

    VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
    VelocityContext context = new VelocityContext();
    Template template = ve.getTemplate("src/main/templates/add-review.html");
    context.put("username", name);
    context.put("hotelId", hotelId);

    StringWriter writer = new StringWriter();
    template.merge(context, writer);

    out.println(writer);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String reviewTitle = request.getParameter("reviewTitle");
    reviewTitle = StringEscapeUtils.escapeHtml4(reviewTitle);
    String hotelId = request.getParameter("hotelId");
    hotelId = StringEscapeUtils.escapeHtml4(hotelId);
    String reviewText = request.getParameter("reviewText");
    reviewText = StringEscapeUtils.escapeHtml4(reviewText);
    String reviewRating = request.getParameter("reviewRating");
    reviewRating = StringEscapeUtils.escapeHtml4(reviewRating);
    String reviewUsername = request.getSession().getAttribute("username").toString();
    reviewUsername = StringEscapeUtils.escapeHtml4(reviewUsername);
    UUID reviewId = UUID.randomUUID();
    LocalDate reviewDate = LocalDate.now();
    Review review =
        new Review(
            hotelId,
            reviewId.toString(),
            Integer.parseInt(reviewRating),
            reviewTitle,
            reviewText,
            reviewUsername,
            reviewDate);
    hotelAndReviewFinder.addNewReview(review);
    response.sendRedirect("hotel-detail?hotelId=" + hotelId);
  }
}
