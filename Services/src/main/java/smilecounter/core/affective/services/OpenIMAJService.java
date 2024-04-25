package smilecounter.core.affective.services;

import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.math.geometry.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smilecounter.core.affective.enums.AffectiveServices;
import smilecounter.core.affective.enums.HaarClassifiers;
import smilecounter.core.affective.interfaces.AffectiveService;
import smilecounter.core.affective.model.Face;
import smilecounter.core.affective.model.OpenImajHaarClassifier;
import smilecounter.core.affective.model.Smile;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class OpenIMAJService implements AffectiveService {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private HaarCascadeDetector faceDetector;
    private HaarCascadeDetector smileDetector;
    private HaarCascadeDetector mouthDetector;

    @PostConstruct
    public void init(){
        try {
            this.faceDetector =  new HaarCascadeDetector(30);
            this.smileDetector = loadClassifier(HaarClassifiers.SMILE);
            this.mouthDetector = loadClassifier(HaarClassifiers.MOUTH);
        }
        catch (IOException e) {
            LOGGER.error("Error loading haar classifier: ", e);
        }
    }

    @Override
    public List<Face> detectFacesWithSmile(BufferedImage image) {
        List<DetectedFace> faces = faceDetector.detectFaces(ImageUtilities.createFImage(image));
        List<Face> result = new ArrayList<>();
        for(DetectedFace detectedFace : faces){
            Face face = getFace(detectedFace);
            Smile smile = detectSmile(detectedFace.getFacePatch(), face);
            face.setSmile(smile);
            result.add(face);
        }

        return result;
    }

    @Override
    public AffectiveServices geServiceData() {
        return AffectiveServices.OPENIMAJ;
    }

    private Smile detectSmile(FImage image, Face face){
        List<DetectedFace> smiles = smileDetector.detectFaces(image);
        Smile smile = findSmile(face, smiles, 1);
        if(smile == null){
            List<DetectedFace> mouths = mouthDetector.detectFaces(image);
            smile = findSmile(face, mouths, 0);
        }
        face.setSmile(smile);

        return smile;
    }

    private Smile findSmile(Face face, List<DetectedFace> smiles, double confidence){
        Smile result = null;

        if(smiles != null && smiles.size() > 0){
            for(DetectedFace s : smiles){
                Rectangle smileBounds = s.getBounds();
                if(face.getHeight()/2 < s.getBounds().minY()){
                    result = new Smile();
                    result.setHeight(smileBounds.getHeight());
                    result.setWidth(smileBounds.getWidth());
                    result.setX(smileBounds.x + face.getX());
                    result.setY(smileBounds.y + face.getY());
                    result.setConfidence(confidence);
                }
            }
        }
        return result;
    }

    private Face getFace(DetectedFace detectedFace){
        Face face = new Face();
        Rectangle bounds = detectedFace.getBounds();
        face.setHeight(bounds.getHeight());
        face.setWidth(bounds.getWidth());
        face.setX(bounds.x);
        face.setY(bounds.y);
        return face;
    }

    private HaarCascadeDetector loadClassifier(HaarClassifiers classifier) throws IOException {
        InputStream classifierStream = classifier.getInputStream();
        OpenImajHaarClassifier detector = new OpenImajHaarClassifier();
        detector.setClassifier(classifierStream);
        LOGGER.debug("loadClassifier - {} ({})", classifier, classifier.getResourcesPath());
        return detector;
    }
}
