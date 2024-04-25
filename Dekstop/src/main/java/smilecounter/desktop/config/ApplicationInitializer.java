package smilecounter.desktop.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smilecounter.desktop.effects.utils.EffectsLoader;
import smilecounter.desktop.services.AffectiveService;
import smilecounter.desktop.services.SmilesCounterService;
import smilecounter.desktop.utils.WeldUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ApplicationInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationInitializer.class);

    private static final String DEFAULT_CONFIG_FILE = "smilecounter.config";
    private static final String DEFAULT_LIBS_DIRECTORY = "libs";

    public static void initApplication(String[] args){
        Boolean useWeld = Boolean.valueOf(getProgramArgument(ApplicationParameters.ARG_WELD, args));
        if (Boolean.TRUE.equals(useWeld)) {
            WeldUtils.initializeContainer(true);
        }

        ApplicationConfiguration appConfig = WeldUtils.getClassFromWeld(ApplicationConfiguration.class);
        appConfig.init();

        // Load configuration from parameters
        String logPath = getProgramArgument(ApplicationParameters.ARG_LOGS, args);
        if (StringUtils.isNotEmpty(logPath)) {
            appConfig.setUpLogger(logPath);
        }

        String configPath = getProgramArgument(ApplicationParameters.ARG_CONFIG, args);
        if (StringUtils.isNotEmpty(configPath)) {
            appConfig.initFromFile(configPath);
        }
        else {
            String defaultConfigPath = getDefaultFilePath(DEFAULT_CONFIG_FILE);
            if(StringUtils.isNotEmpty(defaultConfigPath)){
                appConfig.initFromFile(defaultConfigPath);
            }
        }

        String libsPath = getProgramArgument(ApplicationParameters.ARG_LIBS, args);
        if (StringUtils.isNotEmpty(libsPath)) {
            appConfig.setAffectiveLibsPath(libsPath);
        }
        else{
            String defaultLibPath = getDefaultFilePath(DEFAULT_LIBS_DIRECTORY);
            if(!StringUtils.isEmpty(defaultLibPath)){
                appConfig.setAffectiveLibsPath(defaultLibPath);
            }
        }

        WeldUtils.getClassFromWeld(SmilesCounterService.class).init();
        WeldUtils.getClassFromWeld(AffectiveService.class).preloadServices();
        EffectsLoader.initializeAllEffects();
    }

    private static String getProgramArgument(String name, String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (name.equals(args[i])) {
                if (i + 1 < args.length) {
                    return args[i + 1];
                }
                else {
                    LOGGER.error("{} parameter is not set correctly! Please, set it up like: {} PARAM_VALUE", name, name);
                }
            }
        }
        return null;
    }

    private static String getDefaultFilePath(String file) {
        String mainFile = Paths.get(".").toAbsolutePath().normalize().toString();
        Path pathToFile = Paths.get(mainFile + "/" + file).normalize();
        String normalizedPathToFile = pathToFile.toString();
        boolean fileExists = Files.exists(pathToFile);
        LOGGER.debug("Checking if file {} exists...: {}.", normalizedPathToFile, fileExists);
        return fileExists ? normalizedPathToFile : null;
    }
}
