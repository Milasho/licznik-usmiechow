package smilecounter.desktop.screens.swing.components;

import com.github.sarxos.webcam.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smilecounter.core.affective.model.Face;
import smilecounter.desktop.config.UserSettings;
import smilecounter.desktop.model.DetectedFace;
import smilecounter.desktop.services.EffectsManager;
import smilecounter.desktop.services.SmilesCounterService;
import smilecounter.desktop.services.SmilesDetector;
import smilecounter.desktop.utils.CameraUtils;
import smilecounter.desktop.utils.WeldUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class WebcamComponent extends JPanel implements  WebcamListener, Thread.UncaughtExceptionHandler, WebcamPanel.Painter, WebcamImageTransformer {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final long serialVersionUID = 1L;

    private UserSettings userSettings = WeldUtils.getClassFromWeld(UserSettings.class);
    private SmilesDetector smilesDetector = WeldUtils.getClassFromWeld(SmilesDetector.class);
    private EffectsManager effectsManager = WeldUtils.getClassFromWeld(EffectsManager.class);
    private SmilesCounterService smilesCounterService = WeldUtils.getClassFromWeld(SmilesCounterService.class);

    private CameraUtils cameraUtils;

    private WebcamPanel panel;
    private Webcam webcam;
    private WebcamPanel.Painter painter;

    private List<DetectedFace> detectedFaces;
    private SmilecounterComponent smileCounter;

    public void start() {
        cameraUtils = new CameraUtils();
        refreshCamera();

        createThreadDetectingFaces().start();

        panel.setPainter(this);
        painter = panel.getDefaultPainter();
        this.setVisible(true);

        smilesDetector.clear();
        effectsManager.clear();

        this.setBackground(Color.BLACK);
    }

    private void refreshCamera() {
        if (panel != null) {
            this.remove(panel);
        }

        webcam = userSettings.getCamera();
        webcam.addWebcamListener(this);
        webcam.setImageTransformer(this);
        webcam.setViewSize(WebcamResolution.VGA.getSize());

        panel = new WebcamPanel(webcam, false);
        panel.setFPSDisplayed(true);
        panel.setVisible(true);
        panel.setBackground(Color.GREEN);
        panel.setFPSLimit(20);
        panel.setMirrored(false);

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setVerticalGroup(layout.createParallelGroup().addComponent(panel));
        layout.setHorizontalGroup(layout.createParallelGroup().addComponent(panel));
    }

    public void stop() {
        if (panel != null && panel.isStarted()) {
            panel.stop();
        }
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.err.println(String.format("Exception in thread %s", t.getName()));
        e.printStackTrace();
    }

    @Override
    public void webcamOpen(WebcamEvent we) {
        LOGGER.debug("Webcam is now opened.");
    }

    @Override
    public void webcamClosed(WebcamEvent we) {
        LOGGER.debug("Webcam was correctly closed.");
    }

    @Override
    public void webcamDisposed(WebcamEvent we) {
        LOGGER.debug("Webcam id disposed.");
    }

    @Override
    public void webcamImageObtained(WebcamEvent we) {}

    @Override
    public BufferedImage transform(BufferedImage image) {
        return image;
    }

    @Override
    public void paintPanel(WebcamPanel panel, Graphics2D g2) {
        if (painter != null) {
            painter.paintPanel(panel, g2);
        }
    }

    @Override
    public void paintImage(WebcamPanel panel, BufferedImage image, Graphics2D g2) {
        if (painter != null) {
            painter.paintImage(panel, image, g2);
        }

        Dimension dim = new Dimension(image.getWidth(), image.getHeight());
        Rectangle scaledImageSize = getInnerWebcamImageSize(image);
        cameraUtils.drawFacesOnImage(detectedFaces, g2, scaledImageSize, dim);
        effectsManager.displayCurrentEffects(g2, image, scaledImageSize, dim);
    }

    private Rectangle getInnerWebcamImageSize(BufferedImage image){
        Rectangle result = new Rectangle();
        int pw = panel.getWidth();
        int ph = panel.getHeight();
        int iw = image.getWidth();
        int ih = image.getHeight();

        double s = Math.max((double)iw / (double)pw, (double)ih / (double)ph);
        double niw = (double)iw / s;
        double nih = (double)ih / s;
        double dx = ((double)pw - niw) / 2.0D;
        double dy = ((double)ph - nih) / 2.0D;
        result.width = (int)niw;
        result.height = (int)nih;
        result.x = (int)dx;
        result.y = (int)dy;
        return result;
    }

    private Thread createThreadDetectingFaces() {
        Thread t = new Thread(() -> {
            try{
                panel.start();
                while (webcam.isOpen()) {
                    BufferedImage image = webcam.getImage();
                    if (image == null) {break;}

                    detectedFaces = smilesDetector.addFrame(image);
                    List<Face> newSmiles = smilesDetector.getNewSmiles();

                    effectsManager.handleEffects(detectedFaces);
                    if (!newSmiles.isEmpty()) {
                        LOGGER.info("Found {} new smiles on frame.", newSmiles.size());
                        smilesCounterService.saveImage(newSmiles, image);
                        this.smileCounter.increaseSmilecounter(newSmiles.size());
                    }
                }
            }
            catch (Exception ignored){
            }
        });
        t.setDaemon(true);

        return t;
    }

    public void setSmileCounter(SmilecounterComponent smileCounter) {
        this.smileCounter = smileCounter;
    }
}