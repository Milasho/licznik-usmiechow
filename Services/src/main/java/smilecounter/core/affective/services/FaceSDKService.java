package smilecounter.core.affective.services;

import Luxand.FSDK;
import Luxand.FSDK.HImage;
import Luxand.FSDK.TFacePosition;
import Luxand.FSDK.TFaces;
import Luxand.FSDK.TPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smilecounter.core.affective.enums.AffectiveServices;
import smilecounter.core.affective.interfaces.AffectiveService;
import smilecounter.core.affective.model.Face;
import smilecounter.core.affective.model.Smile;
import smilecounter.core.affective.utils.AffectiveLibsLoader;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class FaceSDKService implements AffectiveService {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private boolean serviceLoaded = false;

    public void init(){
        serviceLoaded = AffectiveLibsLoader.loadLib(AffectiveServices.LUXAND);
    }

    public void init(String libsPath){
        serviceLoaded = AffectiveLibsLoader.loadLib(AffectiveServices.LUXAND, libsPath);
    }

    @PreDestroy
    public void destroy(){
        FSDK.Finalize();
    }

    @Override
    public List<Face> detectFacesWithSmile(BufferedImage image) {
        List<Face> result = new ArrayList<>();
        HImage imageHandle = getHImage(image);
        if(imageHandle != null){
            TFaces faces = detectFaces(imageHandle);

            if(faces.faces != null && faces.faces.length > 0){
                for(TFacePosition face : faces.faces){
                    Smile s = getSmileFromFace(imageHandle, face);

                    Face f = convertToFace(face);
                    f.setSmile(s);
                    result.add(f);
                }
            }

            FSDK.FreeImage(imageHandle);
        }

        return result;
    }

    @Override
    public AffectiveServices geServiceData() {
        return AffectiveServices.LUXAND;
    }

    public void setKey(String key){
        if(serviceLoaded) {
            if (FSDK.ActivateLibrary(key) != FSDK.FSDKE_OK) {
                LOGGER.error("Failed to initialize Luxand with given licence key!");
            }
            else{
                FSDK.Initialize();
                FSDK.SetFaceDetectionParameters(true, false, 384);
            }
        }
    }

    private FSDK.TFaces detectFaces(FSDK.HImage imageHandle){
        FSDK.TFaces faces = new FSDK.TFaces();
        FSDK.DetectMultipleFaces(imageHandle, faces);
        return faces;
    }

    private Face convertToFace(FSDK.TFacePosition face){
        Face f = new Face();
        int left = face.xc - face.w / 2;
        int top = face.yc - face.w / 2;
        f.setX(left);
        f.setY(top);
        f.setHeight(face.w);
        f.setWidth(face.w);
        return f;
    }

    public Smile getSmileFromFace(FSDK.HImage imageHandle, FSDK.TFacePosition face){
        FSDK.FSDK_Features.ByReference features = new FSDK.FSDK_Features.ByReference();
        FSDK.DetectFacialFeaturesInRegion(imageHandle, face, features);
        float smileConfidence = getSmileConfidence(imageHandle, features);

        Smile smile = getSmileFromFeatures(features);
        smile.setConfidence(smileConfidence);
        return smile;
    }

    public HImage getHImage(BufferedImage image){
        HImage imageHandle = null;
        if(image != null){
            imageHandle = new HImage();
            int result = FSDK.LoadImageFromAWTImage(imageHandle, image, FSDK.FSDK_IMAGEMODE.FSDK_IMAGE_COLOR_24BIT);
            if(result != FSDK.FSDKE_OK){
                LOGGER.error("Error during loading image from buffered image: {}", result);
                return null;
            }
        }

        return imageHandle;
    }

    public Smile getSmileFromFeatures(FSDK.FSDK_Features.ByReference features){
        TPoint a = features.features[FSDK.FSDKP_MOUTH_LEFT_TOP];
        TPoint b = features.features[FSDK.FSDKP_MOUTH_RIGHT_BOTTOM];

        Smile smile = new Smile();
        smile.setX(a.x);
        smile.setY(a.y);
        smile.setHeight(b.y - a.y);
        smile.setWidth(b.x - a.x);
        return smile;
    }

    public float getSmileConfidence(HImage imageHandle, FSDK.FSDK_Features.ByReference features){
        String [] attributes = new String[1];
        float [] confidence = new float[1];
        FSDK.DetectFacialAttributeUsingFeatures(imageHandle, features, "Expression", attributes, 64);
        FSDK.GetValueConfidence(attributes[0], "Smile", confidence);
        return confidence[0];
    }
}
