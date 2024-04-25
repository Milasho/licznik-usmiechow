package smilecounter.core.affective.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smilecounter.core.affective.enums.AffectiveServices;

import java.util.ArrayList;
import java.util.List;

public class AffectiveLibsLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(AffectiveLibsLoader.class);
    private static List<String> loadedLibs = new ArrayList<>();
    private static String OS = System.getProperty("os.name");

    public static boolean loadLib(AffectiveServices lib, String path){
        List<String> files = lib.getFileNames();
        LOGGER.debug("Initializing lib {} with files ({})", lib.getName(), files);
        return loadLibByPath(path, files, lib.getLibNames());
    }

    public static boolean loadLib(AffectiveServices lib){
        LOGGER.debug("Initializing lib {} by default ({})", lib.getName(), lib.getLibNames());
        return loadLibByName(lib.getLibNames());
    }

    private static boolean loadLibByName(List<String> names){
        for(String libName : names){
            if(!loadedLibs.contains(libName)){
                try{
                    System.loadLibrary(libName);
                    loadedLibs.add(libName);
                } catch (UnsatisfiedLinkError e) {
                    LOGGER.error("Error during loading {}:", libName, e);
                    return false;
                }
            }
            else{
                LOGGER.debug("Lib {} was already initialised. Skipping...", libName);
            }
        }

        return true;
    }

    private static boolean loadLibByPath(String path, List<String> p, List<String> names){
        for(String file: p){
            String libName = names.get(p.indexOf(file));
            String libPath = getPathForLibrary(path, file);
            if(!loadedLibs.contains(libName)){
                try{
                    LOGGER.debug("Loading file {}...", libPath);
                    System.load(libPath);
                    loadedLibs.add(libName);
                } catch (UnsatisfiedLinkError e) {
                    LOGGER.error("Error during loading {} from {}:", libName, p, e);
                    return false;
                }
            }
            else{
                LOGGER.debug("Lib {} was already initialised. Skipping...", libName);
            }
        }

        return true;
    }

    private static String getPathForLibrary(String path, String libraryFile){
        String extension = ".so";
        if(OS != null && OS.startsWith("Windows")){
            extension = ".dll";
        }
        return path + "/" + libraryFile + extension;
    }

    public static boolean isLibProperlyLoaded(String name){
        return loadedLibs.contains(name);
    }

    public static boolean isLibProperlyLoaded(AffectiveServices service) {
        boolean result = false;
        if(service != null){
            result = true;
            // Libs that don't have to be initialised should be always loaded
            if(service.getLibNames() != null){
                for(String libName : service.getLibNames()){
                    if(!loadedLibs.contains(libName)){
                        result = false;
                        break;
                    }
                }
            }
        }
        return result;
    }
}
