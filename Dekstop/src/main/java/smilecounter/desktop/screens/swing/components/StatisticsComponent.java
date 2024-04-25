package smilecounter.desktop.screens.swing.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smilecounter.desktop.config.ApplicationConfiguration;
import smilecounter.desktop.config.UserSettings;
import smilecounter.desktop.screens.swing.common.LanguageManager;
import smilecounter.desktop.screens.swing.listeners.GlobalListenersRegister;
import smilecounter.desktop.screens.swing.listeners.LocaleChangeListener;
import smilecounter.desktop.services.SmilesCounterService;
import smilecounter.desktop.utils.WeldUtils;

import javax.swing.*;
import java.awt.event.ActionListener;

import static javax.swing.GroupLayout.Alignment.LEADING;

public class StatisticsComponent extends JPanel implements LocaleChangeListener {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private static final long serialVersionUID = 1L;

    private SmilesCounterService smileCounterService = WeldUtils.getClassFromWeld(SmilesCounterService.class);
    private GlobalListenersRegister listenersRegister = WeldUtils.getClassFromWeld(GlobalListenersRegister.class);
    private UserSettings userSettings = WeldUtils.getClassFromWeld(UserSettings.class);
    private LanguageManager languageManager = WeldUtils.getClassFromWeld(LanguageManager.class);
    private ApplicationConfiguration appConfig = WeldUtils.getClassFromWeld(ApplicationConfiguration.class);

    private JLabel title;
    private JLabel smilesToday;
    private JLabel smilesOverall;

    private Timer refreshDataTimer;

    public StatisticsComponent(){
        listenersRegister.addLocaleChangeListener(this);
        initGui();

        refreshTextsFromLocales();

        initLayout();
        setVisible(true);
    }

    public void startTimer(){
        refreshDataTimer = new Timer(appConfig.getDatabaseRefreshDataInterval(), getRefreshDataListener());
        refreshDataTimer.start();
    }

    public void stopTimer(){
        if(refreshDataTimer != null){
            refreshDataTimer.stop();
        }
    }

    private void initGui(){
        title = new JLabel();
        smilesToday = new JLabel();
        smilesOverall = new JLabel();
    }

    private void initLayout(){
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // Set the vertical layout
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(title)
                .addComponent(smilesToday)
                .addComponent(smilesOverall)
        );

        // Set the horizontal layout
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(LEADING)
                        .addComponent(title)
                        .addComponent(smilesToday)
                        .addComponent(smilesOverall)
                )
        );
    }

    @Override
    public void refreshTextsFromLocales() {
        Thread t = new Thread(() -> {
            try{
                LOGGER.info("Refreshing statistics...");
                long globalSmilesCounter = smileCounterService.getGlobalSmilesCounter();
                long globalSmilesToday = smileCounterService.getGlobalSmilesFromTodayCounter();
                long globalSmilesWeek = smileCounterService.getGlobalSmilesFromLastWeekCounter();
                long globalSmilesMonth = smileCounterService.getGlobalSmilesFromLastMonthCounter();

                long localisationSmilesCounter = smileCounterService.getCurrentLocationSmilesCounter();
                long localisationSmilesToday = smileCounterService.getCurrentLocationSmilesFromTodayCounter();
                long localisationSmilesWeek = smileCounterService.getCurrentLocationSmilesFromLastWeekCounter();
                long localisationSmilesMonth = smileCounterService.getCurrentLocationSmilesFromLastMonthCounter();

                title.setText(languageManager.getLocale("statistics.title"));
                smilesToday.setText(languageManager.getParametrizedLocale("statistics.smiles.localisation", userSettings.getLocationName(),
                        localisationSmilesToday, localisationSmilesWeek, localisationSmilesMonth, localisationSmilesCounter));
                smilesOverall.setText(languageManager.getParametrizedLocale("statistics.smiles.global",
                        globalSmilesToday, globalSmilesWeek, globalSmilesMonth, globalSmilesCounter));
            }
            catch (Exception ignored){
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private ActionListener getRefreshDataListener(){
        return e -> refreshTextsFromLocales();
    }
}