package smilecounter.desktop.screens.swing.views;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPicker;
import com.github.sarxos.webcam.WebcamResolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smilecounter.core.affective.enums.AffectiveServices;
import smilecounter.core.affective.utils.AffectiveLibsLoader;
import smilecounter.desktop.config.ApplicationConfiguration;
import smilecounter.desktop.config.UserSettings;
import smilecounter.desktop.screens.swing.common.LanguageManager;
import smilecounter.desktop.screens.swing.common.ViewsCommon;
import smilecounter.desktop.screens.swing.common.ViewsEnum;
import smilecounter.desktop.screens.swing.components.JComboboxItem;
import smilecounter.desktop.screens.swing.components.ViewTitleComponent;
import smilecounter.desktop.screens.swing.listeners.GlobalListenersRegister;
import smilecounter.desktop.screens.swing.listeners.LocaleChangeListener;
import smilecounter.desktop.services.AffectiveService;
import smilecounter.desktop.services.SmilesCounterService;
import smilecounter.desktop.utils.WeldUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import static javax.swing.GroupLayout.Alignment.LEADING;
import static javax.swing.GroupLayout.Alignment.TRAILING;

public class SettingsView extends JPanel implements ItemListener, LocaleChangeListener {
    private static final long serialVersionUID = 1L;
    private final Logger LOGGER = LoggerFactory.getLogger(SettingsView.class);
    private final static Dimension DEFAULT_TEXT_FIELD_SIZE = new Dimension(300, 25);

    private final static int MIN_VERTICAL_GAP = 10;
    private final static int MAX_VERTICAL_GAP = 30;

    private ApplicationConfiguration appConfig = WeldUtils.getClassFromWeld(ApplicationConfiguration.class);
    private ViewsCommon viewsCommon =  WeldUtils.getClassFromWeld(ViewsCommon.class);
    private LanguageManager languageManager = WeldUtils.getClassFromWeld(LanguageManager.class);
    private UserSettings userSettings = WeldUtils.getClassFromWeld(UserSettings.class);
    private GlobalListenersRegister listenersRegister = WeldUtils.getClassFromWeld(GlobalListenersRegister.class);
    private AffectiveService affectiveService = WeldUtils.getClassFromWeld(AffectiveService.class);
    private SmilesCounterService smilesCounterService = WeldUtils.getClassFromWeld(SmilesCounterService.class);

    // Components
    private WebcamPicker webcamPicker;
    private JComboBox<JComboboxItem> languagePicker;
    private JComboBox<JComboboxItem> affectiveServicePicker;
    private JComboBox<JComboboxItem> showDetectedFragmentsPicker;
    private JComboBox<JComboboxItem> storeFacesPicker;
    private JComboBox<JComboboxItem> offlineDatabasePicker;
    private JComboBox<JComboboxItem> enableEffectsPicker;
    private JComboBox<JComboboxItem> fullscreenModePicker;
    private JComboBox<JComboboxItem> showStatisticsPicker;
    private JButton mainMenuButton;
    private JButton saveButton;
    private JTextField localisation;
    private ViewTitleComponent viewTitle;

    private JLabel webcamLabel;
    private JLabel languagePickerLabel;
    private JLabel affectiveServiceLabel;
    private JLabel localisationLabel;
    private JLabel showDetectedFragmentsLabel;
    private JLabel storeFacesLabel;
    private JLabel offlineDatabaseLabel;
    private JLabel enableEffectsLabel;
    private JLabel fullscreenModeLabel;
    private JLabel showStatisticsLabel;

    public SettingsView() {
        listenersRegister.addLocaleChangeListener(this);
        initGui();
    }

    private void initGui() {
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        viewTitle = new ViewTitleComponent();

        initWebcamPicker();
        initLanguagePicker();
        initAffectiveServicePicker();
        initLocalisation();
        initStoreFaces();
        initShowDetectedFragments();
        initOfflineDatabase();
        initEnableEffects();
        initFullscreenMode();
        initShowStatistics();

        mainMenuButton = new JButton();
        mainMenuButton.addActionListener(viewsCommon.getChangeViewListener(ViewsEnum.MAIN_MENU));

        saveButton = new JButton();
        saveButton.addActionListener(getSaveButtonListener());
        saveButton.setEnabled(false);

        refreshTextsFromLocales();

        initLayout();
        this.setVisible(true);
    }

    private void initLayout() {
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // Set the vertical layout
        layout.setVerticalGroup(layout.createSequentialGroup().addComponent(viewTitle).addGap(MIN_VERTICAL_GAP, MAX_VERTICAL_GAP, MAX_VERTICAL_GAP)
                // Settings
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(localisationLabel).addComponent(localisation))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(languagePickerLabel).addComponent(languagePicker))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(webcamLabel).addComponent(webcamPicker))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(affectiveServiceLabel).addComponent(affectiveServicePicker))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(showDetectedFragmentsLabel).addComponent(showDetectedFragmentsPicker))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(storeFacesLabel).addComponent(storeFacesPicker))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(offlineDatabaseLabel).addComponent(offlineDatabasePicker))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(enableEffectsLabel).addComponent(enableEffectsPicker))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(fullscreenModeLabel).addComponent(fullscreenModePicker))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(showStatisticsLabel).addComponent(showStatisticsPicker))
                // Buttons
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(TRAILING).addComponent(saveButton).addComponent(mainMenuButton)));

        // Set the horizontal layout
        layout.setHorizontalGroup(layout.createSequentialGroup().addComponent(viewTitle)
                // Labels
                .addGroup(layout.createParallelGroup(TRAILING).addComponent(localisationLabel).addComponent(languagePickerLabel).addComponent(webcamLabel)
                        .addComponent(affectiveServiceLabel).addComponent(showDetectedFragmentsLabel).addComponent(storeFacesLabel).addComponent(offlineDatabaseLabel)
                        .addComponent(enableEffectsLabel).addComponent(fullscreenModeLabel).addComponent(showStatisticsLabel))
                // Elements
                .addGroup(layout.createParallelGroup(LEADING).addComponent(localisation).addComponent(languagePicker).addComponent(webcamPicker)
                        .addComponent(affectiveServicePicker).addComponent(showDetectedFragmentsPicker).addComponent(storeFacesPicker).addComponent(offlineDatabasePicker)
                        .addComponent(enableEffectsPicker).addComponent(fullscreenModePicker).addComponent(showStatisticsPicker))
                // Buttons
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(saveButton).addComponent(mainMenuButton));
    }

    private void initWebcamPicker() {
        webcamPicker = new WebcamPicker();
        webcamPicker.addItemListener(this);
        webcamPicker.addActionListener(getSettingsChangedListener());

        webcamLabel = new JLabel();
    }


    private void initShowDetectedFragments() {
        showDetectedFragmentsPicker = createYesNoComponent(appConfig.isAffectiveShowDetectedFragments());
        showDetectedFragmentsLabel = new JLabel();
    }

    private void initStoreFaces() {
        storeFacesPicker = createYesNoComponent(appConfig.isAffectiveShowDetectedFragments());
        storeFacesLabel = new JLabel();
    }

    private void initOfflineDatabase() {
        offlineDatabasePicker = createYesNoComponent(!appConfig.isDatabaseSimpleType());
        offlineDatabaseLabel = new JLabel();
    }

    private void initEnableEffects() {
        enableEffectsPicker = createYesNoComponent(appConfig.isEnableEffects());
        enableEffectsLabel = new JLabel();
    }

    private void initFullscreenMode() {
        fullscreenModePicker = createYesNoComponent(userSettings.isFullscreenMode());
        fullscreenModeLabel = new JLabel();
    }

    private void initShowStatistics() {
        showStatisticsPicker = createYesNoComponent(userSettings.isShowStatistics());
        showStatisticsLabel = new JLabel();
    }

    private JComboBox<JComboboxItem> createYesNoComponent(boolean selected){
        JComboBox<JComboboxItem> result = new JComboBox<>();;
        DefaultComboBoxModel<JComboboxItem> model = new DefaultComboBoxModel<>();
        model.addElement(new JComboboxItem(languageManager.getLocale("settings.option.yes"), "true"));
        model.addElement(new JComboboxItem(languageManager.getLocale("settings.option.no"), "false"));
        result.setModel(model);
        if(selected){
            result.setSelectedIndex(0);
        }
        else{
            result.setSelectedIndex(1);
        }

        result.addActionListener(getSettingsChangedListener());
        return result;
    }

    private void initLanguagePicker() {
        languagePicker = new JComboBox<>();
        DefaultComboBoxModel<JComboboxItem> model = new DefaultComboBoxModel<>();
        for (String lang : appConfig.getAvailableLanguages()) {
            model.addElement(new JComboboxItem(languageManager.getLocale("language.name", lang), lang));
        }

        languagePicker.setModel(model);
        languagePicker.addActionListener(getSettingsChangedListener());

        // Set currently selected value as first on list
        for (int i = 0; i < languagePicker.getItemCount(); i++) {
            String selectedLanguage = userSettings.getSelectedLanguage();
            if (selectedLanguage.equals(languagePicker.getItemAt(i).getValue())) {
                languagePicker.setSelectedIndex(i);
                break;
            }
        }

        languagePickerLabel = new JLabel();
    }

    private void initAffectiveServicePicker() {
        affectiveServicePicker = new JComboBox<>();
        AffectiveServices[] availableServices = {
                AffectiveServices.OPEN_CV, AffectiveServices.OPENIMAJ,
                AffectiveServices.LUXAND, AffectiveServices.CUSTOM
        };
        DefaultComboBoxModel<JComboboxItem> model = new DefaultComboBoxModel<>();

        for (AffectiveServices service : availableServices) {
            if (AffectiveLibsLoader.isLibProperlyLoaded(service)) {
                model.addElement(new JComboboxItem(service.getName(), service.getName()));
            }
        }

        affectiveServicePicker.setModel(model);
        affectiveServicePicker.addActionListener(getSettingsChangedListener());

        // Set currently selected value as first on list
        for (int i = 0; i < affectiveServicePicker.getItemCount(); i++) {
            if (affectiveService.isServiceSelected(affectiveServicePicker.getItemAt(i).getValue())) {
                affectiveServicePicker.setSelectedIndex(i);
                break;
            }
        }

        affectiveServiceLabel = new JLabel();
    }

    private void initLocalisation() {
        localisation = new JTextField();
        localisation.setEditable(true);
        localisation.addActionListener(getSettingsChangedListener());
        localisation.setPreferredSize(DEFAULT_TEXT_FIELD_SIZE);
        localisation.setText(userSettings.getLocationName());
        localisation.setVisible(true);

        localisationLabel = new JLabel();
    }

    private void saveSettings() {
        LOGGER.debug("saveSettings - Saving settings...");
        JComboboxItem item = (JComboboxItem) languagePicker.getSelectedItem();
        if (!userSettings.getSelectedLanguage().equals(item.getValue())) {
            LOGGER.debug("updateLocalesListener - Changing language to {}...", item.getValue());
            userSettings.setSelectedLanguage(item.getValue());
            listenersRegister.localeChangeEvent();
        }
        userSettings.setLocationName(localisation.getText());

        JComboboxItem pickedAffectiveService = (JComboboxItem) affectiveServicePicker.getSelectedItem();
        affectiveService.setService(pickedAffectiveService.getValue());

        saveButton.setEnabled(false);
        viewsCommon.getChangeViewListener(ViewsEnum.MAIN_MENU).actionPerformed(null);

        appConfig.setDatabaseStoreFaces(getBooleanFromPicker(storeFacesPicker));
        appConfig.setAffectiveShowDetectedFragments(getBooleanFromPicker(showDetectedFragmentsPicker));
        appConfig.setEnableEffects(getBooleanFromPicker(enableEffectsPicker));
        smilesCounterService.connectToDatabase(getBooleanFromPicker(offlineDatabasePicker));

        boolean isFullscreen = getBooleanFromPicker(fullscreenModePicker);
        if(isFullscreen != userSettings.isFullscreenMode()){
            userSettings.setFullscreenMode(isFullscreen);
            viewsCommon.setFullscreenMode(userSettings.isFullscreenMode());
        }

        userSettings.setShowStatistics(getBooleanFromPicker(showStatisticsPicker));
    }

    private boolean getBooleanFromPicker(JComboBox<JComboboxItem> picker){
        JComboboxItem selectedItem = (JComboboxItem) picker.getSelectedItem();
        String value = selectedItem.getValue();
        return value != null && Boolean.TRUE.equals(Boolean.valueOf(value));
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        Webcam webcam = userSettings.getCamera();
        if (e.getItem() != null && e.getItem() != webcam) {
            Webcam newWebcam = (Webcam) e.getItem();
            newWebcam.setViewSize(WebcamResolution.VGA.getSize());
            userSettings.setCamera(newWebcam);
        }
    }

    @Override
    public void refreshTextsFromLocales() {
        mainMenuButton.setText(languageManager.getLocale("settings.buttons.mainMenu"));
        saveButton.setText(languageManager.getLocale("settings.buttons.save"));
        webcamLabel.setText(languageManager.getLocale("settings.labels.webcam"));
        languagePickerLabel.setText(languageManager.getLocale("settings.labels.language"));
        localisationLabel.setText(languageManager.getLocale("settings.labels.localisation"));
        viewTitle.refreshText(languageManager.getLocale("settings.title"));
        affectiveServiceLabel.setText(languageManager.getLocale("settings.labels.affectiveService"));
        showDetectedFragmentsLabel.setText(languageManager.getLocale("settings.labels.showFragments"));
        storeFacesLabel.setText(languageManager.getLocale("settings.labels.storeFaces"));
        offlineDatabaseLabel.setText(languageManager.getLocale("settings.labels.onlineDatabase"));
        enableEffectsLabel.setText(languageManager.getLocale("settings.labels.enableEffects"));
        fullscreenModeLabel.setText(languageManager.getLocale("settings.labels.fullscreenMode"));
        showStatisticsLabel.setText(languageManager.getLocale("settings.labels.showStatistics"));
    }

    private ActionListener getSettingsChangedListener() {
        return arg0 -> {
            if (saveButton != null) {
                saveButton.setEnabled(true);
            }
        };
    }

    private ActionListener getSaveButtonListener() {
        return arg0 -> saveSettings();
    }
}
