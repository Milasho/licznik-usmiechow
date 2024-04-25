package smilecounter.desktop.effects.controllers.encouraging;

import smilecounter.core.affective.model.Face;
import smilecounter.desktop.effects.controllers.Effect;
import smilecounter.desktop.effects.utils.EffectFilters;
import smilecounter.desktop.effects.utils.EffectsLoader;
import smilecounter.desktop.model.DetectedFace;
import smilecounter.core.data.model.FaceStatus;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Date;

public class HaloEffect extends Effect {
    private static final EffectsLoader EFFECT = EffectsLoader.HALO;
    private static final long FRAME_RATE_MS = 200;

    private int frame;
    private Date lastFrameDisplay;

    private int x, y;
    private boolean ended;

    public HaloEffect(DetectedFace face) {
        super(face);
        frame = 0;
        lastFrameDisplay = new Date();
    }

    @Override
    public void displayEffectForFace(Graphics2D g2, BufferedImage image, Face updatedFace, Rectangle scaledImageSize) {
        BufferedImage frame = EFFECT.getImageFrame(this.frame);

        if(frame != null){
            if(!ended && (FaceStatus.STOPPED_SMILING.equals(getFace().getFaceStatus())
                    || FaceStatus.NOT_SMILING.equals(getFace().getFaceStatus()))){

                double scale = updatedFace.getWidth() / frame.getWidth();

                x = (int) (updatedFace.getX() + updatedFace.getWidth() / 2 - frame.getWidth() * scale / 2);
                y = (int) (updatedFace.getY() - frame.getHeight());
                g2.drawImage(frame, EffectFilters.scale(scale), x, y);
            }
            else{
                ended = true;
            }
        }

        if(shouldIncrementFrame()){
            incrementFrame();
        }
    }

    @Override
    public boolean effectEnded() {
        return ended;
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
