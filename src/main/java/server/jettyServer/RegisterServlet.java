package server.jettyServer;

import hotelapp.DatabaseHandler;
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

public class RegisterServlet extends HttpServlet {
  private final DatabaseHandler databaseHandler = DatabaseHandler.getInstance();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");
    response.setStatus(HttpServletResponse.SC_OK);
    if (request.getSession().getAttribute("username") != null) {
      response.sendRedirect("/welcome");
    } else {
      PrintWriter out = response.getWriter();
      Boolean error = (Boolean) request.getSession().getAttribute("registration_error");
      if (error == null) {
        error = false;
      }
      VelocityEngine ve =
          (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
      VelocityContext context = new VelocityContext();
      Template template = ve.getTemplate("src/main/templates/register.html");
      context.put("error", error);

      StringWriter writer = new StringWriter();
      template.merge(context, writer);
      out.println(writer);
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String username = request.getParameter("username");
    username = StringEscapeUtils.escapeHtml4(username);
    String password = request.getParameter("password");
    password = StringEscapeUtils.escapeHtml4(password);
    if (databaseHandler.registerUser(username, password)) {
      request.getSession().setAttribute("username", username);
      request.getSession().setAttribute("registration_error", false);
      response.sendRedirect("/welcome");
    } else {
      request.getSession().setAttribute("registration_error", true);
      response.sendRedirect("/register");
    }
  }
}
