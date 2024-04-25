package smilecounter.desktop.screens.swing.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smilecounter.core.utils.ResourcesLoader;
import smilecounter.desktop.config.ApplicationConfiguration;
import smilecounter.desktop.screens.swing.common.LanguageManager;
import smilecounter.desktop.screens.swing.listeners.GlobalListenersRegister;
import smilecounter.desktop.screens.swing.listeners.LocaleChangeListener;
import smilecounter.desktop.utils.WeldUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;

import static javax.swing.GroupLayout.Alignment.CENTER;

public class SmilecounterComponent extends JPanel implements LocaleChangeListener {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final long serialVersionUID = 1L;
    private static final Font TEXT_FONT = new Font("Serif", Font.BOLD, 28);
    private static final String SMILE_ICON = "images/smile-icon.png";
    private static final int CHANGING_COLOR_INTERVAL = 1500;

    private GlobalListenersRegister listenersRegister = WeldUtils.getClassFromWeld(GlobalListenersRegister.class);
    private LanguageManager languageManager = WeldUtils.getClassFromWeld(LanguageManager.class);
    private ApplicationConfiguration appConfig = WeldUtils.getClassFromWeld(ApplicationConfiguration.class);

    private long smilesCount;

    private JLabel text;
    private JLabel icon;
    private Timer changeModeTimer;
    private Timer changeColorTimer;
    private boolean inReminderMode;

    public SmilecounterComponent(){
        smilesCount = 0;
        listenersRegister.addLocaleChangeListener(this);
        inReminderMode = false;

        loadIcon();
        initText();
        setVisible(true);

        changeModeTimer = new Timer(appConfig.getSmilingReminderInterval(), refreshReminderMode());
        changeColorTimer = new Timer(CHANGING_COLOR_INTERVAL, refreshColor());
    }

    private void loadIcon(){
        Image image = null;
        try {
            image = ResourcesLoader.getImage(SMILE_ICON, getClass());
        } catch (IOException e) {
            LOGGER.error("Error during loading image resource (logo): {}. ", SMILE_ICON, e);
        }

        if(image != null){
            icon = new JLabel(new ImageIcon(image));
        }
        else{
            icon = new JLabel();
        }
    }

    private void initText() {
        text = new JLabel();

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // Set the vertical layout
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(CENTER)
                    .addComponent(text)
                    .addComponent(icon)
                )
        );

        // Set the horizontal layout
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addComponent(icon)
                .addComponent(text)
        );

        icon.setVisible(false);
        text.setFont(TEXT_FONT);
        refreshTextsFromLocales();
    }

    private ActionListener getRefreshDataListener(){
        return e -> refreshTextsFromLocales();
    }

    @Override
    public void refreshTextsFromLocales() {
        text.setForeground(Color.BLACK);
        if(smilesCount == 0){
            text.setText(languageManager.getLocale("smilecounter.noSmiles"));
        }
        else{
            text.setText(languageManager.getParametrizedLocale("smilecounter.smilesDetected", smilesCount));
        }
    }

    public void increaseSmilecounter(int size) {
        inReminderMode = false;
        smilesCount += size;
        refreshTextsFromLocales();
        restartTimers();
        icon.setVisible(false);
    }

    private void restartTimers() {
        if(changeModeTimer != null){
            changeModeTimer.stop();
            changeModeTimer.start();
        }
        if(changeColorTimer != null){
            changeColorTimer.stop();
        }
    }

    public void stopTimers(){
        if(changeModeTimer != null){
            changeModeTimer.stop();
        }
        if(changeColorTimer != null){
            changeColorTimer.stop();
        }
        inReminderMode = false;
    }

    public void resetCounter() {
        icon.setVisible(false);
        this.smilesCount = 0;
        refreshTextsFromLocales();
        LOGGER.debug("Resetting current session smile counter...");
        if(changeModeTimer != null){
            changeModeTimer.stop();
        }
        changeModeTimer.start();
    }

    private ActionListener refreshReminderMode(){
        return e -> enterReminderMode();
    }

    private ActionListener refreshColor(){
        return e -> updateColor();
    }

    private void enterReminderMode(){
        if(!inReminderMode){
            icon.setVisible(true);
            text.setText(languageManager.getLocale("smilecounter.smilingReminder"));
            text.setForeground(Color.RED);
            inReminderMode = true;
            changeColorTimer.start();
        }
    }

    private void updateColor(){
        Color c = text.getForeground();
        if(Color.RED.equals(c)){
            text.setForeground(Color.BLACK);
        }
        else{
            text.setForeground(Color.RED);
        }
    }
}