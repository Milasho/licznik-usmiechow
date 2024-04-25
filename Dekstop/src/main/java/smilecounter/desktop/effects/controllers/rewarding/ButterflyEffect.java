package smilecounter.desktop.effects.controllers.rewarding;

import smilecounter.core.affective.model.Face;
import smilecounter.core.affective.model.Smile;
import smilecounter.desktop.effects.controllers.Effect;
import smilecounter.desktop.effects.utils.EffectFilters;
import smilecounter.desktop.effects.utils.EffectsLoader;
import smilecounter.desktop.model.DetectedFace;
import smilecounter.core.data.model.FaceStatus;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Date;

public class ButterflyEffect extends Effect {
    private static final EffectsLoader EFFECT = EffectsLoader.BUTTERFLY;
    private static final long FRAME_RATE_MS = 200;

    private int frame;
    private Date lastFrameDisplay;

    private int x, y;
    private boolean shouldEndEffect;

    public ButterflyEffect(DetectedFace face) {
        super(face);
        frame = 0;
        lastFrameDisplay = new Date();
    }

    @Override
    public void displayEffectForFace(Graphics2D g2, BufferedImage image, Face updatedFace, Rectangle scaledImageSize) {
        BufferedImage frame = EFFECT.getImageFrame(this.frame);

        if(frame != null){
            Smile smile = updatedFace.getSmile();

            if(FaceStatus.STARTED_SMILING.equals(getFace().getFaceStatus())
                    || FaceStatus.SMILING.equals(getFace().getFaceStatus())){
                if(smile != null){
                    double scale = computeScaleForEffect(updatedFace);

                    x = (int) (smile.getX() - frame.getWidth() * scale * 0.90);
                    y = (int) (smile.getY() - frame.getHeight() * scale * 0.75);
                    g2.drawImage(frame, EffectFilters.scale(scale), x, y);
                }
            }
            else{
                shouldEndEffect = true;
            }
        }

        if(shouldIncrementFrame()){
            incrementFrame();
        }
    }

    @Override
    public boolean effectEnded() {
        return shouldEndEffect;
    }

    private double computeScaleForEffect(Face updatedFace) {
        double faceWidth = updatedFace.getWidth();
        return faceWidth / (4.0 * EFFECT.getImage().getWidth());
    }

    private boolean shouldIncrementFrame() {
        Date now = new Date();
        boolean result = false;
        if(now.getTime() - this.lastFrameDisplay.getTime() > FRAME_RATE_MS){
            this.lastFrameDisplay = now;
            result = true;
        }
        return result;
    }

    private void incrementFrame(){
        int framesCount = EFFECT.getFramesCount();
        frame = (frame + 1) % framesCount;
    }
}
