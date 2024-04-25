package smilecounter.desktop.utils;

import smilecounter.core.affective.model.Face;
import smilecounter.core.affective.model.HumanFeature;
import smilecounter.core.affective.model.Smile;
import smilecounter.core.data.model.FaceStatus;
import smilecounter.desktop.config.ApplicationConfiguration;
import smilecounter.desktop.model.DetectedFace;

import java.awt.*;
import java.util.List;

public class CameraUtils {
    private ApplicationConfiguration appConfig = WeldUtils.getClassFromWeld(ApplicationConfiguration.class);

    private final Stroke stroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
            1.0f, new float[] { 1.0f }, 0.0f);

    public void drawFacesOnImage(List<DetectedFace> faces, Graphics2D g2d, Rectangle scaledImage, Dimension realImage){
        if(appConfig.isAffectiveShowDetectedFragments() && faces != null && g2d != null) {
            for(DetectedFace f : faces){
                Face face = updateFaceSize(f, scaledImage, realImage);
                Smile smile = face.getSmile();

                drawRectangleForFaceFeature(face, g2d, getColorForFace(f.getFaceStatus()));
                if(smile != null){
                    drawRectangleForFaceFeature(smile, g2d, getColorForSmile(smile.getConfidence()));
                }
            }
        }
    }

    public static Face updateFaceSize(DetectedFace f, Rectangle scaledImage, Dimension realImage){
        Face face = f.getFace();
        Smile smile = face.getSmile();
        double widthFactor = scaledImage.width / realImage.getWidth();
        double heightFactor = scaledImage.height / realImage.getHeight();

        Face newFace = new Face();
        newFace.setX(scaledImage.x + face.getX() * widthFactor);
        newFace.setY(scaledImage.y + face.getY() * heightFactor);
        newFace.setWidth(face.getWidth() * widthFactor);
        newFace.setHeight(face.getHeight() * heightFactor);
        if(smile != null){
            Smile newSmile = new Smile();
            newSmile.setConfidence(smile.getConfidence());
            newSmile.setX(scaledImage.x + smile.getX() * widthFactor);
            newSmile.setY(scaledImage.y + smile.getY() * heightFactor);
            newSmile.setWidth(smile.getWidth() * widthFactor);
            newSmile.setHeight(smile.getHeight() * heightFactor);
            newFace.setSmile(newSmile);
        }
        return newFace;
    }

    private void drawRectangleForFaceFeature(HumanFeature feature, Graphics2D g2d, Color color){
        int dx = (int) (0.1 * feature.getWidth());
        int dy = (int) (0.2 * feature.getHeight());
        int x = (int) feature.getX() - dx;
        int y = (int) feature.getY() - dy;
        int w = (int) feature.getWidth() + 2 * dx;
        int h = (int) feature.getHeight() + dy;
        g2d.setStroke(stroke);
        g2d.setColor(color);
        g2d.drawRect(x, y, w, h);
    }

    private Color getColorForFace(FaceStatus faceStatus) {
        switch(faceStatus){
            case NOT_SMILING:
                return Color.DARK_GRAY;
            case SMILING:
                return Color.MAGENTA;
            case STOPPED_SMILING:
                return Color.CYAN;
            default: return Color.WHITE;
        }
    }

    private Color getColorForSmile(double smileConfidence) {
        Color result = Color.RED;
        if (smileConfidence > 0.7) {
            result = Color.GREEN;
        }
        else if (smileConfidence > 0.5) {
            result = Color.YELLOW;
        }
        else if (smileConfidence > 0.3) {
            result = Color.ORANGE;
        }

        return result;
    }
}
