package smilecounter.core.utils;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ImagesHelper {
    public static BufferedImage convertBase64ToBufferedImage(String base64) throws IOException {
        byte[] imageBytes = DatatypeConverter.parseBase64Binary(base64);
        return ImageIO.read(new ByteArrayInputStream(imageBytes));
    }

    public static String convertBufferedImageToBase64(BufferedImage image) throws IOException{
        String result = null;
        if(image != null){
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "png", Base64.getEncoder().wrap(os));
            result = os.toString(StandardCharsets.ISO_8859_1.name());
        }
        return result;
    }
}
