package smilecounter.desktop.effects.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smilecounter.desktop.config.ApplicationConfiguration;
import smilecounter.desktop.effects.controllers.Effect;
import smilecounter.desktop.model.DetectedFace;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@ApplicationScoped
public class EffectsContainer {
    @Inject private ApplicationConfiguration appConfig;
    private List<Effect> effects;
    private Random random = new Random();

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public List<Effect> getEffects() {
        return effects;
    }

    public void handleEffectsForCurrentDisplayedFaces(List<DetectedFace> allFaces) {
        effects = new ArrayList<>();
        for(DetectedFace detectedFace : allFaces){
            if(detectedFace.isNewSmile()){
                detectedFace.setEffect(createRandomEffectForFace(detectedFace));
            }
            else if(detectedFace.isNewFace() || detectedFace.getEffect() == null){
                detectedFace.setEffect(createEncouragingEffect(detectedFace));
            }

            if(detectedFace.getEffect() != null){
                effects.add(detectedFace.getEffect());
                detectedFace.getEffect().setFace(detectedFace);
            }
        }
    }

    private Effect createEncouragingEffect(DetectedFace detectedFace) {
        EffectsLoader randomEffect = getRandomEffect(EffectType.ENCOURAGING);
        Effect effect = null;
        if(randomEffect != null){
            effect = randomEffect.createEffect(detectedFace);
            LOGGER.debug("Creating encouraging effect {} for face {}...", effect, detectedFace);
        }
        return effect;
    }

    private Effect createRandomEffectForFace(DetectedFace face){
        EffectsLoader randomEffect = getRandomEffect(EffectType.REWARDING);
        Effect effect = null;
        if(randomEffect != null){
            effect = randomEffect.createEffect(face);
            LOGGER.debug("Creating rewarding effect {} for face {}...", effect, face);
        }
        return effect;
    }
    private EffectsLoader getRandomEffect(EffectType effectType){
        List<EffectsLoader> availableEffects = appConfig.getAvailableEffects().get(effectType);
        EffectsLoader randomEffect = null;
        if(availableEffects != null && availableEffects.size() > 0){
            int i = random.nextInt(availableEffects.size());
            randomEffect = availableEffects.get(i);
        }

        return randomEffect;
    }


    public void removeEffect(Effect effect) {
        effects.remove(effect);
    }

    public void clear() {
        effects = new ArrayList<>();
    }
}
