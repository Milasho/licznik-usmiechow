package smilecounter.desktop.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smilecounter.core.affective.model.Face;
import smilecounter.desktop.config.ApplicationConfiguration;
import smilecounter.desktop.effects.controllers.Effect;
import smilecounter.desktop.effects.utils.EffectsContainer;
import smilecounter.desktop.model.DetectedFace;
import smilecounter.desktop.utils.CameraUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

@ApplicationScoped
public class EffectsManager {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Inject private EffectsContainer effects;
    @Inject private ApplicationConfiguration appConfig;

    public void handleEffects(List<DetectedFace> allFaces) {
        if(appConfig.isEnableEffects()){
            effects.handleEffectsForCurrentDisplayedFaces(allFaces);
        }
    }

    public void displayCurrentEffects(Graphics2D g2d, BufferedImage image, Rectangle scaledImageSize, Dimension dim) {
        if(appConfig.isEnableEffects()) {
            List<Effect> effects = this.effects.getEffects();

            if (effects != null && effects.size() > 0) {
                for (int i = effects.size() - 1; i >= 0; i--) {
                    Effect effect = effects.get(i);
                    if (effect.effectEnded()) {
                        LOGGER.debug("Removing effect {} for face {}", effect, effect.getFace());
                        effect.getFace().setEffect(null);
                        effects.remove(effect);
                    } else {
                        Face updatedFace = CameraUtils.updateFaceSize(effect.getFace(), scaledImageSize, dim);
                        effect.displayEffectForFace(g2d, image, updatedFace, scaledImageSize);
                    }
                }
            }
        }
    }

    public void clear() {
        effects.clear();
    }
}
