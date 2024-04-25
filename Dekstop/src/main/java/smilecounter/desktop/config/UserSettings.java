package smilecounter.desktop.config;

import com.github.sarxos.webcam.Webcam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserSettings {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private boolean permissionToSave;
    private boolean fullscreenMode;
    private String locationName;
    private Webcam camera;
    private String selectedLanguage;
    private boolean showStatistics;

    @PostConstruct
    public void init(){
        setCamera(Webcam.getDefault());
        setSelectedLanguage("en");
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        LOGGER.debug("setLocationName - {}", locationName);
        this.locationName = locationName;
    }

    public boolean isPermissionToSave() {
        return permissionToSave;
    }

    public void setPermissionToSave(boolean permissionToSave) {
        LOGGER.debug("setPermissionToSave - {}", permissionToSave);
        this.permissionToSave = permissionToSave;
    }

    public Webcam getCamera() {
        return camera;
    }

    public void setCamera(Webcam camera) {
        LOGGER.debug("setCamera - {}", camera);
        this.camera = camera;
    }

    public String getSelectedLanguage() {
        return selectedLanguage;
    }

    public void setSelectedLanguage(String selectedLanguage) {
        this.selectedLanguage = selectedLanguage;
    }

    public boolean isFullscreenMode() {
        return fullscreenMode;
    }

    public void setFullscreenMode(boolean fullscreenMode) {
        LOGGER.debug("Set fullscreen mode: {}", fullscreenMode);
        this.fullscreenMode = fullscreenMode;
    }

    public boolean isShowStatistics() {
        return showStatistics;
    }

    public void setShowStatistics(boolean showStatistics) {
        LOGGER.debug("Show statistics on webcam view: {}", showStatistics);
        this.showStatistics = showStatistics;
    }
}
