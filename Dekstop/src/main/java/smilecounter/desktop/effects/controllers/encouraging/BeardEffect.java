package smilecounter.desktop.effects.controllers.encouraging;

import smilecounter.core.affective.model.Face;
import smilecounter.core.affective.model.Smile;
import smilecounter.desktop.effects.controllers.Effect;
import smilecounter.desktop.effects.utils.EffectFilters;
import smilecounter.desktop.effects.utils.EffectsLoader;
import smilecounter.desktop.model.DetectedFace;
import smilecounter.core.data.model.FaceStatus;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BeardEffect extends Effect {
    private static final EffectsLoader EFFECT = EffectsLoader.BEARD;
    private static BufferedImage frame = EFFECT.getImage();
    private static final int MOUTH_POSITION_ON_FRAME = 60;

    private boolean ended;
    private int y;
    private double scale;

    public BeardEffect(DetectedFace face) {
        super(face);
        ended = false;
    }

    @Override
    public void displayEffectForFace(Graphics2D g2, BufferedImage image, Face updatedFace, Rectangle scaledImageSize) {
        if(!ended && (FaceStatus.STOPPED_SMILING.equals(getFace().getFaceStatus())
                || FaceStatus.NOT_SMILING.equals(getFace().getFaceStatus()))){
            Smile smile = updatedFace.getSmile();
            if(smile != null){
                scale = updatedFace.getWidth() / frame.getWidth();
                y = (int) (smile.getY() - MOUTH_POSITION_ON_FRAME * scale);
            }
            if(y > 0 && y < image.getHeight()){
                g2.drawImage(frame, EffectFilters.scale(scale), (int) updatedFace.getX(), y);
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
