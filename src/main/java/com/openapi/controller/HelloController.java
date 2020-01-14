package com.openapi.controller;

import io.swagger.annotations.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@SwaggerDefinition(
        info = @Info(
                description = "This is a sample server",
                version = "1.0.0",
                title = "Swagger Sample Servlet",
                termsOfService = "http://swagger.io/terms/",
                contact = @Contact(name = "Sponge-Bob", email = "apiteam@swagger.io", url = "http://swagger.io"),
                license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html")
        ),
        consumes = {"application/json", "application/xml"},
        produces = {"application/json", "application/xml"},
        schemes = {SwaggerDefinition.Scheme.HTTP, SwaggerDefinition.Scheme.HTTPS},
        tags = {@Tag(name = "users", description = "Operations about user")}
)
@Api(value = "/hello")
@WebServlet(urlPatterns = {"/hello"})
public class HelloController extends Controller {

    public void m1() {
        System.out.println("m1");
    }

    public String m2() {
        System.out.println("m2");
        return "m2";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (PrintWriter out = response.getWriter()) {
            out.print("doGet");
        }
    }

    @ApiOperation(httpMethod = "GET", value = "Resource to get a user")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "a1", value = "User ID", required = true, dataType = "string", paramType = "query")
    })
    public void m3(String a1) {
        System.out.println("m3:" + a1);
    }

}
