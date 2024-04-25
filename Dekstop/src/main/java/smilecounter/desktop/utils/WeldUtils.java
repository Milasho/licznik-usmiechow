package smilecounter.desktop.utils;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import smilecounter.core.WeldHelper;

public class WeldUtils {
    private static WeldContainer container;

    public static void initializeContainer(boolean withHelper){
        Weld weld = new Weld();
        if(withHelper){
            //weld.addPackage(true, WeldHelper.class);
        }

        container = weld.initialize();
    }

    public static <T> T getClassFromWeld(Class<T> clazz) {
        if(container == null) initializeContainer(false);
        return (T) container.select(clazz).get();
    }
}
