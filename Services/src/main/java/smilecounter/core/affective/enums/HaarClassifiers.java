package smilecounter.core.affective.enums;

import smilecounter.core.utils.ResourcesLoader;

import java.io.InputStream;

public enum HaarClassifiers {
    MOUTH("haar-classifiers/Mouth.xml"),
    SMILE("haar-classifiers/smile.xml"),
    FRONTAL_FACE("haar-classifiers/haarcascade_frontalface_alt.xml"),
    FRONTAL_FACE_LBP("haar-classifiers/lbpcascade_frontalface_improved.xml"),
    NOSE("haar-classifiers/Nose.xml");

    private String resourcesPath;

    HaarClassifiers(String resourcesPath) {
        this.resourcesPath = resourcesPath;
    }

    public String getResourcesPath(){
        return resourcesPath;
    }

    public String getExternalPath() throws Exception {
        return ResourcesLoader.getExternalResourcePath(resourcesPath, getClass());
    }

    public InputStream getInputStream(){
        return ResourcesLoader.getInputStream(resourcesPath, getClass());
    }
}
