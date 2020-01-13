package open.api.by.reflection;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.tags.Tag;
import open.api.by.reflection.controller.Controller;
import org.reflections.Reflections;

import javax.servlet.annotation.WebServlet;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class OpenAPIBuilder {

    private Reflections reflections;
    private OpenAPI openAPI;

    public OpenAPIBuilder(String fullPackage){
        this.reflections = new Reflections(fullPackage);
    }

    public OpenAPI openapi() {
        this.openAPI = openapi(controllers());
        return openAPI;
    }

    public OpenAPI openapi(Set<Class<? extends Controller>> controllers) {
        OpenAPI openAPI = new OpenAPI();

        for (Class<? extends Controller> clazz : controllers) {

            Tag tagClass = new Tag().name(clazz.getSimpleName());
            tagClass.description(clazz.getName());
            openAPI.addTagsItem(tagClass);

            for (Method method : clazz.getDeclaredMethods()) {

                List<Parameter> parameters = new ArrayList<>();
                Arrays.asList(method.getParameters()).forEach(parameter -> parameters.add(parameter(parameter)));
                openAPI.path(path(clazz, method), pathItem(parameters, tagClass));

            }
        }

        return openAPI;

    }

    private String path(Class<? extends Controller> clazz, Method method) {
        String name = clazz.isAnnotationPresent(WebServlet.class) ? clazz.getAnnotation(WebServlet.class).urlPatterns()[0] : clazz.getSimpleName();
        if (!name.startsWith("/")) name = '/' + name;
        return String.format("%s/%s", name, method.getName());
    }

    private PathItem pathItem(List<Parameter> parameters, Tag tagClass) {
        return new PathItem().get(new Operation().parameters(parameters).summary("summary").addTagsItem(tagClass.getName()));
    }

    private Parameter parameter(java.lang.reflect.Parameter parameter) {
        return new Parameter().name(parameter.getName()).schema(new Schema().type(type(parameter)));
    }

    private String type(java.lang.reflect.Parameter parameter) {
        Class<?> type = parameter.getType();
        String typeName = type.getSimpleName().toLowerCase();
        List<String> numbers = Arrays.asList("int", "long", "float", "double");
        if (numbers.contains(typeName))
            return "number";
        else if (type.isArray())
            return "array";
        else if (typeName.equalsIgnoreCase("string"))
            return "string";
        else
            return type.getSimpleName();
    }

    private Set<Class<? extends Controller>> controllers() {
        return reflections.getSubTypesOf(Controller.class);
    }

    public String json() throws JsonProcessingException {
        return Json.pretty().writeValueAsString(this.openAPI);
    }

    public String yaml() throws JsonProcessingException {
        return Yaml.pretty().writeValueAsString(this.openAPI);
    }

}
