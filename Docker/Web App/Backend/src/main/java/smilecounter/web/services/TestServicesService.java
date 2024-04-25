package smilecounter.web.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smilecounter.core.data.model.TestResult;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TestServicesService {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Inject
    private DatabaseService databaseService;

    public void saveTestResult(TestResult testResult){
        LOGGER.info("Saving test result...");
        databaseService.saveTestResult(testResult);
    }
}
