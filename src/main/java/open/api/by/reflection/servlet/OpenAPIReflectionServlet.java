package open.api.by.reflection.servlet;

import io.swagger.v3.oas.models.servers.Server;
import open.api.by.reflection.OpenAPIBuilder;
import open.api.by.reflection.ReflectedService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;


@WebServlet(urlPatterns = {"/openapi-reflection/*"})
public class OpenAPIReflectionServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();

        OpenAPIBuilder openAPIBuilder = new OpenAPIBuilder(ReflectedService.CONTROLLER_PACKAGE);
        openAPIBuilder.openapi().servers(Arrays.asList(new Server().url("http://localhost:8080/open-api-by-reflection")));

        out.write(req.getParameter("yaml") == null ? openAPIBuilder.json() : openAPIBuilder.yaml());
        out.flush();
    }
}
