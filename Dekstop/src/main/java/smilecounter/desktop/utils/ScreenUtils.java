package smilecounter.desktop.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import smilecounter.core.utils.ResourcesLoader;
import smilecounter.desktop.screens.fxml.Screens;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;

@ApplicationScoped
public class ScreenUtils {
    public Scene openScreen(Screens screen){
        Parent root = null;
        try {
            root = FXMLLoader.load(ResourcesLoader.getResource(screen.getPath(), getClass()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Scene(root, 900, 690);
    }

    public void changeScrene(Screens screen, Node initiator){
        Stage stage = (Stage) initiator.getScene().getWindow();
        Scene settingsScene = openScreen(screen);
        stage.setScene(settingsScene);
        stage.show();
    }
}
