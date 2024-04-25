package smilecounter.desktop.screens.fxml.controllers;

import com.github.sarxos.webcam.Webcam;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import smilecounter.core.affective.enums.AffectiveServices;
import smilecounter.core.affective.utils.AffectiveLibsLoader;
import smilecounter.desktop.config.UserSettings;
import smilecounter.desktop.screens.fxml.Screens;
import smilecounter.desktop.services.AffectiveService;
import smilecounter.desktop.utils.ScreenUtils;
import smilecounter.desktop.utils.WeldUtils;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {
    private ScreenUtils screenUtils = WeldUtils.getClassFromWeld(ScreenUtils.class);
    private UserSettings userSettings = WeldUtils.getClassFromWeld(UserSettings.class);
    private AffectiveService affectiveService = WeldUtils.getClassFromWeld(AffectiveService.class);

    @FXML
    private ComboBox affectiveCombobox;
    @FXML
    private TextField locationNameText;
    @FXML
    private CheckBox permissionToSaveCheckbox;
    @FXML
    private ComboBox cameraCombobox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initAffectiveServicesCombobox();
        initCameraCombobox();

        permissionToSaveCheckbox.setSelected(userSettings.isPermissionToSave());
        locationNameText.setText(userSettings.getLocationName());
    }

    public void backToMainMenu(ActionEvent actionEvent) {
        screenUtils.changeScrene(Screens.MAIN_MENU, (Node) actionEvent.getSource());
    }

    public void saveSettings(ActionEvent actionEvent) {
        AffectiveServices selectedService = (AffectiveServices) affectiveCombobox.getValue();
        affectiveService.setService(selectedService);
        String locationName = locationNameText.getText();
        userSettings.setLocationName(locationName);
        boolean permissionToSave = permissionToSaveCheckbox.isSelected();
        userSettings.setPermissionToSave(permissionToSave);
        Webcam selectedWebcam = (Webcam) cameraCombobox.getValue();
        userSettings.setCamera(selectedWebcam);

        screenUtils.changeScrene(Screens.MAIN_MENU, (Node) actionEvent.getSource());
    }

    private void initAffectiveServicesCombobox() {
        List<AffectiveServices> allServices = AffectiveServices.getAllServices();
        for (AffectiveServices service : allServices) {
            if (AffectiveLibsLoader.isLibProperlyLoaded(service)) {
                ObservableList items = affectiveCombobox.getItems();
                items.add(service);
            }
        }

        affectiveCombobox.setValue(affectiveService.getService().geServiceData());
    }

    private void initCameraCombobox() {
        List<Webcam> webcams = Webcam.getWebcams();
        if (webcams != null) {
            for (Webcam webcam : webcams) {
                ObservableList items = cameraCombobox.getItems();
                items.add(webcam);
            }
            Webcam defaultWebcam = userSettings.getCamera();
            cameraCombobox.setValue(defaultWebcam);
        } else {
            cameraCombobox.setEditable(false);
        }
    }
}
