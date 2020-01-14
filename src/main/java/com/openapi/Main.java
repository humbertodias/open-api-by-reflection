package com.openapi;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.openapi.reflection.APIBuilder;

import java.io.IOException;

public class Main {

    @Parameter(names = {"-p", "-package"}, required = true, description = "Searchable package path")
    private String packagePath = "com.openapi.controller";

    @Parameter(names = {"-o", "-output"}, required = true, description = "Output file")
    private String outputFile = "/tmp/openapi.json";

    @Parameter(names = {"-s", "-serverURL"}, description = "Server URL")
    private String serverURL = "http://localhost:8080/open-api-by-reflection";

    public static void main(String[] argv) throws IOException {

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

    public void run() throws IOException {
        APIBuilder builder = new APIBuilder(packagePath, serverURL);
        builder.write(outputFile);
    }

}
