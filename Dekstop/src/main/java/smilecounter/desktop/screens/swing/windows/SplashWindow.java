package smilecounter.desktop.screens.swing.windows;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smilecounter.core.utils.ResourcesLoader;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class SplashWindow extends JFrame {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private static final String SPLASH_IMAGE = "images/splash.png";

    private Image image = null;

    public SplashWindow(){
        setUndecorated(true);
        loadImage();
        this.setSize(new Dimension(700,300));
        this.setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        this.setVisible(true);
    }

    private void loadImage(){
        try {
            image = ResourcesLoader.getImage(SPLASH_IMAGE, getClass());
        } catch (IOException e) {
            LOGGER.error("Error during loading image resource (splash): {}. ", SPLASH_IMAGE, e);
        }

        if(image != null){
            setContentPane(new JLabel(new ImageIcon(image)));
            getContentPane().setBackground(new Color(1.0f,1.0f,1.0f,0.0f));
            setBackground(new Color(1.0f,1.0f,1.0f,0.0f));
        }
    }
}
