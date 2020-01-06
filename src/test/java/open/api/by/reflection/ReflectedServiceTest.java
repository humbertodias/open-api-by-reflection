package open.api.by.reflection;

import open.api.by.reflection.controller.Controller;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class ReflectedServiceTest {

    ReflectedService reflectedService = new ReflectedService();

    @Test
    public void anyClassTest() {
        Set<Class<? extends Controller>> classes = reflectedService.getClasses();
        assertTrue(classes.size() > 0);
    }

    @Test
    public void anyMethodTest() {
        Set<Class<? extends Controller>> classes = reflectedService.getClasses();
        Class<?> firstClass = classes.stream().findFirst().get();
        Set<Method> methods = reflectedService.getMethods(firstClass);
        assertTrue(methods.size() > 0);
    }

    @Test
    public void anyMethodParametersTest() {
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
