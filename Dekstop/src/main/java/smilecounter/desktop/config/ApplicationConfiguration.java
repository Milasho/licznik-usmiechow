package smilecounter.desktop.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smilecounter.core.utils.ResourcesLoader;
import smilecounter.desktop.effects.utils.EffectType;
import smilecounter.desktop.effects.utils.EffectsLoader;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.util.*;

@ApplicationScoped
public class ApplicationConfiguration {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final static String PROPERTIES_FILE_RESOURCE = "config/application-configuration.properties";
    private final static String PROPERTIES_LIST_SEPARATOR = ",";
    private final static String LOGGER_PATTERN = "%d{yyyy-MM-dd'T'HH:mm:ss.SSS} %-5p [%c] - %m%n";

    @Inject private UserSettings userSettings;

    private String applicationTitle;
    private String[] availableLanguages;
    private String defaultLanguage;
    private boolean databaseSimpleType;
    private boolean databaseStoreFaces;
    private String databaseConnectionUrl;
    private String defaultAffectiveService;
    private Integer databaseRefreshDataInterval;
    private String luxandKey;
    private String affectiveLibsPath;
    private float smileConfidenceTreshold;
    private boolean debugMode = false;
    private boolean affectiveShowDetectedFragments = false;
    private Map<EffectType, List<EffectsLoader>> availableEffects;
    private boolean enableEffects;
    private Integer smilingReminderInterval;
    private Integer maxSingleSmileTime;

    public ApplicationConfiguration() {
    }

    @PostConstruct
    public void init(){
        LOGGER.debug("Initializing application configuration...");
        Properties config = null;
        try {
            config = ResourcesLoader.getPropertiesFile(PROPERTIES_FILE_RESOURCE, this.getClass());
        } catch (IOException e) {
            LOGGER.error("Error during loading parameters from file {}: ", PROPERTIES_FILE_RESOURCE, e);
        }

        initFromProperties(config);
    }

    public void initFromFile(String path){
        LOGGER.debug("Initializing application from file: {}", path);
        Properties config = null;
        try {
            config = ResourcesLoader.getPropertiesFromExternalFile(path);
        } catch (IOException e) {
            LOGGER.error("Error during loading parameters from file {}: ", path, e);
        }
        initFromProperties(config);
    }

    private void initFromProperties(Properties config){
        String appTitle = config.getProperty(ApplicationParameters.PARAM_APPLICATION_TITLE);
        if(StringUtils.isNotEmpty(appTitle)){
            setApplicationTitle(appTitle);
        }

        String defaultLanguage = config.getProperty(ApplicationParameters.PARAM_DEFAULT_LANGUAGE);
        if(StringUtils.isNotEmpty(defaultLanguage)){
            setDefaultLanguage(defaultLanguage);
        }

        String availableLanguagesString = config.getProperty(ApplicationParameters.PARAM_AVAILABLE_LANGUAGES);
        if(StringUtils.isNotEmpty(availableLanguagesString)){
            setAvailableLanguages(availableLanguagesString.split(PROPERTIES_LIST_SEPARATOR));
        }

        String connectionUrl = config.getProperty(ApplicationParameters.PARAM_DATABASE_CONNECTION_URL);
        if(StringUtils.isNotEmpty(connectionUrl)){
            setDatabaseConnectionUrl(connectionUrl);
        }

        String refreshDataInterval = config.getProperty(ApplicationParameters.PARAM_REFRESH_DATA_INTERVAL);
        if(StringUtils.isNotEmpty(refreshDataInterval)){
            setDatabaseRefreshDataInterval( Integer.parseInt(refreshDataInterval) * 1000);
        }

        String debugMode = config.getProperty(ApplicationParameters.PARAM_DEBUG_MODE);
        if(StringUtils.isNotEmpty(debugMode)){
            setDebugMode(Boolean.TRUE.equals(Boolean.parseBoolean(debugMode)));
        }

        String affectiveService = config.getProperty(ApplicationParameters.PARAM_DEFAULT_AFFECTIVE_SERVICE);
        if(StringUtils.isNotEmpty(affectiveService)){
            setDefaultAffectiveService(affectiveService);
        }

        String luxandKey = config.getProperty(ApplicationParameters.PARAM_AFFECTIVE_LUXAND_KEY);
        if(StringUtils.isNotEmpty(luxandKey)){
            setLuxandKey(luxandKey);
        }

        String affectiveLibsPath = config.getProperty(ApplicationParameters.PARAM_AFFECTIVE_LIBS_PATH);
        if(StringUtils.isNotEmpty(affectiveLibsPath)){
            setAffectiveLibsPath(affectiveLibsPath);
        }

        String smileConfidenceTreshold = config.getProperty(ApplicationParameters.PARAM_AFFECTIVE_SMILE_CONFIDENCE_TRESHOLD);
        if(StringUtils.isNotEmpty(smileConfidenceTreshold)){
            setSmileConfidenceTreshold(Float.parseFloat(smileConfidenceTreshold));
        }

        String affectiveShowDetectedFragments = config.getProperty(ApplicationParameters.PARAM_AFFECTIVE_SHOW_DETECTED_FRAGMENTS);
        if(StringUtils.isNotEmpty(affectiveShowDetectedFragments)){
            setAffectiveShowDetectedFragments(Boolean.TRUE.equals(Boolean.parseBoolean(affectiveShowDetectedFragments)));
        }

        String databaseSimpleType = config.getProperty(ApplicationParameters.PARAM_DATABASE_SIMPLE_TYPE);
        if(StringUtils.isNotEmpty(databaseSimpleType)){
            setDatabaseSimpleType(Boolean.TRUE.equals(Boolean.parseBoolean(databaseSimpleType)));
        }

        String databaseStoreFaces = config.getProperty(ApplicationParameters.PARAM_DATABASE_STORE_FACES);
        if(StringUtils.isNotEmpty(databaseStoreFaces)){
            setDatabaseStoreFaces(Boolean.TRUE.equals(Boolean.parseBoolean(databaseStoreFaces)));
        }

        String fullscreenMode = config.getProperty(ApplicationParameters.PARAM_SETTING_FULLSCREEN_MODE);
        if(StringUtils.isNotEmpty(fullscreenMode)){
            userSettings.setFullscreenMode(Boolean.TRUE.equals(Boolean.parseBoolean(fullscreenMode)));
        }

        String locationName = config.getProperty(ApplicationParameters.PARAM_SETTING_LOCATION_NAME);
        if(StringUtils.isNotEmpty(locationName)){
            userSettings.setLocationName(locationName);
        }

        String enableEffects = config.getProperty(ApplicationParameters.PARAM_EFFECTS_ENABLED);
        if(StringUtils.isNotEmpty(enableEffects)){
            setEnableEffects(Boolean.TRUE.equals(Boolean.parseBoolean(enableEffects)));
        }

        String effectsList = config.getProperty(ApplicationParameters.PARAM_EFFECTS_LIST);
        if(StringUtils.isNotEmpty(effectsList)){
           setAvailableEffects(effectsList);
        }

        String smilingReminderInterval = config.getProperty(ApplicationParameters.PARAM_SMILING_REMINDER_INTERVAL);
        if(StringUtils.isNotEmpty(smilingReminderInterval)){
            setSmilingReminderInterval(Integer.parseInt(smilingReminderInterval) * 1000);
        }

        String showStats = config.getProperty(ApplicationParameters.PARAM_SETTING_SHOW_STATISTICS);
        if(StringUtils.isNotEmpty(showStats)){
            userSettings.setShowStatistics(Boolean.TRUE.equals(Boolean.parseBoolean(showStats)));
        }

        String maxSingleSmileTime = config.getProperty(ApplicationParameters.PARAM_MAX_SINGLE_SMILE_TIME);
        if(StringUtils.isNotEmpty(maxSingleSmileTime)){
            setMaxSingleSmileTime(Integer.parseInt(maxSingleSmileTime));
        }
    }

    public void setUpLogger(String filePath){
        LOGGER.debug("Creating log file on {}", filePath);
        FileAppender fa = new FileAppender();
        fa.setName("FILE");
        fa.setFile(filePath);
        fa.setLayout(new PatternLayout(LOGGER_PATTERN));
        fa.setThreshold(Level.DEBUG);
        fa.setAppend(false);
        fa.setImmediateFlush(true);
        fa.activateOptions();

        org.apache.log4j.Logger.getLogger("smilecounter").addAppender(fa);
    }

    public String getApplicationTitle() {
        return applicationTitle;
    }

    public void setApplicationTitle(String applicationTitle) {
        LOGGER.debug("setApplicationTitle - {}", applicationTitle);
        this.applicationTitle = applicationTitle;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        LOGGER.debug("setDefaultLanguage - {}", defaultLanguage);
        this.defaultLanguage = defaultLanguage;
    }

    public String[] getAvailableLanguages() {
        return availableLanguages;
    }

    public void setAvailableLanguages(String[] availableLanguages) {
        LOGGER.debug("setAvailableLanguages - {}", StringUtils.join(availableLanguages, ", "));
        this.availableLanguages = availableLanguages;
    }

    public Integer getDatabaseRefreshDataInterval() {
        return databaseRefreshDataInterval;
    }

    public void setDatabaseRefreshDataInterval(Integer databaseRefreshDataInterval) {
        LOGGER.debug("setDatabaseRefreshDataInterval - {}", databaseRefreshDataInterval);
        this.databaseRefreshDataInterval = databaseRefreshDataInterval;
    }

    public void setDebugMode(boolean debugMode){
        LOGGER.debug("setDebugMode - {}", debugMode);
        this.debugMode = debugMode;
    }

    public boolean isDebugMode(){
        return debugMode;
    }

    public String getDefaultAffectiveService() {
        return defaultAffectiveService;
    }

    public void setDefaultAffectiveService(String defaultAffectiveService) {
        LOGGER.debug("setDefaultAffectiveService - {}", defaultAffectiveService);
        this.defaultAffectiveService = defaultAffectiveService;
    }

    public String getLuxandKey() {
        return luxandKey;
    }

    public void setLuxandKey(String luxandKey) {
        LOGGER.debug("setLuxandKey - size ({})", luxandKey != null ? luxandKey.length() : 0);
        this.luxandKey = luxandKey;
    }

    public String getAffectiveLibsPath() {
        return affectiveLibsPath;
    }

    public void setAffectiveLibsPath(String affectiveLibsPath) {
        LOGGER.debug("setAffectiveLibsPath - {}", affectiveLibsPath);
        this.affectiveLibsPath = affectiveLibsPath;
    }

    public float getSmileConfidenceTreshold() {
        return smileConfidenceTreshold;
    }

    public void setSmileConfidenceTreshold(float smileConfidenceTreshold) {
        LOGGER.debug("setSmileConfidenceTreshold - {}", smileConfidenceTreshold);
        this.smileConfidenceTreshold = smileConfidenceTreshold;
    }

    public boolean isAffectiveShowDetectedFragments() {
        return affectiveShowDetectedFragments;
    }

    public void setAffectiveShowDetectedFragments(boolean affectiveShowDetectedFragments) {
        LOGGER.debug("setAffectiveShowDetectedFragments - {}", affectiveShowDetectedFragments);
        this.affectiveShowDetectedFragments = affectiveShowDetectedFragments;
    }

    public boolean isDatabaseSimpleType() {
        return databaseSimpleType;
    }

    public void setDatabaseSimpleType(boolean databaseSimpleType) {
        LOGGER.debug("setDatabaseSimpleType - {}", databaseSimpleType);
        this.databaseSimpleType = databaseSimpleType;
    }

    public boolean isDatabaseStoreFaces() {
        return databaseStoreFaces;
    }

    public void setDatabaseStoreFaces(boolean databaseStoreFaces) {
        LOGGER.debug("setDatabaseStoreFaces - {}", databaseStoreFaces);
        this.databaseStoreFaces = databaseStoreFaces;
    }

    public String getDatabaseConnectionUrl() {
        return databaseConnectionUrl;
    }

    public void setDatabaseConnectionUrl(String databaseConnectionUrl) {
        LOGGER.debug("setDatabaseConnectionUrl - {}", databaseConnectionUrl);
        this.databaseConnectionUrl = databaseConnectionUrl;
    }

    public boolean isEnableEffects() {
        return enableEffects;
    }

    public void setEnableEffects(boolean enableEffects) {
        LOGGER.debug("setEnableEffects - {}", enableEffects);
        this.enableEffects = enableEffects;
    }

    public void setAvailableEffects(String availableEffects) {
        LOGGER.debug("setAvailableEffects - {}", availableEffects);
        String[] effects = availableEffects.toLowerCase().replace(" ", "").split(",");
        this.availableEffects = new HashMap<>();

        for(String effect : effects){
            EffectsLoader effectLoader = EffectsLoader.getEffectWithName(effect);
            EffectType type = effectLoader.getType();
            List<EffectsLoader> list = this.availableEffects.getOrDefault(type, new ArrayList<>());
            list.add(effectLoader);
            this.availableEffects.put(type, list);
        }
    }

    public Map<EffectType, List<EffectsLoader>> getAvailableEffects() {
        return availableEffects;
    }

    public Integer getSmilingReminderInterval() {
        return smilingReminderInterval;
    }

    public void setSmilingReminderInterval(Integer smilingReminderInterval) {
        LOGGER.debug("setSmilingReminderInterval - {}", smilingReminderInterval);
        this.smilingReminderInterval = smilingReminderInterval;
    }

    public Integer getMaxSingleSmileTime() {
        return maxSingleSmileTime;
    }

    public void setMaxSingleSmileTime(Integer maxSingleSmileTime) {
        LOGGER.debug("setMaxSingleSmileTime - {}", maxSingleSmileTime);
        this.maxSingleSmileTime = maxSingleSmileTime;
    }
}
