package smilecounter.desktop.model;

import smilecounter.core.affective.model.Face;

import java.util.List;

public class SingleFrame {
    private List<DetectedFace> facesOnFrame;

    public SingleFrame(List<DetectedFace> detectedFaces) {
        facesOnFrame = detectedFaces;
    }

    public DetectedFace getSimiliarFace(Face detectedFace) {
        DetectedFace result = null;
        for(DetectedFace face : facesOnFrame){
            Face f = face.getFace();
            if(areFacesSimiliar(f, detectedFace)){
                result = face;
                break;
            }
        }
        return result;
    }

    private boolean areFacesSimiliar(Face f1, Face f2){
        double dx = (f1.getWidth() + f2.getWidth()) / 8;
        double dy = (f1.getHeight() + f2.getHeight()) / 8;

        boolean xSimilarity = Math.abs(f1.getX() - f2.getX()) < dx
                && Math.abs(f1.getWidth() - f2.getWidth()) < dx;
        boolean ySimilarity = Math.abs(f1.getY() - f2.getY()) < dy
                && Math.abs(f1.getHeight() - f2.getHeight()) < dy;

        return xSimilarity && ySimilarity;
    }
}
