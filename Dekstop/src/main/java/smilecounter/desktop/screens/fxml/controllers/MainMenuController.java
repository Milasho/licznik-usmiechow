package smilecounter.desktop.screens.fxml.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.stage.Stage;
import smilecounter.desktop.screens.fxml.Screens;
import smilecounter.desktop.utils.ScreenUtils;
import smilecounter.desktop.utils.WeldUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {
    private ScreenUtils screenUtils = WeldUtils.getClassFromWeld(ScreenUtils.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    public void startCamera(ActionEvent event){
        screenUtils.changeScrene(Screens.CAMERA, (Node) event.getSource());
    }

    public void exitApplication(ActionEvent event){
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }

    public void openSettings(ActionEvent event) {
        screenUtils.changeScrene(Screens.SETTINGS, (Node) event.getSource());
    }
}
