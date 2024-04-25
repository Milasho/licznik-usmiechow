package smilecounter.core.affective.interfaces;

import smilecounter.core.affective.enums.AffectiveServices;
import smilecounter.core.affective.model.Face;
import smilecounter.core.utils.ImagesHelper;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public interface AffectiveService {
    List<Face> detectFacesWithSmile(BufferedImage image);
    // Default methods
    default List<Face> detectFacesWithSmile(String base64) throws IOException {
        BufferedImage image = ImagesHelper.convertBase64ToBufferedImage(base64);
        return detectFacesWithSmile(image);
    }
    void init();
    default void init(String libsPath){
        init();
    }
    AffectiveServices geServiceData();
}
