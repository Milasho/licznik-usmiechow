package smilecounter.desktop.effects.controllers.encouraging;

import smilecounter.core.affective.model.Face;
import smilecounter.desktop.effects.controllers.Effect;
import smilecounter.desktop.effects.utils.EffectFilters;
import smilecounter.desktop.effects.utils.EffectsLoader;
import smilecounter.desktop.model.DetectedFace;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Date;

public class GrowingHeartEffect extends Effect {
    private static final EffectsLoader EFFECT = EffectsLoader.HEART;
    private static final long HEAR_DISPLAY_RATE_MS = 3000;
    private static final double MAX_SCALE = 0.4;
    private static final double SCALE_STEP = 0.01;

    private int displayedHearts;
    private double[] scales;

    private boolean ended;
    private double d;

    private Date lastFrameDisplay;

    public GrowingHeartEffect(DetectedFace face) {
        super(face);

        scales = new double [EFFECT.getFramesCount()];
        for(int i = 0; i < scales.length; i++){
            scales[i] = 0.20;
        }

        displayedHearts = 0;

        ended = false;
        lastFrameDisplay = new Date();
        d = 0.02;
    }

    @Override
    public void displayEffectForFace(Graphics2D g2, BufferedImage image, Face updatedFace, Rectangle scaledImageSize) {
        for(int i = 0; i < displayedHearts; i++){
            displayHeart(g2, updatedFace, i);
        }

        if(shouldIncrementHearts() && displayedHearts < EFFECT.getFramesCount()){
            displayedHearts++;
        }

        d = -d;
    }

    private void displayHeart(Graphics2D g2, Face face, int heart){
        BufferedImage frame = EFFECT.getImageFrame(heart);
        if(frame != null){
            double scale = computeScale(heart) * face.getWidth() / frame.getWidth() ;
            int x = getXPositionForHeart(heart, face, frame, scale);
            int y =  getYPositionForHeart(heart, face, frame, scale);
            g2.drawImage(frame, EffectFilters.scale(scale), x, y);
        }
    }

    private int getXPositionForHeart(int frame, Face face, BufferedImage frameImage, double scale){
        int ret;
        switch(frame){
            case 1:
                ret = (int) (face.getX() - frameImage.getWidth() * scale / 2);
            break;
            case 2:
                ret = (int) (face.getX() + face.getWidth() - frameImage.getWidth() * scale / 4);
            break;
            default:
                ret = (int) (face.getX() - frameImage.getWidth() * scale / 2);
        }
        return ret;
    }

    private int getYPositionForHeart(int frame, Face face, BufferedImage frameImage, double scale){
        int ret;
        switch(frame){
            case 1:
                ret = (int) (face.getY() - frameImage.getHeight() * scale / 2);
                break;
            case 2:
                ret = (int) (face.getY() - frameImage.getHeight() * scale / 2);
                break;
            default:
                ret = (int) (face.getY() + face.getHeight() - frameImage.getHeight() * scale / 2);
        }
        return ret;
    }

    private double computeScale(int i) {
        scales[i] += SCALE_STEP;
        if( scales[i] >= MAX_SCALE){
            scales[i] = MAX_SCALE;
        }

        return scales[i] + d;
    }

    private boolean shouldIncrementHearts() {
        Date now = new Date();
        boolean result = false;
        if(now.getTime() - this.lastFrameDisplay.getTime() > HEAR_DISPLAY_RATE_MS){
            this.lastFrameDisplay = now;
            result = true;
        }
        return result;
    }

    @Override
    public boolean effectEnded() {
        return ended;
    }
}
