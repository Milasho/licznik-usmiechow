package smilecounter.core.affective.model;

import java.io.IOException;
import java.io.InputStream;

import org.openimaj.image.objectdetection.haar.Detector;
import org.openimaj.image.objectdetection.haar.OCVHaarLoader;
import org.openimaj.image.objectdetection.haar.StageTreeClassifier;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;

public class OpenImajHaarClassifier extends HaarCascadeDetector{
    public void setClassifier(InputStream stream) throws IOException {
        final StageTreeClassifier cascade = OCVHaarLoader.read(stream);
        this.detector = new Detector(cascade);
    }
}
