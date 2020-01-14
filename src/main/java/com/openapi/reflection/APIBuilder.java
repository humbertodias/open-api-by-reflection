package com.openapi.reflection;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;

import javax.servlet.annotation.WebServlet;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class APIBuilder {

    private OpenAPI openAPI;

    public APIBuilder(Set<Class<?>> classes, String serverURL) {
        this.openAPI = openapi(classes).servers(Arrays.asList(new Server().url(serverURL)));
    }

    Set<Class<?>> classesByName(Set<Class<?>> classes){
        return classes.stream().sorted(Comparator.comparing(Class::getSimpleName)).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public OpenAPI openapi(Set<Class<?>> classes) {
        OpenAPI openAPI = new OpenAPI();

        for (Class<?> clazz : classesByName(classes)) {

            Tag tagClass = new Tag().name(clazz.getSimpleName()).description(clazz.getName());
            openAPI.addTagsItem(tagClass);

            methods(openAPI, clazz, tagClass);

        }

        return openAPI;

    }

    private void methods(OpenAPI openAPI, Class<?> clazz, Tag tagClass) {
        Stream<Method> sortedMethods = Arrays.asList(clazz.getDeclaredMethods()).stream().sorted(Comparator.comparing(Method::getName));
        sortedMethods.forEach(method -> method(openAPI, clazz, tagClass, method));
    }

    private void method(OpenAPI openAPI, Class<?> clazz, Tag tagClass, Method method) {
        openAPI.path(path(clazz, method), pathItem(method, tagClass));
    }

    private Parameter parameter(java.lang.reflect.Parameter parameter) {
        return new Parameter().name(parameter.getName()).schema(new Schema().type(type(parameter)));
    }

    private String path(Class<?> clazz, Method method) {
        String name = clazz.isAnnotationPresent(WebServlet.class) ? clazz.getAnnotation(WebServlet.class).urlPatterns()[0] : clazz.getSimpleName();
        if (!name.startsWith("/")) name = '/' + name;
        return String.format("%s/%s", name, method.getName());
    }

    private PathItem pathItem(Method method, Tag tagClass) {
        PathItem pathItem = new PathItem();

        String name = method.getName().toLowerCase();
        if(name.contains("edit") || name.contains("update") || name.contains("modify"))
            return pathItem.put(operation(method, tagClass));
        if(name.contains("save") || name.contains("add") || name.contains("new"))
            return pathItem.post(operation(method, tagClass));
        if(name.contains("remove") || name.contains("delete"))
            return pathItem.delete(operation(method, tagClass));

        return pathItem.get(operation(method, tagClass));
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
