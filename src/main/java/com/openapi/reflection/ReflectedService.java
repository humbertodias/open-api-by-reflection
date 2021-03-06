package com.openapi.reflection;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public final class ReflectedService {

    final Reflections reflections;

    public ReflectedService(String packagePath){
        reflections = new Reflections(packagePath, new SubTypesScanner(false));
    }

    public Set<Class<?>> getClasses() throws ClassNotFoundException {
        return getClasses("com.openapi.controller.Controller");
    }

    public Set<Class<?>> getClasses(String className) throws ClassNotFoundException {
        Class<Object> type = (Class<Object>) Class.forName(className);
        return reflections.getSubTypesOf(type);
    }

    public Set<Class<?>> getClassesBySimpleName(String name) throws ClassNotFoundException {
        return getClasses().stream()
                .filter(clazz -> clazz.getSimpleName().equals(name))
                .collect(Collectors.toSet());
    }

    public Set<Method> getMethods(Class<?> clazz) {
        return Arrays.asList(clazz.getDeclaredMethods()).stream().collect(Collectors.toSet());
    }

    public Set<Method> getMethodsByName(Class<?> clazz, String name) {
        return getMethods(clazz).stream().filter(method -> method.getName().equals(name)).collect(Collectors.toSet());
    }

    public Set<Parameter> getParameters(Method method) {
        return Arrays.asList(method.getParameters()).stream().collect(Collectors.toSet());
    }

}
