package com.openapi.servlet;

import com.openapi.reflection.APIBuilder;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@WebServlet(urlPatterns = {"/reflection/*"})
public class ReflectionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();

        APIBuilder builder = new APIBuilder("com.openapi.controller", "http://localhost:8080/open-api-by-reflection");
        out.write(req.getParameter("yaml") == null ? builder.json() : builder.yaml());
        out.flush();
    }
}
