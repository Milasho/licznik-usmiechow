package smilecounter.libtest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smilecounter.libtest.utils.TestHelper;
import smilecounter.libtest.utils.Tester;

import java.util.List;

public class Main {
    private final static Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        if (args.length != 5) {
            LOGGER.error("Program needs four arguments: input directory with photos, output directory for results, path with dll for connectors and key for Luxand library.");
        }
        else {
            String inputDirectory = args[0];
            String outputDirectory = args[1];
            TestHelper.setLibsPath(args[2]);
            TestHelper.setUpLuxandKey(args[3]);
            boolean saveOutput = Boolean.TRUE.equals(Boolean.valueOf(args[4]));

            List<String> files = TestHelper.loadFilesFromDirectory(inputDirectory);
            //Tester.testOpenCVForDifferentSmileDetections(files, outputDirectory, saveOutput);
            Tester.testAllServicesForImages(files, outputDirectory, saveOutput);
        }
    }
}
