package smilecounter.desktop.screens.swing.views;

import smilecounter.desktop.config.UserSettings;
import smilecounter.desktop.screens.swing.common.LanguageManager;
import smilecounter.desktop.screens.swing.common.ViewsCommon;
import smilecounter.desktop.screens.swing.common.ViewsEnum;
import smilecounter.desktop.screens.swing.components.SmilecounterComponent;
import smilecounter.desktop.screens.swing.components.StatisticsComponent;
import smilecounter.desktop.screens.swing.components.WebcamComponent;
import smilecounter.desktop.screens.swing.listeners.LocaleChangeListener;
import smilecounter.desktop.utils.WeldUtils;

import javax.swing.*;
import java.awt.event.ActionListener;

import static javax.swing.GroupLayout.Alignment.CENTER;

public class CameraView extends JPanel implements LocaleChangeListener {
    private static final long serialVersionUID = 1L;

    private ViewsCommon viewsCommon = WeldUtils.getClassFromWeld(ViewsCommon.class);
    private LanguageManager languageManager = WeldUtils.getClassFromWeld(LanguageManager.class);
    private UserSettings userSettings = WeldUtils.getClassFromWeld(UserSettings.class);

    private WebcamComponent webcam;
    private StatisticsComponent statistics;
    private SmilecounterComponent smilecounter;

    private JButton mainMenuButton;

    public CameraView(){
        webcam = new WebcamComponent();
        statistics = new StatisticsComponent();
        smilecounter = new SmilecounterComponent();

        initGui();
    }

    private void initGui(){
        addCameraView();

        mainMenuButton = new JButton();
        mainMenuButton.addActionListener(getMainMenuListener());

        initLayout();
        refreshTextsFromLocales();
        this.setVisible(true);
    }

    private void initLayout(){
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // Set the vertical layout
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(smilecounter)
                .addComponent(statistics)
                .addComponent(webcam)
                .addComponent(mainMenuButton)
        );

        // Set the horizontal layout
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(CENTER)
                    .addComponent(smilecounter)
                    .addComponent(webcam)
                    .addComponent(statistics)
                    .addComponent(mainMenuButton)
                )
        );
    }

    public void start(){
        if(userSettings.isShowStatistics()){
            statistics.setVisible(true);
            statistics.startTimer();
        }
        else{
            statistics.setVisible(false);
        }

        webcam.start();
        webcam.setSmileCounter(smilecounter);
        smilecounter.resetCounter();
    }

    public void stop(){
        if(userSettings.isShowStatistics()) {
            statistics.stopTimer();
        }
        webcam.stop();
        smilecounter.stopTimers();
    }

    private void addCameraView(){
        this.add(webcam);
    }

    private ActionListener getMainMenuListener(){
        return arg0 -> {
            viewsCommon.switchView(ViewsEnum.MAIN_MENU);
            stop();
        };
    }

    @Override
    public void refreshTextsFromLocales() {
        mainMenuButton.setText(languageManager.getLocale("cameraView.buttons.mainMenu"));
    }
}