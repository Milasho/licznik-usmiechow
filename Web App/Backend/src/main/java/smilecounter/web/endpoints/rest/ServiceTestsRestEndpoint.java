package smilecounter.web.endpoints.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smilecounter.core.data.model.TestResult;
import smilecounter.web.services.TestServicesService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/serviceTest")
@Api(value = "/serviceTest")
public class ServiceTestsRestEndpoint {
    @Inject private TestServicesService service;

    @POST
    @Path("/send")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value="Saves information about performed tests")
    public void saveTestResult(TestResult testResult){
        service.saveTestResult(testResult);
    }
}
