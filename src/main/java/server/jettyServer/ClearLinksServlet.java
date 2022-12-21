package server.jettyServer;

import hotelapp.DatabaseHandler;
import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ClearLinksServlet extends HttpServlet {
  DatabaseHandler databaseHandler = DatabaseHandler.getInstance();

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
    if (databaseHandler.clearExpediaLinks(name)) {
      response.sendRedirect("/user-profile");
    } else {
      response.sendRedirect("/login?clearLinksError=true");
    }
  }
}
