package server.jettyServer;

import hotelapp.DatabaseHandler;
import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LogoutServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    HttpSession session = request.getSession();
    DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
    String username = (String) session.getAttribute("username");
    if (username != null) {
      username = StringEscapeUtils.escapeHtml4(username);
      String lastLogin = request.getSession().getAttribute("loginTime").toString();
      lastLogin = StringEscapeUtils.escapeHtml4(lastLogin);
      databaseHandler.updateLogin(username, lastLogin);
    } else {
      response.sendRedirect("/login?loginError=true");
    }
    session.invalidate();
    response.sendRedirect("/welcome");
  }
}
