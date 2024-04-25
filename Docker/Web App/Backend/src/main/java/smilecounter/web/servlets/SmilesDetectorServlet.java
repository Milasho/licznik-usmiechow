package smilecounter.web.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smilecounter.core.affective.enums.AffectiveServices;
import smilecounter.web.services.SmileCounterService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ApplicationScoped
@WebServlet(urlPatterns = {"/rest/smiles-detector"})
public class SmilesDetectorServlet extends HttpServlet{
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Inject private SmileCounterService affectiveService;

    public SmilesDetectorServlet(){
        LOGGER.info("Creating smiles detector...");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        initializeSession(request);
        AffectiveServices service = getService(request);

    }

    private void initializeSession(HttpServletRequest request){
        if(request.getSession(false) == null){
            LOGGER.info("Creating new session for user {}", request.getRemoteAddr());
        }
        request.getSession();
    }

    private void sendObject(HttpServletResponse response, Object obj) throws IOException {
        PrintWriter out = response.getWriter();
        ObjectMapper objectMapper= new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(obj);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print(jsonString);
        out.flush();
    }

    private AffectiveServices getService(HttpServletRequest request){
        String serviceName = request.getParameter("service");
        AffectiveServices service = AffectiveServices.LUXAND;
        if(StringUtils.isNotEmpty(serviceName)){
            service = AffectiveServices.valueOf(serviceName);
        }
        return service;
    }
}
