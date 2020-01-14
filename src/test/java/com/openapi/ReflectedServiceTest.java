package com.openapi;

import com.openapi.controller.Controller;
import com.openapi.reflection.ReflectedService;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class ReflectedServiceTest {

    ReflectedService reflectedService = new ReflectedService("com.openapi.controller");

    Set<Class<?>> classes() throws ClassNotFoundException {
        return reflectedService.getClasses("com.openapi.controller.Controller");
    }

    @Test
    public void anyClassTest() throws ClassNotFoundException {
        Set<Class<?>> classes = classes();
        assertTrue(classes.size() > 0);
    }

    @Test
    public void anyMethodTest() throws ClassNotFoundException {
        Set<Class<?>> classes = classes();
        Class<?> firstClass = classes.stream().findFirst().get();
        Set<Method> methods = reflectedService.getMethods(firstClass);
        assertTrue(methods.size() > 0);
    }

    @Test
    public void anyMethodParametersTest() throws ClassNotFoundException {
        Set<Class<?>> classes = reflectedService.getClassesBySimpleName("HelloController");
        Class<?> firstClass = classes.stream().findFirst().get();
        Set<Method> methods = reflectedService.getMethodsByName(firstClass, "m3");
        Method firstMethod = methods.stream().findFirst().get();
        Set<Parameter> parameters = reflectedService.getParameters(firstMethod);
        assertTrue(parameters.size() > 0);
        Parameter firstParameter = parameters.stream().findFirst().get();
        assertTrue(firstParameter.getName().equals("a1"));
        assertTrue(firstParameter.getType().equals(String.class));
    }

}
