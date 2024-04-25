package smilecounter.desktop.screens.swing.common;

import org.apache.commons.lang3.StringUtils;
import smilecounter.core.utils.ResourcesLoader;
import smilecounter.desktop.config.ApplicationConfiguration;
import smilecounter.desktop.config.UserSettings;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@ApplicationScoped
public class LanguageManager {
    private static final String LOCALE_FILE_FORMAT = "languages/locale-%s.properties";
    private static final String LOCALE_ICON_PATH_FORMAT = "languages/icons/lang_%s.png";

    @Inject private UserSettings userSettigns;
    @Inject private ApplicationConfiguration appConfig;

    private Map<String, Properties> propertiesMap;

    @PostConstruct
    public void init(){
        propertiesMap = new HashMap<>();

        for(String locale : appConfig.getAvailableLanguages()){
            String fileName = String.format(LOCALE_FILE_FORMAT, locale);
            Properties propertiesFile;
            try {
                propertiesFile = ResourcesLoader.getPropertiesFile(fileName, this.getClass());
            }
            // On any error, just load empty properties file
            catch (IOException e) {
                propertiesFile = new Properties();
            }
            propertiesMap.put(locale, propertiesFile);
        }
    }

    public String getLocale(String key){
        String language = userSettigns.getSelectedLanguage();
        return getLocale(key, language);
    }

    public String getParametrizedLocale(String key, Object ... parameters){
        String language = userSettigns.getSelectedLanguage();
        String value = getLocale(key, language);
        return MessageFormat.format(value, parameters);
    }

    public String getLanguageIconPath(String lang){
        String file = String.format(LOCALE_ICON_PATH_FORMAT, lang);
        ClassLoader classLoader = getClass().getClassLoader();
        return classLoader.getResource(file).getPath();
    }

    public String getLocale(String key, String lang){
        Properties properties = propertiesMap.get(lang);

        if(properties == null){
            return defaultValue(key, lang);
        }

        String result = properties.getProperty(key);
        return StringUtils.isNotEmpty(result) ? result : defaultValue(key, lang);
    }

    private String defaultValue(String key, String lang){
        return new StringBuilder().append(lang).append(" - ").append(key).toString();
    }
}
