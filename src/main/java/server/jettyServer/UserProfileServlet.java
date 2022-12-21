package server.jettyServer;

import hotelapp.DatabaseHandler;
import hotelapp.Link;
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
import java.util.Set;

public class UserProfileServlet extends HttpServlet {
  private final DatabaseHandler databaseHandler = DatabaseHandler.getInstance();

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
    Set<Link> links = databaseHandler.getExpediaLinks(name);

    VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
    Template template = ve.getTemplate("src/main/templates/user-profile.html");
    VelocityContext context = new VelocityContext();
    context.put("username", name);
    context.put("links", links);

    StringWriter writer = new StringWriter();
    template.merge(context, writer);
    out.println(writer);
  }
}
