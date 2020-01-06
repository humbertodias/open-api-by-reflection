package open.api.by.reflection;

import open.api.by.reflection.controller.Controller;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public final class ReflectedService {

    public final static String CONTROLLER_PACKAGE = "open.api.by.reflection.controller";
    final Reflections reflections;

    public ReflectedService(){
        reflections = new Reflections(CONTROLLER_PACKAGE, new SubTypesScanner(false));
    }

    public Set<Class<? extends Controller>> getClasses() {
        return reflections.getSubTypesOf(Controller.class);
    }

    public Set<Class<?>> getClassesBySimpleName(String name) {
        return getClasses().stream()
                .filter(clazz -> clazz.getSimpleName().equals(name))
                .collect(Collectors.toSet());
    }

    public Set<Class<?>> getClassesByName(String name) {
        return getClasses().stream()
                .filter(clazz -> clazz.getName().equals(name))
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
