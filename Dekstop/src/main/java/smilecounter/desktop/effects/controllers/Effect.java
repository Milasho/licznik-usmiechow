package smilecounter.desktop.effects.controllers;

import smilecounter.core.affective.model.Face;
import smilecounter.desktop.model.DetectedFace;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Date;

public abstract class Effect {
    private DetectedFace face;
    private Date creationDate;

    public Effect(DetectedFace face){
        this.face = face;
        this.creationDate = new Date();
    }

    public abstract void displayEffectForFace(Graphics2D g2, BufferedImage image, Face updatedFace, Rectangle scaledImageSize);

    public abstract boolean effectEnded();

    public DetectedFace getFace() {
        return face;
    }

    public void setFace(DetectedFace face) {
        this.face = face;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
