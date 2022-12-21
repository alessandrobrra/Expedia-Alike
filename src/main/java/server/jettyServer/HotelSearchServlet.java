package server.jettyServer;

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

public class HotelSearchServlet extends HttpServlet {
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
    VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
    Template template = ve.getTemplate("src/main/templates/hotel-search.html");
    VelocityContext context = new VelocityContext();
    context.put("username", name);
    if (request.getParameter("invalid") != null) {
      context.put("hotel-error", true);
    } else {
      context.put("hotel-error", false);
    }
    StringWriter writer = new StringWriter();
    template.merge(context, writer);
    out.println(writer);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String name = (String) request.getSession().getAttribute("username");
    String query = request.getParameter("hotel-search");
    query = StringEscapeUtils.escapeHtml4(query);
    response.sendRedirect("/hotel-result?query=" + query);
  }
}
