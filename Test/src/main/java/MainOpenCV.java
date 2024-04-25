import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smilecounter.libtest.utils.TestHelper;
import smilecounter.libtest.utils.Tester;

import java.util.List;

public class MainOpenCV {
    private final static Logger LOGGER = LoggerFactory.getLogger(MainOpenCV.class);

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            LOGGER.error("Program needs three arguments: input directory with photos, output directory for results and path with dll.");
        }
        else {
            String inputDirectory = args[0];
            String outputDirectory = args[1];
            TestHelper.setLibsPath(args[2]);

            List<String> files = TestHelper.loadFilesFromDirectory(inputDirectory);
            Tester.testDifferenceBetweenNestingClassifiers(files, outputDirectory);
        }
    }
}
