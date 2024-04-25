package smilecounter.web.services;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smilecounter.core.affective.enums.AffectiveServices;
import smilecounter.core.affective.model.Face;
import smilecounter.core.affective.model.Smile;
import smilecounter.core.affective.services.FaceSDKService;
import smilecounter.core.affective.services.OpenCVService;
import smilecounter.core.affective.services.OpenCVWithFaceSDKService;
import smilecounter.core.affective.services.OpenIMAJService;
import smilecounter.core.affective.utils.AffectiveLibsLoader;
import smilecounter.web.config.ApplicationConfiguration;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class AffectiveService {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Inject private ApplicationConfiguration appConfig;

    @Inject private OpenCVService openCV;
    @Inject private FaceSDKService faceSDK;
    @Inject private OpenIMAJService openIMAJ;
    @Inject private OpenCVWithFaceSDKService openCVwithFaceSDK;

    @PostConstruct
    public void preloadServices(){
        LOGGER.debug("Preloading all services...");
        preloadService(AffectiveServices.OPEN_CV);
        preloadService(AffectiveServices.LUXAND);
        preloadService(AffectiveServices.CUSTOM);

        faceSDK.setKey(appConfig.getLuxandKey());
        openCVwithFaceSDK.setLuxandKey(appConfig.getLuxandKey());
    }

    private void preloadService(AffectiveServices service){
        smilecounter.core.affective.interfaces.AffectiveService detector = getAffectiveService(service);

        if(!AffectiveLibsLoader.isLibProperlyLoaded(service)){
            LOGGER.debug("Preloading service {}!", service);
            String libsPath = appConfig.getAffectiveLibsPath();
            if(StringUtils.isNotEmpty(libsPath)) {
                detector.init(libsPath);
            }
            else{
                detector.init();
            }
        }
    }

    public List<Face> detectFaces(String base64, AffectiveServices service){
        if(service == null){
            service = AffectiveServices.LUXAND;
        }

        smilecounter.core.affective.interfaces.AffectiveService detector = getAffectiveService(service);
        List<Face> detectedFaces = null;
        try {
            detectedFaces = detector.detectFacesWithSmile(base64);
        } catch (IOException e) {
           LOGGER.error("Error during detecting faces on {} with service {}.", base64, service, e);
        }

        return filterNotSmilingFaces(detectedFaces);
    }

    private List<Face> filterNotSmilingFaces(List<Face> allFaces){
        List<Face> smilingFaces = new ArrayList<>();
        if(allFaces != null){
            for(Face face : allFaces){
                Smile smile = face.getSmile();
                if(smile != null && smile.getConfidence() > 0.45){
                    smilingFaces.add(face);
                }
            }
        }
        return smilingFaces;
    }

    private smilecounter.core.affective.interfaces.AffectiveService getAffectiveService(AffectiveServices service) {
        smilecounter.core.affective.interfaces.AffectiveService result = null;
        switch(service){
            case OPENIMAJ:
                result = openIMAJ;
                break;
            case OPEN_CV:
                result = openCV;
                break;
            case LUXAND:
                result = faceSDK;
                break;
            case CUSTOM:
                result = openCVwithFaceSDK;
                break;
        }
        return result;
    }
}
