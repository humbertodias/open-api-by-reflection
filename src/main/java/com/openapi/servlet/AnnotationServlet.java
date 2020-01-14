package com.openapi.servlet;

import io.swagger.servlet.config.ServletScanner;
import io.swagger.servlet.config.WebXMLReader;
import io.swagger.servlet.listing.ApiDeclarationServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;


@WebServlet(urlPatterns = {"/annotation/*"},
        initParams = {
                @WebInitParam(name = "swagger.resource.package", value = "com.openapi.controller"),
                @WebInitParam(name = "swagger.api.basepath", value = "http://localhost:8080/open-api-by-reflection"),
                @WebInitParam(name = "api.version", value = "1.0.0")
        }
)
public class AnnotationServlet extends ApiDeclarationServlet {
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        WebXMLReader webXMLReader = new WebXMLReader(servletConfig);
        servletConfig.getServletContext().setAttribute("reader", webXMLReader);
        servletConfig.getServletContext().setAttribute("scanner", new ServletScanner(servletConfig));
        super.init(servletConfig);
    }
}
