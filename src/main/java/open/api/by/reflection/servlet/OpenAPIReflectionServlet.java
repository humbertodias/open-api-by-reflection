package open.api.by.reflection.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
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
import java.util.*;

import io.swagger.v3.oas.models.parameters.Parameter;


@WebServlet(urlPatterns = {"/openapi-reflection/*"})
public class OpenAPIReflectionServlet extends HttpServlet {

    Reflections reflections = new Reflections(ReflectedService.CONTROLLER_PACKAGE);

    OpenAPI openapi(Set<Class<? extends Controller>> controllers) {
        OpenAPI openAPI = new OpenAPI();
        openAPI.servers(Arrays.asList( new Server().url("http://localhost:8080/open-api-by-reflection")));

        for (Class<?> clazz : controllers) {

            Tag tagClass = new Tag().name(clazz.getName());
            openAPI.addTagsItem(tagClass);

            for (Method method : clazz.getDeclaredMethods()) {

                List<Parameter> parameters = new ArrayList<>();
                Arrays.asList(method.getParameters()).forEach(parameter -> {
                    parameters.add(new Parameter().name(parameter.getName()).schema( new Schema().type(type(parameter))));
                });

                String name = clazz.isAnnotationPresent(WebServlet.class) ? clazz.getAnnotation(WebServlet.class).urlPatterns()[0] : clazz.getSimpleName();
                if (!name.startsWith("/")) name = '/' + name;

                openAPI.path(String.format("%s/%s", name, method.getName()), new PathItem().get(new Operation().parameters(parameters).addTagsItem(tagClass.getName())));

            }
        }

        return openAPI;

    }

    private String type(java.lang.reflect.Parameter parameter) {
        Class<?> type = parameter.getType();
        String typeName = type.getSimpleName().toLowerCase();
        List<String> numbers = Arrays.asList("int", "long", "float", "double");
        if (numbers.contains(typeName))
            return "number";
        else if (type.isArray())
            return "array";
        else if(typeName.equalsIgnoreCase("string"))
            return "string";
        else
            return type.getSimpleName();
    }

    Set<Class<? extends Controller>> controllers() {
        return reflections.getSubTypesOf(Controller.class);
    }

    String json() throws JsonProcessingException {
        return Json.pretty().writeValueAsString(openapi(controllers()));
    }

    String yaml() throws JsonProcessingException {
        return Yaml.pretty().writeValueAsString(openapi(controllers()));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        out.write(req.getParameter("yaml") == null ? json() : yaml());
        out.flush();
    }
}
