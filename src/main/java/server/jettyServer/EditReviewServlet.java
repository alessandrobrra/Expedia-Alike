package server.jettyServer;

import hotelapp.DatabaseHandler;
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

public class EditReviewServlet extends HttpServlet {
  private final ThreadSafeHotelAndReviewFinder hotelAndReviewFinder;

  public EditReviewServlet(ThreadSafeHotelAndReviewFinder hotelAndReviewFinder) {
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
    }
    hotelId = StringEscapeUtils.escapeHtml4(hotelId);
    String reviewId = request.getParameter("reviewId");
    reviewId = StringEscapeUtils.escapeHtml4(reviewId);
    Review review = hotelAndReviewFinder.findReview(hotelId, reviewId);
    if (review.getReviewUsername().equals(name)) {
      VelocityEngine ve =
          (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
      VelocityContext context = new VelocityContext();
      Template template = ve.getTemplate("src/main/templates/edit-review.html");
      context.put("reviewTitle", review.getReviewTitle());
      context.put("reviewText", review.getReviewText());
      context.put("reviewId", reviewId);
      context.put("hotelId", hotelId);

      StringWriter writer = new StringWriter();
      template.merge(context, writer);

      out.println(writer);
    } else {
      response.sendRedirect("/hotel-detail?hotelId=" + hotelId + "&editReviewError=true");
    }
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
    String reviewId = request.getParameter("reviewId");
    reviewId = StringEscapeUtils.escapeHtml4(reviewId);

    Review review = hotelAndReviewFinder.findReview(hotelId, reviewId);
    review.updateTitle(reviewTitle);
    review.updateReviewText(reviewText);
    review.updateRating(Integer.parseInt(reviewRating));
    DatabaseHandler.getInstance().updateReview(review);

    response.sendRedirect("hotel-detail?hotelId=" + hotelId);
  }
}
