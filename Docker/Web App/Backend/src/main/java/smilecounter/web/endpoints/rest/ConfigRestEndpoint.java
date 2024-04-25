package smilecounter.web.endpoints.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/config")
@Api(value = "/config")
public class ConfigRestEndpoint {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @POST
    @Path("/init")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value="Initializes application")
    public void init(@Context HttpServletRequest request){
        String browserName = request.getHeader("User-Agent");
        String ipAdd = request.getRemoteAddr();
        LOGGER.info("New connection from {} ([BROWSER: {}]", ipAdd, browserName);
    }
}
