package smilecounter.desktop.effects.controllers.encouraging;

import smilecounter.core.affective.model.Face;
import smilecounter.core.data.model.FaceStatus;
import smilecounter.desktop.effects.controllers.Effect;
import smilecounter.desktop.effects.utils.EffectFilters;
import smilecounter.desktop.effects.utils.EffectsLoader;
import smilecounter.desktop.model.DetectedFace;

import java.awt.*;
import java.awt.image.BufferedImage;

public class AfroEffect extends Effect {
    private static final EffectsLoader EFFECT = EffectsLoader.AFRO;
    private static BufferedImage frame = EFFECT.getImage();

    private boolean ended;
    private int y;
    private double scale;

    public AfroEffect(DetectedFace face) {
        super(face);
        ended = false;
    }

    @Override
    public void displayEffectForFace(Graphics2D g2, BufferedImage image, Face updatedFace, Rectangle scaledImageSize) {
        if(!ended && (FaceStatus.STOPPED_SMILING.equals(getFace().getFaceStatus())
                || FaceStatus.NOT_SMILING.equals(getFace().getFaceStatus()))){
            scale = updatedFace.getWidth() * 1.3 / frame.getWidth();
            y = (int)(updatedFace.getY() - updatedFace.getHeight() * 0.4);
            if(y < image.getHeight()){
                g2.drawImage(frame, EffectFilters.scale(scale), (int) (updatedFace.getX() - updatedFace.getWidth() / 6), y);
            }
        }
        else{
            ended = true;
        }
    }

    @Override
    public boolean effectEnded() {
        return ended;
    }
}
