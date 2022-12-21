package server.jettyServer;

import hotelapp.DatabaseHandler;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class WelcomePageServlet extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    HttpSession session = request.getSession();

    response.setContentType("text/html");
    response.setStatus(HttpServletResponse.SC_OK);
    PrintWriter out = response.getWriter();
    String name = (String) session.getAttribute("username");
    String lastLogin = "";
    if (name == null || name.isEmpty()) {
      name = "Stranger";
    } else {
      name = StringEscapeUtils.escapeHtml4(name);
      DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
      lastLogin = databaseHandler.getLastLogin(name);
    }

    if (lastLogin == null) {
      lastLogin = "";
    }

    VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
    VelocityContext context = new VelocityContext();
    Template template = ve.getTemplate("src/main/templates/welcome.html");
    context.put("username", name);
    context.put("lastLogin", lastLogin);

    StringWriter writer = new StringWriter();
    template.merge(context, writer);

    out.println(writer);
  }
}
