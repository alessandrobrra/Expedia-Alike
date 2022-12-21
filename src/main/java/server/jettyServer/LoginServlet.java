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
import java.sql.Date;
import java.text.SimpleDateFormat;

public class LoginServlet extends HttpServlet {
  private final DatabaseHandler databaseHandler = DatabaseHandler.getInstance();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");
    response.setStatus(HttpServletResponse.SC_OK);
    if (request.getSession().getAttribute("username") != null) {
      response.sendRedirect("/welcome");
    } else {
      PrintWriter out = response.getWriter();
      Boolean error = (Boolean) request.getSession().getAttribute("error");
      if (error == null) {
        error = false;
      }
      VelocityEngine ve =
          (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
      VelocityContext context = new VelocityContext();
      Template template = ve.getTemplate("src/main/templates/login.html");
      context.put("error", error);
      if (request.getParameter("loginError") != null) {
        context.put("loginError", true);
      } else {
        context.put("add-review-error", false);
      }

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

    if (databaseHandler.checkUser(username, password)) {
      request.getSession().setAttribute("username", username);
      request.getSession().setAttribute("error", false);
      SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy 'at' HH:mm:ss z");
      Date date = new Date(System.currentTimeMillis());
      String loginTime = formatter.format(date);
      request.getSession().setAttribute("loginTime", loginTime);
      response.sendRedirect("/welcome");
    } else {
      request.getSession().setAttribute("error", true);
      response.sendRedirect("/login");
    }
  }
}
