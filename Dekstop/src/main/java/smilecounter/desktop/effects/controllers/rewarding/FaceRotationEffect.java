package smilecounter.desktop.effects.controllers.rewarding;

import smilecounter.core.affective.model.Face;
import smilecounter.desktop.effects.controllers.Effect;
import smilecounter.desktop.effects.utils.EffectFilters;
import smilecounter.desktop.model.DetectedFace;

import java.awt.*;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class FaceRotationEffect extends Effect {
    private BufferedImage facePart;
    private int x, y, faceX, faceY;
    private double rotation, scale;
    private boolean shouldEnd;

    public FaceRotationEffect(DetectedFace face) {
        super(face);
        rotation = 0;
        scale = 1;
        shouldEnd = false;
    }

    @Override
    public void displayEffectForFace(Graphics2D g2, BufferedImage image, Face updatedFace, Rectangle scaledImageSize) {
        if(facePart == null){
            facePart = extractFacePartFromImage(image);
            x = (int) updatedFace.getX();
            y = (int) updatedFace.getY();
        }
        if(facePart != null){
            updateEffect(image);

            AffineTransformOp filter = EffectFilters.rotate(rotation, scale, facePart.getWidth(), facePart.getHeight());
            g2.drawImage(facePart, filter, normalize(x, 0, image.getWidth()), normalize(y, 0, image.getHeight()));
        }
    }

    private void updateEffect(BufferedImage image) {
        rotation = (rotation + 5) % 360;
        x += 5;
        y += 5;
        scale -= 0.05;

        if(scale < 0.1){
            shouldEnd = true;
        }
    }

    private BufferedImage extractFacePartFromImage(BufferedImage image) {
        Face face = getFace().getFace();
        faceX = (int) getFace().getFace().getX();
        faceY = (int) getFace().getFace().getY();
        int width = (int) face.getWidth();
        int height = (int) face.getHeight();
        BufferedImage subimageBufferedImage = null;
        try{
            subimageBufferedImage = image.getSubimage(normalize(faceX, 0, image.getWidth()), normalize(faceY, 0, image.getWidth()), width, height);
        }
        catch(Exception e){ }

        return subimageBufferedImage;
    }

    private int normalize(int amount, int min, int max){
        int result = amount;
        if(amount < min) {
            result = min;
        }
        if(amount > max){
            result = max;
        }
        return result;
    }

    @Override
    public boolean effectEnded() {
        return shouldEnd;
    }
}
