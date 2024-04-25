package smilecounter.desktop;

import smilecounter.desktop.config.ApplicationInitializer;
import smilecounter.desktop.screens.swing.windows.ApplicationWindow;
import smilecounter.desktop.screens.swing.windows.SplashWindow;

import java.awt.event.WindowEvent;

public class MainSwing {
    public static void main(String[] args) throws Exception {
        SplashWindow splash = new SplashWindow();
        ApplicationInitializer.initApplication(args);
        splash.dispatchEvent(new WindowEvent(splash, WindowEvent.WINDOW_CLOSING));
        startApp();
    }

    private static void startApp() {
        ApplicationWindow mainWindow = new ApplicationWindow();
        mainWindow.init();
        mainWindow.setVisible(true);
    }
}
