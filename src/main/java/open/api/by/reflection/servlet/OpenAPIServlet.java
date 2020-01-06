package open.api.by.reflection.servlet;

import io.swagger.servlet.config.ServletScanner;
import io.swagger.servlet.config.WebXMLReader;
import io.swagger.servlet.listing.ApiDeclarationServlet;
import open.api.by.reflection.ReflectedService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;


@WebServlet(urlPatterns = {"/openapi/*"},
        initParams = {
                @WebInitParam(name = "swagger.resource.package", value = ReflectedService.CONTROLLER_PACKAGE),
                @WebInitParam(name = "swagger.api.basepath", value = "http://localhost:8080/open-api-by-reflection"),
                @WebInitParam(name = "api.version", value = "1.0.0")
        }
)
public class OpenAPIServlet extends ApiDeclarationServlet {
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        WebXMLReader webXMLReader = new WebXMLReader(servletConfig);
        servletConfig.getServletContext().setAttribute("reader", webXMLReader);
        servletConfig.getServletContext().setAttribute("scanner", new ServletScanner(servletConfig));
        super.init(servletConfig);
    }
}
