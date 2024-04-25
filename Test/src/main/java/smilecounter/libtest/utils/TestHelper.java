package smilecounter.libtest.utils;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smilecounter.core.affective.model.HumanFeature;
import smilecounter.core.affective.services.FaceSDKService;
import smilecounter.core.affective.services.OpenCVService;
import smilecounter.core.affective.services.OpenCVWithFaceSDKService;
import smilecounter.core.affective.services.OpenIMAJService;
import smilecounter.core.affective.enums.AffectiveServices;
import smilecounter.core.affective.interfaces.AffectiveService;
import smilecounter.core.affective.model.Face;
import smilecounter.core.affective.model.Smile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class TestHelper {
    private final static Logger LOGGER = LoggerFactory.getLogger(TestHelper.class);
    private final static String SAVE_FILE_FORMAT = "%s/%s_%s.jpg";

    private static String libsPath;
    private static String luxandKey;

    private static BufferedImage copyImage(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public static BufferedImage loadImageFromFile(String fileName) throws IOException {
        File input = new File(fileName);
        BufferedImage image = ImageIO.read(input);
        BufferedImage bimage = new BufferedImage(image.getWidth(null), image
                .getHeight(null), BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g = bimage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;
    }

    private static BufferedImage highlightSmilesOnImage(BufferedImage image, List<Face> faces, Color color) {
        if (faces != null && faces.size() > 0) {
            Graphics2D graph = image.createGraphics();
            graph.setStroke(new BasicStroke(2));

            for (Face face : faces) {
                graph.setColor(color);
                graph.drawRect((int) face.getX(), (int) face.getY(), (int) face.getWidth(), (int) face.getHeight());

                Smile smile = face.getSmile();
                if(smile != null){
                    graph.setColor(getColorForSmile(smile.getConfidence()));
                    graph.drawRect((int) smile.getX(), (int) smile.getY(), (int) smile.getWidth(), (int) smile.getHeight());
                }
                HumanFeature nose = face.getNose();
                if(nose != null){
                    graph.setColor(Color.BLACK);
                    graph.drawRect((int) nose.getX(), (int) nose.getY(), (int) nose.getWidth(), (int) nose.getHeight());
                }
            }

            graph.dispose();
        }

        return image;
    }

    private static Color getColorForSmile(double smileConfidence) {
        Color result = Color.RED;
        if (smileConfidence > 0.7) {
            result = Color.GREEN;
        }
        else if (smileConfidence > 0.5) {
            result = Color.YELLOW;
        }
        else if (smileConfidence > 0.3) {
            result = Color.ORANGE;
        }

        return result;
    }

    public static long countFoundSmiles(List<Face> foundFaces, double smileConfidenceTreshold) {
        long smiles = 0;
        for(Face face: foundFaces){
            if(face.getSmile() != null && face.getSmile().getConfidence() > smileConfidenceTreshold){
                smiles ++ ;
            }
        }
        return smiles;
    }

    public static List<String> loadFilesFromDirectory(String path) throws IOException {
        List<String> files = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(path))) {
            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    files.add(filePath.toString());
                }
            });
        }

        return files;
    }

    public static AffectiveService getService(AffectiveServices service) {
        AffectiveService result = null;

        switch (service) {
            case OPEN_CV:
                OpenCVService openCV = new OpenCVService();
                if(StringUtils.isNotEmpty(libsPath)){openCV.init(libsPath);}else{openCV.init();}
                result = openCV;
                break;
            case OPENIMAJ:
                OpenIMAJService openImaj = new OpenIMAJService();
                openImaj.init();
                result = openImaj;
                break;
            case LUXAND:
                FaceSDKService faceSdk = new FaceSDKService();
                if(StringUtils.isNotEmpty(libsPath)){faceSdk.init(libsPath);}else{faceSdk.init();}
                faceSdk.setKey(luxandKey);
                result = faceSdk;
                break;
            case CUSTOM:
                OpenCVWithFaceSDKService openCVWithFaceSDKService = new OpenCVWithFaceSDKService();
                openCVWithFaceSDKService.initNewServices();
                if(StringUtils.isNotEmpty(libsPath)){openCVWithFaceSDKService.init(libsPath);}else{openCVWithFaceSDKService.init();}
                openCVWithFaceSDKService.setLuxandKey(luxandKey);
                result = openCVWithFaceSDKService;
                break;
            default:
        }
        return result;
    }

    public static void saveAsImage(String directory, BufferedImage image, String fileName, String prefix, List<Face> foundFaces) throws IOException {
        createDirectoryIfNotExists(directory);

        BufferedImage facesImage = TestHelper.highlightSmilesOnImage(TestHelper.copyImage(image), foundFaces, Color.MAGENTA);

        String file = FilenameUtils.getName(fileName);
        String fileWithoutExt = file.substring(0, file.lastIndexOf('.'));
        ImageIO.write(facesImage, "jpg", new File(String.format(SAVE_FILE_FORMAT, directory, prefix, fileWithoutExt)));
    }

    public static void setUpLuxandKey(String key) {
        luxandKey = key;
    }

    public static void setLibsPath(String path){
        libsPath = path;
    }

    private static void createDirectoryIfNotExists(String dir){
        new File(dir).mkdir();
    }

    public static long countFoundMouths(List<Face> foundFaces) {
        long mouths = 0;
        for(Face face: foundFaces){
            if(face.getSmile() != null){
                mouths ++;
            }
        }
        return mouths;
    }
}
