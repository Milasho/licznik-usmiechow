package smilecounter.libtest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smilecounter.libtest.utils.WebTester;

public class MainWebTests {
    private final static Logger LOGGER = LoggerFactory.getLogger(MainWebTests.class);

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            LOGGER.error("Program needs four arguments: URL address for Mongo and output directory.");
        }
        else {
            String connectionUrl = args[0];
            String outputDirectory = args[1];

            WebTester.saveTestResults(connectionUrl, outputDirectory);
        }
    }
}
