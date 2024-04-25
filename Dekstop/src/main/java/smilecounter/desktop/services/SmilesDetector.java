package smilecounter.desktop.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smilecounter.core.affective.model.Face;
import smilecounter.core.affective.model.Smile;
import smilecounter.desktop.config.ApplicationConfiguration;
import smilecounter.desktop.model.DetectedFace;
import smilecounter.core.data.model.FaceStatus;
import smilecounter.desktop.model.SingleFrame;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class SmilesDetector {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Inject private AffectiveService affectiveService;
    @Inject private ApplicationConfiguration appConfig;

    private List<SingleFrame> previousFrames;
    private List<Face> newSmiles;

    private int maxPreviousFrames;
    private int minSmilingFrames;

    @PostConstruct
    public void init(){
        maxPreviousFrames = 5;
        minSmilingFrames = 1;
        previousFrames = new ArrayList<>(maxPreviousFrames);
        newSmiles = new ArrayList<>();
    }

    public List<Face> getNewSmiles(){
        return newSmiles;
    }

    public List<DetectedFace> addFrame(BufferedImage frame){
        newSmiles = new ArrayList<>();

        List<Face> allFaces = affectiveService.getService().detectFacesWithSmile(frame);
        List<DetectedFace> detectedFaces = new ArrayList<>();

        for(Face detectedFace : allFaces){
            DetectedFace faceOnCurrentFrame = new DetectedFace(detectedFace);

            List<DetectedFace> similarFaces = findSimiliarFacesOnPreviousFrames(detectedFace);
            DetectedFace faceOnPreviousFrame = findNearestFaceFromPreviousFrames(similarFaces);

            if(faceOnPreviousFrame == null){
                faceOnCurrentFrame.setFaceStatus(getSimpleFaceStatus(detectedFace));
                faceOnCurrentFrame.setNewFace(true);
                faceOnCurrentFrame.setDetectionDate(new Date());

                if(FaceStatus.STARTED_SMILING.equals(faceOnCurrentFrame.getFaceStatus())){
                    faceOnCurrentFrame.setSmileStartDate(new Date());
                }
                LOGGER.debug("Found new face: {}", faceOnCurrentFrame);
            }
            else{
                FaceStatus status = getFaceStatusBasedOnPreviousFrame(detectedFace, faceOnPreviousFrame);
                faceOnCurrentFrame.setFaceStatus(status);
                faceOnCurrentFrame.setEffect(faceOnPreviousFrame.getEffect());
                faceOnCurrentFrame.setDetectionDate(faceOnPreviousFrame.getDetectionDate());

                computeSmileDate(faceOnCurrentFrame, faceOnPreviousFrame);

                if(FaceStatus.STARTED_SMILING.equals(status) || FaceStatus.STOPPED_SMILING.equals(status)
                        || !faceOnPreviousFrame.getFaceStatus().equals(status)){
                    faceOnCurrentFrame.setFrames(0);
                    faceOnCurrentFrame.setAlreadyDetected(false);
                }
                else{
                    faceOnCurrentFrame.setFrames(faceOnPreviousFrame.getFrames() + 1);
                    faceOnCurrentFrame.setAlreadyDetected(faceOnPreviousFrame.isAlreadyDetected());
                }
            }

            detectedFaces.add(faceOnCurrentFrame);

            if(isNewFace(faceOnCurrentFrame, similarFaces)){
                faceOnCurrentFrame.setAlreadyDetected(true);
                faceOnCurrentFrame.setNewSmile(true);
                newSmiles.add(detectedFace);
                LOGGER.debug("Found new smile: {}", faceOnCurrentFrame);
            }
        }

        SingleFrame newFrame = new SingleFrame(detectedFaces);
        insertNewFrame(newFrame);

        return detectedFaces;
    }

    private void computeSmileDate(DetectedFace faceOnCurrentFrame, DetectedFace faceOnPreviousFrame) {
        if(FaceStatus.STARTED_SMILING.equals(faceOnCurrentFrame.getFaceStatus())){
            faceOnCurrentFrame.setSmileStartDate(new Date());
        }
        else if(FaceStatus.SMILING.equals(faceOnCurrentFrame.getFaceStatus())){
            faceOnCurrentFrame.setSmileStartDate(faceOnPreviousFrame.getSmileStartDate());
        }
        else{
            faceOnCurrentFrame.setSmileStartDate(null);
        }
    }

    private boolean isNewFace(DetectedFace faceOnCurrentFrame, List<DetectedFace> similiarFaces) {
        boolean result = false;

        if(FaceStatus.SMILING.equals(faceOnCurrentFrame.getFaceStatus()) && faceOnCurrentFrame.getFrames() >= this.minSmilingFrames){
            result = !faceOnCurrentFrame.isAlreadyDetected();
        }
        return result;
    }

    private boolean isSmileTooLong(Date smileStartDate, FaceStatus faceStatus){
        boolean result = false;

        if(appConfig.getMaxSingleSmileTime() != null && appConfig.getMaxSingleSmileTime() > 0 && smileStartDate != null
                && FaceStatus.SMILING.equals(faceStatus)){
            long difference = new Date().getTime() -  smileStartDate.getTime();
            long diffSeconds = difference / 1000 % 60;

            result = diffSeconds > appConfig.getMaxSingleSmileTime();
        }

        return result;
    }

    private FaceStatus getFaceStatusBasedOnPreviousFrame(Face detectedFace, DetectedFace faceOnPreviousFrame) {
        FaceStatus statusForCurrentSmile = getSimpleFaceStatus(detectedFace);
        FaceStatus status = FaceStatus.NOT_SMILING;

        if(isSmileTooLong(faceOnPreviousFrame.getSmileStartDate(), faceOnPreviousFrame.getFaceStatus())){
            LOGGER.debug("Detected smile {} is too long (more than {} seconds). Setting as new smile...", detectedFace, appConfig.getMaxSingleSmileTime());
            status = FaceStatus.STARTED_SMILING;
        }
        else{
            switch(faceOnPreviousFrame.getFaceStatus()){
                case SMILING:
                case STARTED_SMILING:
                    if(FaceStatus.NOT_SMILING.equals(statusForCurrentSmile)){
                        status = FaceStatus.STOPPED_SMILING;
                    }
                    else{
                        status = FaceStatus.SMILING;
                    }
                    break;
                case STOPPED_SMILING:
                case NOT_SMILING:
                    if(FaceStatus.NOT_SMILING.equals(statusForCurrentSmile)){
                        status = FaceStatus.NOT_SMILING;
                    }
                    else{
                        status = FaceStatus.STARTED_SMILING;
                    }
                    break;
            }
        }
        return status;
    }

    private FaceStatus getSimpleFaceStatus(Face detectedFace) {
        FaceStatus status = FaceStatus.NOT_SMILING;
        Smile smile = detectedFace.getSmile();
        if(smile != null){
            if(smile.getConfidence() > 0.45){
                status = FaceStatus.STARTED_SMILING;
            }
        }
        return status;
    }

    private List<DetectedFace> findSimiliarFacesOnPreviousFrames(Face detectedFace){
        List<DetectedFace> similiarFaces = new ArrayList<>();

        for(SingleFrame previousFrame : previousFrames){
            DetectedFace faceOnPreviousFrame = previousFrame.getSimiliarFace(detectedFace);
            if(faceOnPreviousFrame != null){
               similiarFaces.add(faceOnPreviousFrame);
            }
        }

        return similiarFaces;
    }

    private DetectedFace findNearestFaceFromPreviousFrames(List<DetectedFace> similiarFaces) {
        return similiarFaces.size() > 0 ? similiarFaces.get(0) : null;
    }

    private DetectedFace findNearestSmilingFaceFromPreviousSnapshots(List<DetectedFace> similiarFaces) {
        DetectedFace smilingSimiliarFace = null;
        for(DetectedFace similiarFace : similiarFaces){
            if(FaceStatus.STARTED_SMILING.equals(similiarFace.getFaceStatus())
                    || FaceStatus.SMILING.equals(similiarFace.getFaceStatus())){
                smilingSimiliarFace = similiarFace;
                break;
            }
        }
        return smilingSimiliarFace;
    }

    private void insertNewFrame(SingleFrame frame){
        if(maxPreviousFrames > 0){
            previousFrames.add(0, frame);
            if(previousFrames.size() > maxPreviousFrames){
                previousFrames = previousFrames.subList(0, maxPreviousFrames - 1);
            }
        }
    }

    public void clear() {
        init();
    }
}
