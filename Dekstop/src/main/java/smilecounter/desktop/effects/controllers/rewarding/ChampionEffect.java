package smilecounter.desktop.effects.controllers.rewarding;

import smilecounter.core.affective.model.Face;
import smilecounter.desktop.effects.controllers.Effect;
import smilecounter.desktop.model.DetectedFace;
import smilecounter.core.data.model.FaceStatus;
import smilecounter.desktop.screens.swing.common.LanguageManager;
import smilecounter.desktop.utils.WeldUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Date;

public class ChampionEffect extends Effect {
    private final static String TEXT = "effects.champion.text";

    private Date startedSmiling;
    private boolean shouldEndEffect;
    private String displayedText;

    public ChampionEffect(DetectedFace face) {
        super(face);
        startedSmiling = new Date();
        LanguageManager languageManager = WeldUtils.getClassFromWeld(LanguageManager.class);
        displayedText = languageManager.getLocale(TEXT);
    }

    @Override
    public void displayEffectForFace(Graphics2D g2, BufferedImage image, Face updatedFace, Rectangle scaledImageSize) {
        if(FaceStatus.STARTED_SMILING.equals(getFace().getFaceStatus())
                || FaceStatus.SMILING.equals(getFace().getFaceStatus())){
            Date now = new Date();
            Long seconds = secondsBetween(now, startedSmiling);
            char[] text = String.format(displayedText, seconds).toCharArray();
            g2.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(getColor(seconds));
            g2.setFont(new Font("SansSerif", Font.BOLD, getFontSize(seconds, updatedFace)));
            int x = (int) (updatedFace.getX());
            int y = (int) (updatedFace.getY() + updatedFace.getHeight());

            g2.drawChars(text, 0, text.length, x, y);
        }
        else{
            shouldEndEffect = true;
        }
    }

    private int getFontSize(Long seconds, Face updatedFace) {
        int fontSize = (int) Math.max(25 + seconds, 50);
        int length = fontSize * displayedText.length();
        double scale = updatedFace.getWidth() / length;
        return (int) (fontSize * scale);
    }

    private Paint getColor(Long seconds) {
        Paint paint;
        if(seconds < 5){
            paint = Color.GREEN;
        }
        else if(seconds < 10){
            paint = Color.YELLOW;
        }
        else if(seconds < 15){
            paint = Color.ORANGE;
        }
        else{
            paint = Color.RED;
        }
        return paint;
    }

    private Long secondsBetween(Date first, Date second){
        return Math.abs(second.getTime() - first.getTime())/1000;
    }

    @Override
    public boolean effectEnded() {
        return shouldEndEffect;
    }
}
