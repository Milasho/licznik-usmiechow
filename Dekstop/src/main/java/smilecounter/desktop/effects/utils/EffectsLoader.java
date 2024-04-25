package smilecounter.desktop.effects.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smilecounter.core.utils.ResourcesLoader;
import smilecounter.desktop.effects.controllers.Effect;
import smilecounter.desktop.effects.controllers.encouraging.*;
import smilecounter.desktop.effects.controllers.rewarding.*;
import smilecounter.desktop.model.DetectedFace;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public enum EffectsLoader {
    BUTTERFLY("butterfly", EffectType.REWARDING, "effects/butterfly/1.png", "effects/butterfly/2.png", "effects/butterfly/3.png", "effects/butterfly/4.png"),
    HEART("heart", EffectType.ENCOURAGING,"effects/heart/love.png", "effects/heart/simple.png", "effects/heart/two-hearts.png"),
    BEARD("beard",EffectType.ENCOURAGING, "effects/beard/beard.png"),
    AFRO("afro", EffectType.ENCOURAGING, "effects/afro/afro.png"),
    MUSTACHE("mustache", EffectType.ENCOURAGING, "effects/mustache/mustache.png"),
    EMO("emo", EffectType.ENCOURAGING, "effects/emo/hair.png"),
    STAR("star", EffectType.REWARDING, "effects/star/star.png"),
    MOUTHS("mouths", EffectType.REWARDING, "effects/mouth/vampire.png", "effects/mouth/sexy.png", "effects/mouth/kissu.png"),
    HALO("halo", EffectType.ENCOURAGING, "effects/halo/1.png", "effects/halo/2.png", "effects/halo/3.png", "effects/halo/4.png", "effects/halo/5.png"),
    FACE_ROTATION("face-rotation", EffectType.REWARDING),
    CHAMPION("champion", EffectType.REWARDING),
    SMILE_EMOJI("smile-emoji", EffectType.REWARDING, "effects/smile-emoji/1.png", "effects/smile-emoji/2.png", "effects/smile-emoji/3.png", "effects/smile-emoji/2.png"),
    SHEEP("sheep", EffectType.REWARDING, "effects/sheep/1.png", "effects/sheep/2.png", "effects/sheep/3.png", "effects/sheep/2.png"),
    KITTY_CUTE("kitty-cute", EffectType.REWARDING, "effects/kitty/1.png", "effects/kitty/2.png", "effects/kitty/3.png", "effects/kitty/2.png");

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private BufferedImage [] images;
    private String name;
    private EffectType type;

    EffectsLoader(String name, EffectType type, String ... effectFiles) {
        this.images = new BufferedImage[effectFiles.length];
        this.name = name;
        this.type = type;

        for (int i = 0; i < effectFiles.length; i++) {
            String file = effectFiles[i];
            LOGGER.debug("Loading file {} for effect: {}", file, this);
            InputStream inputStream = ResourcesLoader.getInputStream(file, getClass());
            try {
                images[i] = ImageIO.read(inputStream);
            } catch (IOException e) {
                LOGGER.debug("Error during loading {}", file, e);
            }
        }
    }

    public BufferedImage getImage(){
        return getImageFrame(0);
    }

    public BufferedImage getImageFrame(int index){
        return images != null && images.length > index ? images[index] : null;
    }

    public int getFramesCount(){
        return images != null ? images.length : 0;
    }

    public static void initializeAllEffects(){
        for(EffectsLoader loader : EffectsLoader.values()){
            loader.getImage();
        }
    }

    public static EffectsLoader getEffectWithName(String name){
        for(EffectsLoader loader : EffectsLoader.values()){
            if(loader.name.equals(name)){
                return loader;
            }
        }
        return null;
    }

    public Effect createEffect(DetectedFace detectedFace) {
        Effect effect = null;
        switch (this) {
            case BUTTERFLY:
                effect = new ButterflyEffect(detectedFace);
                break;
            case HEART:
                effect = new GrowingHeartEffect(detectedFace);
                break;
            case BEARD:
                effect = new BeardEffect(detectedFace);
                break;
            case AFRO:
                effect = new AfroEffect(detectedFace);
                break;
            case MUSTACHE:
                effect = new MustacheEffect(detectedFace);
                break;
            case EMO:
                effect = new EmoEffect(detectedFace);
                break;
            case STAR:
                effect = new StarEffect(detectedFace);
                break;
            case MOUTHS:
                effect = new BigMouthsEffect(detectedFace);
                break;
            case HALO:
                effect = new HaloEffect(detectedFace);
                break;
            case FACE_ROTATION:
                effect = new FaceRotationEffect(detectedFace);
                break;
            case CHAMPION:
                effect = new ChampionEffect(detectedFace);
                break;
            case SMILE_EMOJI:
                effect = new SmileEmojiEffect(detectedFace);
                break;
            case SHEEP:
                effect = new SheepEffect(detectedFace);
                break;
            case KITTY_CUTE:
                effect = new KittyCuteEffect(detectedFace);
                break;
        }
        return effect;
    }

    public EffectType getType() {
        return type;
    }
}
