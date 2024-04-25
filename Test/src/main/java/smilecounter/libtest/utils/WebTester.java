package smilecounter.libtest.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smilecounter.core.affective.enums.AffectiveServices;
import smilecounter.core.data.connectors.mongodb.MongoDatabaseConnector;
import smilecounter.core.data.model.ServiceTestResult;
import smilecounter.core.data.model.TestResult;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebTester {
    private final static Logger LOGGER = LoggerFactory.getLogger(WebTester.class);
    private static final String SERVICE_RESULTS_LINE_FORMAT = "%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%f";
    private static final String SERVICE_RESULTS_TITLE_FORMAT = "%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s";
    private static final String FORM_ANSWERS_TITLE_FORMAT = "%s\t%s\t%s\t%s\t%s";
    private static final String FORM_ANSWERS_LINE_FORMAT = "%s\t%s\t%s\t%s\t%s";

    public static void saveTestResults(String connectionUrl, String outputDirectory) throws IOException {
        MongoDatabaseConnector database = new MongoDatabaseConnector();
        database.init(connectionUrl);

        List<TestResult> serviceTestResults = database.getServiceTestResults();
        LOGGER.info("Downloaded {} services tests...", serviceTestResults.size());
        Map<AffectiveServices, List<ServiceTestResult>> groupedServiceTest = groupTestsByService(serviceTestResults);
        saveServiceTests(groupedServiceTest, outputDirectory + "/services");
        saveFormAnswers(serviceTestResults, outputDirectory + "/form");
    }

    private static void saveFormAnswers(List<TestResult> serviceTestResults, String directory) throws IOException {
        createDirectoryIfNotExists(directory);
        String fileName = directory + "/form_answers.txt";
        BufferedWriter file = createFileForFormAnswers(fileName);

        LOGGER.info("Saving form answers into file {}...", fileName);
        for(TestResult test : serviceTestResults){
            file.write(String.format(FORM_ANSWERS_LINE_FORMAT,
                    test.getGender() != null ? test.getGender() : "",
                    test.getAge() != null ? test.getAge() : "",
                    test.getAffectiveFuture() != null ? test.getAffectiveFuture() : "",
                    test.getGroupingSmiles() != null ? test.getGroupingSmiles() : "",
                    sanitizeString(test.getAdditionalData())
            ));
            file.newLine();
        }

        file.close();
    }

    private static String sanitizeString(String string){
        if(string != null){
            String[] lines = string.split("\n");
            return String.join("; ", lines);
        }
        return "";
    }

    private static void saveServiceTests(Map<AffectiveServices, List<ServiceTestResult>> groupedServiceTest, String directory) throws IOException {
        createDirectoryIfNotExists(directory);
        for (Map.Entry<AffectiveServices, List<ServiceTestResult>> serviceTests : groupedServiceTest.entrySet()) {
            String fileName = directory + "/" + serviceTests.getKey().getName() + ".txt";
            BufferedWriter file = createFileForService(fileName);
            List<ServiceTestResult> tests = serviceTests.getValue();

            LOGGER.info("Saving test results of service {} into file {}...", serviceTests.getKey().getName(), fileName);

            for(ServiceTestResult test : tests){
                file.write(String.format(SERVICE_RESULTS_LINE_FORMAT,
                        test.getOrder(),
                        test.getDetectedSmiles(),
                        test.getSmiling(),
                        test.getNotSmiling(),
                        test.getTimeSpent(),
                        test.getGeneralDetection() != null ?  test.getGeneralDetection() : "",
                        test.getSmileTypes() != null ? test.getSmileTypes().isWideOpen() :  "",
                        test.getSmileTypes() != null ? test.getSmileTypes().isOpen() :  "",
                        test.getSmileTypes() != null ? test.getSmileTypes().isClosed() :  "",
                        test.getSmileTypes() != null ? test.getSmileTypes().isNothing() :  "",
                        sanitizeString(test.getAdditionalData()),
                        test.getAverageResponseTime()));
                file.newLine();
            }

            file.close();
        }
    }

    private static BufferedWriter createFileForService(String file) throws IOException {
        File f = new File(file);
        FileWriter fw = new FileWriter(f.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write(String.format(SERVICE_RESULTS_TITLE_FORMAT, "Order", "Detected smiles", "Positive", "Negative",
                "Time spent", "General detection", "Wide open smiles", "Open smiles", "Closed smiles", "No smiles", "Informations", "Average response time"));
        bw.newLine();
        return bw;
    }

    private static BufferedWriter createFileForFormAnswers(String file) throws IOException {
        File f = new File(file);
        FileWriter fw = new FileWriter(f.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(String.format(FORM_ANSWERS_TITLE_FORMAT, "Gender", "Age", "Affective future", "Grouping smiles", "Affective future data"));
        bw.newLine();
        return bw;
    }

    private static Map<AffectiveServices, List<ServiceTestResult>> groupTestsByService(List<TestResult> serviceTestResults) {
        Map<AffectiveServices, List<ServiceTestResult>> result = new HashMap<>();

        for(TestResult testResult : serviceTestResults){
            List<ServiceTestResult> serviceResults = testResult.getServiceTestResults();

            for(ServiceTestResult serviceTest : serviceResults){
                AffectiveServices service = serviceTest.getService();
                List<ServiceTestResult> tests = result.getOrDefault(service, new ArrayList<>());
                tests.add(serviceTest);
                result.put(service, tests);
            }
        }


        return result;
    }

    private static void createDirectoryIfNotExists(String dir){
        new File(dir).mkdir();
    }

}
