package open.api.by.reflection.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.tags.Tag;
import open.api.by.reflection.ReflectedService;
import open.api.by.reflection.controller.Controller;
import org.reflections.Reflections;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.swagger.v3.oas.models.parameters.Parameter;


@WebServlet(urlPatterns = {"/openapi-reflection/*"})
public class OpenAPIReflectionServlet extends HttpServlet {

    OpenAPI openapi() {
        Reflections reflections = new Reflections(ReflectedService.CONTROLLER_PACKAGE);

        OpenAPI openAPI = new OpenAPI().openapi("3.0.0");

        Set<Class<? extends Controller>> controllers = reflections.getSubTypesOf(Controller.class);
        for (Class<? extends Controller> clazz : controllers) {

            Tag tagClass = new Tag().name(clazz.getName());
            openAPI.addTagsItem( tagClass );

            for (Method method : clazz.getDeclaredMethods()) {

                List<Parameter> parameters = new ArrayList<>();
                for (java.lang.reflect.Parameter parameter : method.getParameters()) {
                    parameters.add(new Parameter().name(parameter.getName()).required(true));
                }

                openAPI.path("/" + method.getName(), new PathItem().description("DESC").get(new Operation().summary("List all pets").parameters(parameters)));

            }
        }

        return openAPI;

    }

    String json() throws JsonProcessingException {
        return Json.pretty().writeValueAsString(openapi());
    }

    String yaml() throws JsonProcessingException {
        return Yaml.pretty().writeValueAsString(openapi());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        out.write(req.getParameter("yaml") == null ? json() : yaml());
        out.flush();
    }
}
