package com.openapi.reflection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.openapi.controller.Controller;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.reflections.Reflections;

import javax.servlet.annotation.WebServlet;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class APIBuilder {

    private Reflections reflections;
    private OpenAPI openAPI;

    public APIBuilder(String fullPackage, String serverURL) {
        this.reflections = new Reflections(fullPackage);
        this.openAPI = openapi(controllers()).servers(Arrays.asList(new Server().url(serverURL)));
    }

    public OpenAPI openapi() {
        return this.openAPI;
    }

    private OpenAPI openapi(Set<Class<? extends Controller>> controllers) {
        OpenAPI openAPI = new OpenAPI();

        for (Class<? extends Controller> clazz : controllers) {

            Tag tagClass = new Tag().name(clazz.getSimpleName()).description(clazz.getName());
            openAPI.addTagsItem(tagClass);

            methods(openAPI, clazz, tagClass);

        }

        return openAPI;

    }

    private void methods(OpenAPI openAPI, Class<? extends Controller> clazz, Tag tagClass) {
        for (Method method : clazz.getDeclaredMethods()) {
            method(openAPI, clazz, tagClass, method);
        }
    }

    private void method(OpenAPI openAPI, Class<? extends Controller> clazz, Tag tagClass, Method method) {
        openAPI.path(path(clazz, method), pathItem(method, tagClass));
    }

    private Parameter parameter(java.lang.reflect.Parameter parameter) {
        return new Parameter().name(parameter.getName()).schema(new Schema().type(type(parameter)));
    }

    private String path(Class<? extends Controller> clazz, Method method) {
        String name = clazz.isAnnotationPresent(WebServlet.class) ? clazz.getAnnotation(WebServlet.class).urlPatterns()[0] : clazz.getSimpleName();
        if (!name.startsWith("/")) name = '/' + name;
        return String.format("%s/%s", name, method.getName());
    }


    private PathItem pathItem(Method method, Tag tagClass) {
        return new PathItem().get(operation(method, tagClass));
    }

    private Operation operation(Method method, Tag tagClass) {

        List<Parameter> parameters = new ArrayList<>();
        Arrays.asList(method.getParameters()).forEach(parameter -> parameters.add(parameter(parameter)));

        Operation operation = new Operation().parameters(parameters).summary("summary").addTagsItem(tagClass.getName());

        Class<?> returnType = method.getReturnType();
        if (returnType.getSimpleName() != "void") {
            ApiResponses responses = new ApiResponses();
            responses.addApiResponse("200", new ApiResponse().description(returnType.getName()));
            operation.responses(responses);
        }

        return operation;
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

    public void write(String path) throws IOException {
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(path.endsWith("json") ? json() : yaml());
            writer.flush();
        }
    }

}
