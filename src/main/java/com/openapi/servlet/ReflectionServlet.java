package com.openapi.servlet;

import com.openapi.reflection.APIBuilder;
import com.openapi.reflection.ReflectedService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;


@WebServlet(urlPatterns = {"/reflection/*"})
public class ReflectionServlet extends HttpServlet {

    Set<Class<?>> classes;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            classes = new ReflectedService("com.openapi.controller").getClasses("com.openapi.controller.Controller");
        } catch (ClassNotFoundException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();

        APIBuilder builder = new APIBuilder(classes, "http://localhost:8080/open-api-by-reflection");
        out.write(req.getParameter("yaml") == null ? builder.json() : builder.yaml());
        out.flush();
    }
}
