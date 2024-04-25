package smilecounter.desktop.screens.fxml.controllers;

import com.github.sarxos.webcam.Webcam;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smilecounter.core.affective.model.Face;
import smilecounter.desktop.config.UserSettings;
import smilecounter.desktop.model.DetectedFace;
import smilecounter.desktop.screens.fxml.Screens;
import smilecounter.desktop.services.EffectsManager;
import smilecounter.desktop.services.SmilesCounterService;
import smilecounter.desktop.services.SmilesDetector;
import smilecounter.desktop.utils.CameraUtils;
import smilecounter.desktop.utils.ScreenUtils;
import smilecounter.desktop.utils.WeldUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CameraController implements Initializable {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private ScreenUtils screenUtils = WeldUtils.getClassFromWeld(ScreenUtils.class);
    private SmilesDetector smilesDetector = WeldUtils.getClassFromWeld(SmilesDetector.class);

    private SmilesCounterService smilesCounterService = WeldUtils.getClassFromWeld(SmilesCounterService.class);
    private UserSettings userSettings = WeldUtils.getClassFromWeld(UserSettings.class);
    private EffectsManager effectsManager = WeldUtils.getClassFromWeld(EffectsManager.class);

    @FXML private ImageView activeEffectsImage;
    @FXML private ImageView snapshotImage;
    @FXML private ImageView detectedFacesImage;
    @FXML private Button backButton;
    @FXML private Label globalSmilesCounter;
    @FXML private Label cameraLoadingLabel;
    @FXML private Label cameraNotLoaded;

    private BufferedImage cameraSnapshot;
    private boolean recording = false;
    private CameraUtils cameraUtils;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initCamera();
        refreshStatistics();
    }

    public void backToMainMenu(ActionEvent actionEvent) {
        closeCamera();
        screenUtils.changeScrene(Screens.MAIN_MENU, (Node) actionEvent.getSource());
    }

    private void initCamera(){
        LOGGER.debug("Initializing camera...");
        Webcam webcam = userSettings.getCamera();
        if(webcam != null){
            LOGGER.debug("Camera initialized successfully - {}.", webcam);
            cameraLoadingLabel.setVisible(true);
            backButton.setDisable(true);
            getCameraInitializerThread().start();
            cameraUtils = new CameraUtils();
        }
        else{
            LOGGER.warn("Camera wasn't initialized successfully.");
            cameraNotLoaded.setVisible(true);
        }
    }

    private Thread getCameraInitializerThread(){
        Task<Void> webCamIntilizer = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Webcam webcam = userSettings.getCamera();
                Dimension[] sizes = webcam.getViewSizes();
                webcam.setViewSize(sizes[sizes.length - 1]);
                if(webcam.open()){
                    LOGGER.debug("Camera {} is now opened.", webcam);
                    backButton.setDisable(false);
                    startStreamingCamera();
                    recording = true;
                }
                else{
                    cameraNotLoaded.setVisible(true);
                }

                cameraLoadingLabel.setVisible(false);
                return null;
            }

        };
        return new Thread(webCamIntilizer);
    }

    private void refreshStatistics(){
        globalSmilesCounter.setText("Total smiles: " + smilesCounterService.getGlobalSmilesCounter().toString());
    }

    private void startStreamingCamera(){
        LOGGER.debug("Initializing threads for displaying camera frames.");
        ObjectProperty<Image> imageProperty = new SimpleObjectProperty<>();
        snapshotImage.imageProperty().bind(imageProperty);
        ObjectProperty<Image> imagePropertyForFaces = new SimpleObjectProperty<>();
        detectedFacesImage.imageProperty().bind(imagePropertyForFaces);
        ObjectProperty<Image> imagePropertyForEffects = new SimpleObjectProperty<>();
        activeEffectsImage.imageProperty().bind(imagePropertyForEffects);

        Thread paintingCameraSnapshots = createThreadForDisplayingImage(imageProperty);
        paintingCameraSnapshots.setDaemon(true);
        paintingCameraSnapshots.start();

        Thread paintingFaces = createThreadForPaintingFaces(imagePropertyForFaces);
        paintingFaces.setDaemon(true);
        paintingFaces.start();

        Thread paintingEffects = createThreadForPaintingEffects(imagePropertyForEffects);
        paintingEffects.setDaemon(true);
        //paintingEffects.start();
    }

    private Thread createThreadForDisplayingImage(final ObjectProperty<Image> imageProperty){
       Task<Void> task =  new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                LOGGER.debug("Thread for displaying image is called (recording: {}).", recording);
                while (recording) {
                    try {
                        Webcam webcam = userSettings.getCamera();
                        if ((cameraSnapshot = webcam.getImage()) != null) {
                            imageProperty.set(SwingFXUtils.toFXImage(cameraSnapshot, null));
                            cameraSnapshot.flush();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };

       return new Thread(task);
    }

    private Thread createThreadForPaintingFaces(final ObjectProperty<Image> imageProperty){
        Task<Void> task =  new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                LOGGER.debug("Thread for displaying faces is called (recording: {}).", recording);
                while (recording) {
                    try {
                        if(cameraSnapshot != null){
                            BufferedImage snap = cameraSnapshot;
                            List<DetectedFace> allFaces = smilesDetector.addFrame(snap);
                            List<Face> newSmiles = smilesDetector.getNewSmiles();
                            BufferedImage image = new BufferedImage(snap.getWidth(),
                                    snap.getHeight(), BufferedImage.TYPE_INT_ARGB);

                            Graphics2D g2d = image.createGraphics();

                            cameraUtils.drawFacesOnImage(allFaces, g2d, null, null);
                            effectsManager.handleEffects(allFaces);

                            if(newSmiles.size() > 0){
                                smilesCounterService.saveImage(newSmiles, snap);
                            }

                            imageProperty.set(SwingFXUtils.toFXImage(image, null));
                        }
                    } catch (Exception e) {
                        LOGGER.error("There was a problem with painting faces thread.", e);
                    }
                }
                return null;
            }
        };

        return new Thread(task);
    }

    private Thread createThreadForPaintingEffects(final ObjectProperty<Image> imageProperty){
        Task<Void> task =  new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                LOGGER.debug("Thread for displaying effects is called (recording: {}).", recording);
                while (recording) {
                    try {
                        if(cameraSnapshot != null){
                            BufferedImage image = new BufferedImage(cameraSnapshot.getWidth(),
                                    cameraSnapshot.getHeight(), BufferedImage.TYPE_INT_ARGB);
                            Graphics2D g2d = image.createGraphics();

                            effectsManager.displayCurrentEffects(g2d, cameraSnapshot, null, null);
                            imageProperty.set(SwingFXUtils.toFXImage(image, null));
                            g2d.dispose();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };

        return new Thread(task);
    }


    private void closeCamera() {
        recording = false;
        Webcam webcam = userSettings.getCamera();
        if (webcam != null) {
            webcam.close();
        }
    }
}
