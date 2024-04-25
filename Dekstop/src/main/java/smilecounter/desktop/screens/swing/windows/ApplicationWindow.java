package smilecounter.desktop.screens.swing.windows;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smilecounter.core.utils.ResourcesLoader;
import smilecounter.desktop.config.UserSettings;
import smilecounter.desktop.screens.swing.common.LanguageManager;
import smilecounter.desktop.screens.swing.common.ViewsCommon;
import smilecounter.desktop.screens.swing.common.ViewsEnum;
import smilecounter.desktop.screens.swing.listeners.GlobalListenersRegister;
import smilecounter.desktop.screens.swing.listeners.LocaleChangeListener;
import smilecounter.desktop.screens.swing.views.CameraView;
import smilecounter.desktop.screens.swing.views.MainMenuView;
import smilecounter.desktop.screens.swing.views.SettingsView;
import smilecounter.desktop.utils.WeldUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class ApplicationWindow extends JFrame implements LocaleChangeListener {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final long serialVersionUID = 1L;
    private UserSettings userSettings = WeldUtils.getClassFromWeld(UserSettings.class);
    private static final String ICON = "images/icon.png";

    private ViewsCommon viewsCommon = WeldUtils.getClassFromWeld(ViewsCommon.class);
    private LanguageManager languageManager = WeldUtils.getClassFromWeld(LanguageManager.class);
    private GlobalListenersRegister listenersRegister = WeldUtils.getClassFromWeld(GlobalListenersRegister.class);

    private MainMenuView mainMenu = new MainMenuView();
    private SettingsView settings = new SettingsView();
    private CameraView camera = new CameraView();

    public void init(){
        listenersRegister.addLocaleChangeListener(this);
        viewsCommon.setMainFrame(this);

        loadTitleIcon();
        initWindow();
        initGui();
    }

    private void loadTitleIcon(){
        Image image = null;
        try {
            image = ResourcesLoader.getImage(ICON, getClass());
        } catch (IOException e) {
            LOGGER.error("Error during loading image resource (logo): {}. ", ICON, e);
        }

        if(image != null){
            setIconImage(image);
        }
    }

    private void initWindow(){
        this.setLocationRelativeTo(null);
        this.setDefaultTitle();

        this.addWindowListener(afterWindowCloseAction());
    }

    private WindowAdapter afterWindowCloseAction(){
        return new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                LOGGER.debug("afterWindowCloseAction - Closing application...");
                camera.stop();
                System.exit(0);
            }
        };
    }

    private void initGui(){
        JPanel mainPanel = viewsCommon.getMainPanel();
        mainPanel.setLayout(viewsCommon.getCardLayout());
        mainPanel.add(settings, ViewsEnum.SETTINGS);
        mainPanel.add(mainMenu, ViewsEnum.MAIN_MENU);
        mainPanel.add(camera, ViewsEnum.CAMERA);
        this.add(mainPanel);

        viewsCommon.setCameraView(camera);
        viewsCommon.switchView(ViewsEnum.MAIN_MENU);
        viewsCommon.setFullscreenMode(userSettings.isFullscreenMode());
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

    private void setDefaultTitle(){
        refreshTextsFromLocales();
    }

    @Override
    public void refreshTextsFromLocales() {
        this.setTitle(languageManager.getLocale("application.title"));
    }
}
