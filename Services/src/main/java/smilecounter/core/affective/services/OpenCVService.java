package smilecounter.core.affective.services;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smilecounter.core.affective.enums.AffectiveServices;
import smilecounter.core.affective.enums.HaarClassifiers;
import smilecounter.core.affective.interfaces.AffectiveService;
import smilecounter.core.affective.model.Face;
import smilecounter.core.affective.model.HumanFeature;
import smilecounter.core.affective.model.Smile;
import smilecounter.core.affective.utils.AffectiveLibsLoader;

import javax.enterprise.context.ApplicationScoped;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgcodecs.Imgcodecs.imwrite;

@ApplicationScoped
public class OpenCVService implements AffectiveService{
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private CascadeClassifier faceDetector;
    private CascadeClassifier smileDetector;
    private CascadeClassifier mouthDetector;
    private CascadeClassifier noseDetector;

    public void init() {
        if (AffectiveLibsLoader.loadLib(AffectiveServices.OPEN_CV)) {
            loadDetectors();
        }
    }

    public void init(String libsPath) {
        if (AffectiveLibsLoader.loadLib(AffectiveServices.OPEN_CV, libsPath)) {
            loadDetectors();
        }
    }

    private void loadDetectors(){
        faceDetector = loadClassifier(HaarClassifiers.FRONTAL_FACE);
        smileDetector = loadClassifier(HaarClassifiers.SMILE);
        mouthDetector = loadClassifier(HaarClassifiers.MOUTH);
        noseDetector = loadClassifier(HaarClassifiers.NOSE);
    }

    public void switchToLBP(){
        faceDetector = loadClassifier(HaarClassifiers.FRONTAL_FACE_LBP);
    }

    @Override
    public List<Face> detectFacesWithSmile(BufferedImage image) {
        List<Face> result = new ArrayList<>();

        Mat img = convertImageToMat(image);
        if (img != null) {
            Mat grayImg = sanitizeMat(img);
            MatOfRect faceDetections = detectFaces(grayImg);

            for (Rect rect : faceDetections.toArray()) {
                Mat faceMat = new Mat(grayImg, rect);
                Face face = getFace(rect);
                Smile smile = detectSmile(faceMat, face);
                face.setSmile(smile);
                result.add(face);
            }
        }
        else {
            result = new ArrayList<>();
        }

        return result;
    }

    @Override
    public AffectiveServices geServiceData() {
        return AffectiveServices.OPEN_CV;
    }

    public List<Face> simpleSmileDetection(BufferedImage image){
        List<Face> result = new ArrayList<>();

        Mat img = convertImageToMat(image);
        if (img != null) {
            Mat grayImg = sanitizeMat(img);
            Rect[] foundSmiles = detectSimpleSmiles(smileDetector, grayImg);

            for(Rect rect : foundSmiles){
                Face face = new Face();
                face.setSmile(getSmile(rect, face, 1));
                result.add(face);
            }

            Rect[] foundMouths = detectSimpleSmiles(mouthDetector, grayImg);
            for(Rect rect : foundMouths){
                Face face = new Face();
                face.setSmile(getSmile(rect, face, 0));
                result.add(face);
            }
        }

        return result;
    }

    private CascadeClassifier loadClassifier(HaarClassifiers haarClassifier){
        LOGGER.debug("Loading classifier {} (from {})...", haarClassifier, haarClassifier.getResourcesPath());

        String classifierAbsolutePath;
        CascadeClassifier classifier = null;
        try {
            classifierAbsolutePath = haarClassifier.getExternalPath();
            classifier = new CascadeClassifier(classifierAbsolutePath);
            boolean loadedSuccessfully = classifier.load(classifierAbsolutePath);
            if(!loadedSuccessfully){
                throw new Exception("Classifier wasn't loaded sucessfully!");
            }
        }
        catch (Exception e) {
            LOGGER.error("Error during loading classifier {}", haarClassifier.getResourcesPath(), e);
        }

        return classifier;
    }

    private Face getFace(Rect rect) {
        Face face = new Face();
        face.setHeight(rect.height);
        face.setWidth(rect.width);
        face.setX(rect.x);
        face.setY(rect.y);
        return face;
    }

    private Smile getSmile(Rect rect, Face face, double smileConfidence) {
        Smile smile = new Smile();
        smile.setHeight(rect.height);
        smile.setWidth(rect.width);
        smile.setX(rect.x + face.getX());
        smile.setY(rect.y + face.getY());
        smile.setConfidence(smileConfidence);
        return smile;
    }

    private Smile detectSmile(Mat mat, Face face) {
        double smileConfidence = 1;
        Smile smile = null;
        Rect[] foundSmiles = detectSmilesWithClassifier(smileDetector, mat);
        if(foundSmiles.length == 0){
            foundSmiles = detectSmilesWithClassifier(mouthDetector, mat);
            smileConfidence = 0;
        }

        for (Rect s : foundSmiles) {
            if (s.y > mat.height() / 2) {
                smile = getSmile(s, face, smileConfidence);
                break;
            }
        }

        return smile;
    }

    public List<Face> detectFacesWithSmileByNoseDetection(BufferedImage image) {
        List<Face> result = new ArrayList<>();

        Mat img = convertImageToMat(image);
        if (img != null) {
            Mat grayImg = sanitizeMat(img);
            MatOfRect faceDetections = detectFaces(grayImg);

            for (Rect rect : faceDetections.toArray()) {
                Face face = getFace(rect);
                Mat faceMat = new Mat(grayImg, rect);
                HumanFeature nose = detectNose(faceMat, face);
                /*Rect noseRect = new Rect(rect.x, (int)nose.getY(), rect.width, rect.height);
                Mat underNose = new Mat(grayImg, noseRect);
                Smile smile = detectSmile(underNose, face);
                face.setSmile(smile);*/
                face.setNose(nose);
                result.add(face);

                Rect noseRect = new Rect((int)nose.getX(), (int)nose.getY(),(int) nose.getWidth(), (int)nose.getHeight());
                Mat underNose = new Mat(grayImg, noseRect);
                imwrite( "E:\\Projekty\\Magisterka\\smiles-test\\results\\detections\\nose\\nose.jpg", underNose);
                imwrite( "E:\\Projekty\\Magisterka\\smiles-test\\results\\detections\\nose\\nose2.jpg", faceMat);
                int a = 5;
            }
        }
        else {
            result = new ArrayList<>();
        }

        return result;
    }

    private HumanFeature getFeature(Rect rect, HumanFeature parent){
        HumanFeature feature = new HumanFeature();
        feature.setHeight(rect.height);
        feature.setWidth(rect.width);
        feature.setX(rect.x + parent.getX());
        feature.setY(rect.y + parent.getY());
        return feature;
    }

    private HumanFeature detectNose(Mat mat, Face face){
        HumanFeature nose = null;
        Rect[] foundNoses = detectNosesWithClassifier(mat);
        LOGGER.debug("Found {} noses - {} ({})", foundNoses.length, foundNoses[0], face.toString());
        if(foundNoses.length > 0){
            nose = getFeature(foundNoses[0], face);
        }
        return nose;
    }

    private Rect[] detectNosesWithClassifier(Mat mat){
        MatOfRect noseDetections = new MatOfRect();
        noseDetector.detectMultiScale(mat, noseDetections, 1.1, 5,
                Objdetect.CASCADE_SCALE_IMAGE,
                new Size(30, 30),
                new Size(mat.width(), mat.height()));
        return noseDetections.toArray();
    }

    private Rect[] detectSmilesWithClassifier(CascadeClassifier classifier, Mat mat){
        MatOfRect smileDetections = new MatOfRect();
        classifier.detectMultiScale(mat, smileDetections, 1.05, 5,
                Objdetect.CASCADE_SCALE_IMAGE | Objdetect.CASCADE_FIND_BIGGEST_OBJECT,
                new Size(mat.width() / 8, mat.height() / 10),
                new Size(mat.width() / 2, mat.height() / 3));
        return smileDetections.toArray();
    }

    private Rect[] detectSimpleSmiles(CascadeClassifier classifier, Mat mat){
        MatOfRect smileDetections = new MatOfRect();
        classifier.detectMultiScale(mat, smileDetections, 1.05, 5,
                Objdetect.CASCADE_SCALE_IMAGE,
                new Size(20, 20),
                new Size(mat.width() / 2, mat.height() / 3));
        return smileDetections.toArray();
    }

    protected Mat convertImageToMat(BufferedImage in) {
        Mat out = new Mat(in.getHeight(), in.getWidth(), CvType.CV_8UC3);
        byte[] data = new byte[in.getWidth() * in.getHeight() * (int) out.elemSize()];
        int[] dataBuff = in.getRGB(0, 0, in.getWidth(), in.getHeight(), null, 0, in.getWidth());
        for (int i = 0; i < dataBuff.length; i++) {
            data[i * 3] = (byte) ((dataBuff[i]));
            data[i * 3 + 1] = (byte) ((dataBuff[i]));
            data[i * 3 + 2] = (byte) ((dataBuff[i]));
        }
        out.put(0, 0, data);
        return out;
    }

    public Mat sanitizeMat(Mat img) {
        Mat grayFrame = new Mat();
        Imgproc.cvtColor(img, grayFrame, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(grayFrame, grayFrame);

        return grayFrame;
    }

    public MatOfRect detectFaces(Mat img) {
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(img, faceDetections, 1.05, 10,
                Objdetect.CASCADE_SCALE_IMAGE,
                new Size(30, 30), new Size(img.width(), img.height()));
        return faceDetections;
    }
}
