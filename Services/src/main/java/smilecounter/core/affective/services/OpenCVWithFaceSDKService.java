package smilecounter.core.affective.services;

import Luxand.FSDK;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import smilecounter.core.affective.enums.AffectiveServices;
import smilecounter.core.affective.interfaces.AffectiveService;
import smilecounter.core.affective.model.Face;
import smilecounter.core.affective.model.Smile;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class OpenCVWithFaceSDKService implements AffectiveService {
    @Inject private OpenCVService openCVService;
    @Inject private FaceSDKService faceSDKService;

    public void init(){
       openCVService.init();
       faceSDKService.init();
    }

    public void init(String libsPath){
       openCVService.init(libsPath);
       faceSDKService.init(libsPath);
    }

    public void initNewServices(){
        if(openCVService == null){
            openCVService = new OpenCVService();
        }
        if(faceSDKService == null){
            faceSDKService = new FaceSDKService();
        }
    }

    @Override
    public List<Face> detectFacesWithSmile(BufferedImage image) {
        List<Face> result = new ArrayList<>();

        Mat img = openCVService.convertImageToMat(image);
        if (img != null) {
            Mat grayImg = openCVService.sanitizeMat(img);
            MatOfRect faceDetections = openCVService.detectFaces(grayImg);
            for (Rect rect : faceDetections.toArray()) {
                Face face = new Face(rect.x, rect.y, rect.width, rect.height);
                Smile smile = getSmileFromFace(image, face);
                face.setSmile(smile);
                result.add(face);
            }
        }

        return result;
    }

    @Override
    public AffectiveServices geServiceData() {
        return AffectiveServices.CUSTOM;
    }

    private Smile getSmileFromFace(BufferedImage image, Face face){
        FSDK.HImage hImage = faceSDKService.getHImage(image);

        FSDK.TFacePosition facePosition = new FSDK.TFacePosition();
        facePosition.xc = (int) (face.getX() + face.getWidth() / 2);
        facePosition.yc = (int) (face.getY() + face.getHeight() / 2);
        facePosition.w = (int) face.getWidth();

        Smile smile = faceSDKService.getSmileFromFace(hImage, facePosition);
        FSDK.FreeImage(hImage);
        return smile;
    }

    public void setLuxandKey(String luxandKey) {
        faceSDKService.setKey(luxandKey);
    }
}
