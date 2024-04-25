package smilecounter.desktop.services;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smilecounter.core.affective.enums.AffectiveServices;
import smilecounter.core.affective.services.FaceSDKService;
import smilecounter.core.affective.services.OpenCVService;
import smilecounter.core.affective.services.OpenCVWithFaceSDKService;
import smilecounter.core.affective.services.OpenIMAJService;
import smilecounter.core.affective.utils.AffectiveLibsLoader;
import smilecounter.desktop.config.ApplicationConfiguration;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class AffectiveService {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Inject private ApplicationConfiguration appConfig;

    @Inject private OpenCVService openCV;
    @Inject private FaceSDKService faceSDK;
    @Inject private OpenIMAJService openIMAJ;
    @Inject private OpenCVWithFaceSDKService openCVwithFaceSDK;

    private smilecounter.core.affective.interfaces.AffectiveService selectedService;

    @PostConstruct
    public void init(){
        setService(AffectiveServices.LUXAND);
    }

    public smilecounter.core.affective.interfaces.AffectiveService getService(){
        return selectedService;
    }

    public void setService(AffectiveServices service){
        LOGGER.debug("setService - {}", service);
        selectedService = preloadService(service);
    }

    public void setService(String service){
        for(AffectiveServices affectiveService : AffectiveServices.getAllServices()){
            if(affectiveService.getName().equals(service)){
                setService(affectiveService);
            }
        }
    }

    public void preloadServices(){
        LOGGER.debug("Preloading all services...");
        preloadService(AffectiveServices.OPEN_CV);
        preloadService(AffectiveServices.LUXAND);
        preloadService(AffectiveServices.CUSTOM);
    }

    private smilecounter.core.affective.interfaces.AffectiveService preloadService(AffectiveServices service){
        smilecounter.core.affective.interfaces.AffectiveService result = getAffectiveService(service);

        if(!AffectiveLibsLoader.isLibProperlyLoaded(service)){
            LOGGER.debug("Service {} needs preloading!", service);
            String libsPath = appConfig.getAffectiveLibsPath();
            if(StringUtils.isNotEmpty(libsPath)) {
                result.init(libsPath);
            }
            else{
                result.init();
            }
        }

        return result;
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
                faceSDK.setKey(appConfig.getLuxandKey());
                break;
            case CUSTOM:
                result = openCVwithFaceSDK;
                openCVwithFaceSDK.setLuxandKey(appConfig.getLuxandKey());
                break;
        }
        return result;
    }

    public boolean isServiceSelected(String serviceName) {
        return getService().geServiceData().getName().equals(serviceName);
    }
}
