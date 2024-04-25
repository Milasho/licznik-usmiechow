package smilecounter.desktop.config;

public class ApplicationParameters {
    public static final String ARG_CONFIG = "--config";
    public static final String ARG_LOGS = "--log";
    public static final String ARG_LIBS = "--libs";
    public static final String ARG_WELD = "--weld";

    public static final String PARAM_APPLICATION_TITLE = "application.name";
    public static final String PARAM_DEBUG_MODE = "application.debugMode";
    public static final String PARAM_DEFAULT_LANGUAGE = "application.defaultLanguage";
    public static final String PARAM_AVAILABLE_LANGUAGES = "application.availableLanguages";

    public static final String PARAM_DATABASE_CONNECTION_URL = "database.connectionUrl";
    public static final String PARAM_REFRESH_DATA_INTERVAL = "database.refreshDataInterval";
    public static final String PARAM_DATABASE_SIMPLE_TYPE = "database.simpleType";
    public static final String PARAM_DATABASE_STORE_FACES = "database.storeFaces";

    public static final String PARAM_DEFAULT_AFFECTIVE_SERVICE = "affective.default.service";
    public static final String PARAM_AFFECTIVE_LUXAND_KEY = "affective.luxand.key";
    public static final String PARAM_AFFECTIVE_LIBS_PATH = "affective.libs.path";
    public static final String PARAM_AFFECTIVE_SMILE_CONFIDENCE_TRESHOLD = "affective.smileConfidence.threshold";
    public static final String PARAM_AFFECTIVE_SHOW_DETECTED_FRAGMENTS = "affective.showDetectedFragments";
    public static final String PARAM_MAX_SINGLE_SMILE_TIME = "affective.maxSingleSmileTime";

    public static final String PARAM_SETTING_LOCATION_NAME = "settings.locationName";
    public static final String PARAM_SETTING_FULLSCREEN_MODE = "settings.fullscreenMode";
    public static final String PARAM_SETTING_SHOW_STATISTICS = "settings.showStatistics";

    public static final String PARAM_EFFECTS_LIST = "effects.effectsList";
    public static final String PARAM_EFFECTS_ENABLED = "effects.enabled";

    public static final String PARAM_SMILING_REMINDER_INTERVAL = "smiles.reminder.interval";
}
