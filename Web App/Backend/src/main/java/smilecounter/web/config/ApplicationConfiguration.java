package smilecounter.web.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smilecounter.core.utils.ResourcesLoader;
import smilecounter.web.utils.ContextLoader;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.*;
import java.util.Properties;

@ApplicationScoped
@Startup
@Singleton
public class ApplicationConfiguration {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final static String PROPERTIES_FILE_RESOURCE = "config/application-configuration.properties";

    @Inject private ContextLoader contextLoader;

    private String luxandKey;
    private String databaseConnectionUrl;
    private String affectiveLibsPath;

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

        String externalPath = contextLoader.loadStringFromContext(ContextLoader.EXTERNAL_PROPERTIES);
        if(StringUtils.isNotEmpty(externalPath)){
            initFromFile(externalPath);
        }
    }

    private void initFromFile(String path){
        LOGGER.debug("Loading application configuration from external file {}...", path);
        try {
            Properties config = ResourcesLoader.getPropertiesFromExternalFile(path);
            initFromProperties(config);
        } catch (IOException e) {
            LOGGER.error("Error during loading parameters from file {}: ", path, e);
        }
    }

    private void initFromProperties(Properties config){
        String luxandKey = config.getProperty(ApplicationParameters.PARAM_AFFECTIVE_LUXAND_KEY);
        if(StringUtils.isNotEmpty(luxandKey)){
            setLuxandKey(luxandKey);
        }

        String databaseConnectionUrl = config.getProperty(ApplicationParameters.PARAM_DATABASE_CONNECTION_URL);
        if(StringUtils.isNotEmpty(databaseConnectionUrl)){
            setDatabaseConnectionUrl(databaseConnectionUrl);
        }

        String affectiveLibsPath = config.getProperty(ApplicationParameters.PARAM_AFFECTIVE_LIBS_PATH);
        if(StringUtils.isNotEmpty(affectiveLibsPath)){
            setAffectiveLibsPath(affectiveLibsPath);
        }
    }

    public String getDatabaseConnectionUrl() {
        return databaseConnectionUrl;
    }

    public void setDatabaseConnectionUrl(String databaseConnectionUrl) {
        LOGGER.debug("setDatabaseConnectionUrl - {}", databaseConnectionUrl);
        this.databaseConnectionUrl = databaseConnectionUrl;
    }

    public String getLuxandKey() {
        return luxandKey;
    }

    public void setLuxandKey(String luxandKey) {
        LOGGER.debug("setLuxandKey - {}", luxandKey);
        this.luxandKey = luxandKey;
    }

    public String getAffectiveLibsPath() {
        return affectiveLibsPath;
    }

    public void setAffectiveLibsPath(String affectiveLibsPath) {
        LOGGER.debug("setAffectiveLibsPath - {}", affectiveLibsPath);
        this.affectiveLibsPath = affectiveLibsPath;
    }
}
