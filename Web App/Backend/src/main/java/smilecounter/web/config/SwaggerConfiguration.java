package smilecounter.web.config;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import io.swagger.jaxrs.config.BeanConfig;
public class SwaggerConfiguration extends HttpServlet {
    private static final long serialVersionUID = -3881551615695108700L;

    @Override
    public void init(ServletConfig config) throws ServletException {
        initSwagger();
    }

    private void initSwagger(){
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("1.0.2");
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setHost("localhost:8080");
        beanConfig.setBasePath("/smilecounter/rest");
        beanConfig.setResourcePackage("smilecounter.web.endpoints.rest");
        beanConfig.setScan(true);
    }
}