package smilecounter.libtest.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smilecounter.core.affective.enums.AffectiveServices;
import smilecounter.core.affective.interfaces.AffectiveService;
import smilecounter.core.affective.model.Face;
import smilecounter.core.affective.services.OpenCVService;
import smilecounter.libtest.model.TestInfo;
import smilecounter.libtest.model.TestResult;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Tester {
    private final static Logger LOGGER = LoggerFactory.getLogger(Tester.class);

    private final static String RESULT_LOG_FORMAT = "FILE: {} -> {DETECTION: {} ms, FACES: {}}";
    private final static String TEST_END_LOG_FORMAT = "[END] Test ended successfully in {} ms.";
    private final static String RESULT_FILE_FORMAT = "%s/%s/results.log";

    private final static double SMILE_CONFIDENCE_TRESHOLD = 0.45;
    private final static AffectiveServices[] SERVICES_TO_TEST = {
            //AffectiveServices.OPENIMAJ,
            AffectiveServices.OPEN_CV,
            //AffectiveServices.LUXAND,
            AffectiveServices.CUSTOM
    };

    public static void testDifferenceBetweenNestingClassifiers(List<String> images, String outputDirectory) throws IOException {
        LOGGER.info("Test will process {} images.", images.size());

        for(String image : images){
            BufferedImage img = TestHelper.loadImageFromFile(image);
            OpenCVService openCVService = (OpenCVService) TestHelper.getService(AffectiveServices.OPEN_CV);

            List<Face> simpleFaces = openCVService.simpleSmileDetection(img);
            List<Face> faces = openCVService.detectFacesWithSmile(img);

            LOGGER.info("For {} found {} simple smiles and {} normal ones.", image, simpleFaces.size(), faces.size());
            TestHelper.saveAsImage(outputDirectory + "/simple", img, image, "", simpleFaces);
            TestHelper.saveAsImage(outputDirectory + "/normal", img, image, "", faces);

        }
    }

    public static void testAllServicesForImages(List<String> images, String outputDirectory, boolean saveOutput) throws IOException {
        long testStart = System.currentTimeMillis();
        LOGGER.info("Test will process {} images.", images.size());
        for (AffectiveServices s : SERVICES_TO_TEST) {
            AffectiveService service = TestHelper.getService(s);
            TestInfo testInfo = testService(service, images, outputDirectory, saveOutput);

            LOGGER.info("Test result: {}", testInfo.toString());

            saveResultsFile(outputDirectory, service.geServiceData().getName(), testInfo);
        }
        LOGGER.info(TEST_END_LOG_FORMAT, (System.currentTimeMillis() - testStart));
    }

    private static void saveResultsFile(String outputDirectory, String name, TestInfo testInfo) throws IOException {
        String resultFile = String.format(RESULT_FILE_FORMAT, outputDirectory, name);
        BufferedWriter out = new BufferedWriter(new FileWriter(resultFile));
        out.write(testInfo.toString());
        out.close();
    }

    public static void testOpenCVForDifferentSmileDetections(List<String> images, String outputDirectory, boolean saveOutput) throws IOException {
        long testStart = System.currentTimeMillis();
        LOGGER.info("Test will process {} images.", images.size());
        OpenCVService service = (OpenCVService) TestHelper.getService(AffectiveServices.OPEN_CV);
        String serviceName = service.geServiceData().getName();
        TestInfo testInfoForSimpleDetections = new TestInfo(service.geServiceData().getName());
        TestInfo testInfoForDetectionsWithNose = new TestInfo(service.geServiceData().getName());

        for(String imagePath : images){
            BufferedImage image = TestHelper.loadImageFromFile(imagePath);

            TestResult simpleResult = testSmileDetection(false, image, service);
            TestResult noseResult = testSmileDetection(true, image, service);

            testInfoForSimpleDetections.updateFrom(simpleResult, SMILE_CONFIDENCE_TRESHOLD);
            testInfoForDetectionsWithNose.updateFrom(noseResult, SMILE_CONFIDENCE_TRESHOLD);

            if(saveOutput){
                TestHelper.saveAsImage(outputDirectory + "/detections/simple", image, imagePath,
                        serviceName, simpleResult.getFoundFaces());
                TestHelper.saveAsImage(outputDirectory + "/detections/nose", image, imagePath,
                        serviceName, noseResult.getFoundFaces());
            }
        }

        LOGGER.info(TEST_END_LOG_FORMAT, (System.currentTimeMillis() - testStart));
    }

    private static TestInfo testService(AffectiveService service, List<String> images, String outputDirectory, boolean saveOutput) throws IOException {
        String serviceName = service.geServiceData().getName();
        TestInfo testInfo = new TestInfo(serviceName);

        LOGGER.info("[TESTING {}]", serviceName.toUpperCase());
        for (String img : images) {
            BufferedImage image = TestHelper.loadImageFromFile(img);
            TestResult testResult = testSmileDetectionsForService(image, service);
            testInfo.updateFrom(testResult, SMILE_CONFIDENCE_TRESHOLD, img);

            LOGGER.info(RESULT_LOG_FORMAT, img, testResult.getDetectionTime(),
                    testResult.getFoundFaces() != null ? testResult.getFoundFaces().size() : 0);
            if(saveOutput){
                TestHelper.saveAsImage(outputDirectory + "/" + serviceName, image, img, serviceName, testResult.getFoundFaces());
            }
        }

        testInfo.setAverageDetectionTime(testInfo.getAverageDetectionTime() / images.size());

        return testInfo;
    }

    private static TestResult testSmileDetectionsForService(BufferedImage image, AffectiveService service) throws IOException {
        TestResult result = new TestResult();

        long time = System.currentTimeMillis();
        List<Face> foundFaces = service.detectFacesWithSmile(image);
        result.setDetectionTime(System.currentTimeMillis() - time);
        result.setFoundFaces(foundFaces);

        return result;
    }

    private static TestResult testSmileDetection(boolean withNose, BufferedImage image, OpenCVService service){
        TestResult result = new TestResult();

        long time = System.currentTimeMillis();
        List<Face> foundFaces = withNose ? service.detectFacesWithSmileByNoseDetection(image)
                : service.detectFacesWithSmile(image);
        result.setDetectionTime(System.currentTimeMillis() - time);
        result.setFoundFaces(foundFaces);
        return result;
    }
}
