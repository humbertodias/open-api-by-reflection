package com.openapi;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.openapi.reflection.APIBuilder;
import com.openapi.reflection.ReflectedService;

import java.io.IOException;
import java.util.Set;

public class Main {

    @Parameter(names = {"-p", "-package"}, required = true, order = 0, description = "Searchable package path")
    private String packagePath = "com.openapi.controller";

    @Parameter(names = {"-t", "-type"}, required = true, order = 1, description = "SubType class full name")
    private String subType = "com.openapi.controller.Controller";

    @Parameter(names = {"-o", "-output"}, required = true, order = 2, description = "Output file")
    private String outputFile = "/tmp/openapi.json";

    @Parameter(names = {"-s", "-serverURL"}, order = 3, description = "Server URL")
    private String serverURL = "http://localhost:8080/open-api-by-reflection";

    public static void main(String[] argv) throws IOException, ClassNotFoundException {

        Main main = new Main();
        JCommander cmd = JCommander.newBuilder()
                .addObject(main)
                .build();
        if (argv.length < 2) {
            cmd.usage();
            System.exit(-1);
        } else {
            cmd.parse(argv);
            main.run();
        }

    }

    public void run() throws IOException, ClassNotFoundException {
        ReflectedService reflectedService = new ReflectedService(packagePath);
        Set<Class<?>> classes = reflectedService.getClasses(subType);
        APIBuilder builder = new APIBuilder(classes, serverURL);
        builder.write(outputFile);
    }

}
