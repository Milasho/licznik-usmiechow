package smilecounter.desktop;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smilecounter.desktop.config.ApplicationInitializer;
import smilecounter.desktop.screens.fxml.Screens;
import smilecounter.desktop.utils.ScreenUtils;
import smilecounter.desktop.utils.WeldUtils;

public class MainFXML extends Application{
    public static void main(String[] args) throws Exception {
        ApplicationInitializer.initApplication(args);
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        ScreenUtils screenUtils = WeldUtils.getClassFromWeld(ScreenUtils.class);
        Scene scene = screenUtils.openScreen(Screens.MAIN_MENU);

        primaryStage.setTitle("Smilecounter");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
}
