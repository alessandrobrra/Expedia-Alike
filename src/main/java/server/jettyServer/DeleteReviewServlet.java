package server.jettyServer;

import hotelapp.Review;
import hotelapp.ThreadSafeHotelAndReviewFinder;
import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class DeleteReviewServlet extends HttpServlet {
  private final ThreadSafeHotelAndReviewFinder hotelAndReviewFinder;

  public DeleteReviewServlet(ThreadSafeHotelAndReviewFinder hotelAndReviewFinder) {
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
      hotelAndReviewFinder.deleteReview(review);
      response.sendRedirect("/hotel-detail?hotelId=" + hotelId);
    } else {
      response.sendRedirect("/hotel-detail?hotelId=" + hotelId + "&editReviewError=true");
    }
  }
}
