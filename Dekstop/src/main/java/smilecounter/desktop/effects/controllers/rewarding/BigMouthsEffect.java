package smilecounter.desktop.effects.controllers.rewarding;

import smilecounter.core.affective.model.Face;
import smilecounter.core.affective.model.Smile;
import smilecounter.core.data.model.FaceStatus;
import smilecounter.desktop.effects.controllers.Effect;
import smilecounter.desktop.effects.utils.EffectFilters;
import smilecounter.desktop.effects.utils.EffectsLoader;
import smilecounter.desktop.model.DetectedFace;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class BigMouthsEffect extends Effect {
    private static final EffectsLoader EFFECT = EffectsLoader.MOUTHS;
    private static BufferedImage frame;
    private static final int MOUTH_POSITION_ON_FRAME = 60;

    private boolean ended;
    private int y;
    private double scale;

    public BigMouthsEffect(DetectedFace face) {
        super(face);
        Random r = new Random();
        frame = EFFECT.getImageFrame(r.nextInt(EFFECT.getFramesCount()));
        ended = false;
    }

    @Override
    public void displayEffectForFace(Graphics2D g2, BufferedImage image, Face updatedFace, Rectangle scaledImageSize) {
        if(!ended && (FaceStatus.STARTED_SMILING.equals(getFace().getFaceStatus())
                || FaceStatus.SMILING.equals(getFace().getFaceStatus()))){
            Smile smile = updatedFace.getSmile();
            if(smile != null){
                scale = updatedFace.getWidth() * 0.5 / frame.getWidth();
                y = (int) (smile.getY() - MOUTH_POSITION_ON_FRAME * scale);
            }
            if(y > 0 && y < image.getHeight()){
                g2.drawImage(frame, EffectFilters.scale(scale), (int) (smile.getX() + smile.getWidth() / 2 - frame.getWidth() * scale / 2), y);
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
