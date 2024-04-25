package smilecounter.desktop.model;


import smilecounter.core.affective.model.Face;
import smilecounter.core.data.model.FaceStatus;
import smilecounter.desktop.effects.controllers.Effect;

import java.util.Date;

public class DetectedFace {
    private static final String TO_STRING_FORMAT = "%s -> [Detected on %s, Status: %s]";

    private Face face;
    private Effect effect;
    private FaceStatus faceStatus;
    private Date detectionDate;
    private Date smileStartDate;
    private boolean newFace;
    private boolean newSmile;
    private int frames;
    private boolean alreadyDetected;

    public DetectedFace(Face face) {
        this.face = face;
    }

    public DetectedFace(Face face, Effect effect, FaceStatus faceStatus) {
        this.face = face;
        this.effect = effect;
        this.faceStatus = faceStatus;
    }

    public Face getFace() {
        return face;
    }

    public void setFace(Face face) {
        this.face = face;
    }

    public Effect getEffect() {
        return effect;
    }

    public void setEffect(Effect effect) {
        this.effect = effect;
    }

    public FaceStatus getFaceStatus() {
        return faceStatus;
    }

    public void setFaceStatus(FaceStatus faceStatus) {
        this.faceStatus = faceStatus;
    }

    public Date getDetectionDate() {
        return detectionDate;
    }

    public void setDetectionDate(Date detectionDate) {
        this.detectionDate = detectionDate;
    }

    @Override
    public String toString(){
        return String.format(TO_STRING_FORMAT, face, detectionDate, faceStatus);
    }

    public boolean isNewFace() {
        return newFace;
    }

    public void setNewFace(boolean newFace) {
        this.newFace = newFace;
    }

    public boolean isNewSmile() {
        return newSmile;
    }

    public void setNewSmile(boolean newSmile) {
        this.newSmile = newSmile;
    }

    public int getFrames() {
        return frames;
    }

    public void setFrames(int frames) {
        this.frames = frames;
    }

    public void setAlreadyDetected(boolean alreadyDetected) {
        this.alreadyDetected = alreadyDetected;
    }

    public boolean isAlreadyDetected() {
        return alreadyDetected;
    }

    public Date getSmileStartDate() {
        return smileStartDate;
    }

    public void setSmileStartDate(Date smileStartDate) {
        this.smileStartDate = smileStartDate;
    }
}
