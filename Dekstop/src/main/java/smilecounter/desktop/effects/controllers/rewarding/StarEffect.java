package smilecounter.desktop.effects.controllers.rewarding;

import smilecounter.core.affective.model.Face;
import smilecounter.desktop.effects.controllers.Effect;
import smilecounter.desktop.effects.utils.EffectFilters;
import smilecounter.desktop.effects.utils.EffectsLoader;
import smilecounter.desktop.model.DetectedFace;
import smilecounter.core.data.model.FaceStatus;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class StarEffect extends Effect {
    private static final EffectsLoader EFFECT = EffectsLoader.STAR;
    private static final long FRAME_RATE_MS = 200;
    private static final int STARS_COUNT = 5;

    private Date lastFrameDisplay;
    private List<StarElement> stars;
    private boolean shouldEndEffect;
    private  BufferedImage starImage;

    public StarEffect(DetectedFace face) {
        super(face);
        lastFrameDisplay = new Date();
    }

    private void initializeStars(Face updatedFace){
        stars = new ArrayList<>(STARS_COUNT);
        starImage = EFFECT.getImage();
        for(int i = 0; i < STARS_COUNT; i++){
            stars.add(new StarElement(updatedFace));
        }
    }

    @Override
    public void displayEffectForFace(Graphics2D g2, BufferedImage image, Face updatedFace, Rectangle scaledImageSize) {
        if(stars == null){
            initializeStars(updatedFace);
        }
        if (FaceStatus.SMILING.equals(getFace().getFaceStatus())) {
            boolean updateStars = shouldIncrementFrame();
            for (int i = 0; i < STARS_COUNT; i++) {
                StarElement star = stars.get(i);
                star.updateFace(updatedFace);
                if(updateStars){
                    star.makeMove();
                }
                g2.drawImage(starImage, EffectFilters.makeTransparent(star.getTransparency()), star.getX(), star.getY());
            }
        } else {
            shouldEndEffect = true;
        }
    }

    @Override
    public boolean effectEnded() {
        return shouldEndEffect;
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

    public class StarElement{
        private Face face;
        private int x, y;
        private Random random;
        private float transparency;
        private float yStep;
        private float alphaStep;

        StarElement(Face face){
            this.face = face;
            this.random = new Random();

            initialize();
        }

        private void initialize(){
            this.x = (int) (face.getX() + random.nextInt((int) face.getWidth()));
            this.y = (int) (face.getY() + random.nextInt((int) face.getHeight()));
            this.transparency = 1;
            this.alphaStep = (float) (0.05 * random.nextFloat() + 0.01);
            this.yStep = random.nextInt(5) + 1;
        }

        public int getY() {
            return y;
        }

        public int getX() {
            return x;
        }

        public float getTransparency() {
            return transparency;
        }

        public void makeMove() {
            this.y -= yStep;
            if(this.y < 0 || this.y < face.getY() - face.getHeight() / 5){
                alphaStep *= 1.5;
            }
            transparency -= alphaStep;

            if(transparency <= 0){
                initialize();
            }
        }

        public void updateFace(Face updatedFace) {
            this.face = updatedFace;
        }
    }
}
