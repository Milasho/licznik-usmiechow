package smilecounter.desktop.screens.swing.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smilecounter.desktop.screens.swing.views.CameraView;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

@ApplicationScoped
public class ViewsCommon {
    private final Logger LOGGER = LoggerFactory.getLogger(ViewsCommon.class);

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private CameraView cameraView;
    private JFrame mainFrame;

    @PostConstruct
    public void init(){
        setCardLayout(new CardLayout());
        setMainPanel(new JPanel());
    }

    public void setMainFrame(JFrame mainFrame){
        this.mainFrame = mainFrame;
    }

    public void setFullscreenMode(boolean isFullscreen){
        LOGGER.debug("Changing displaying mode to fullscreen: {}", isFullscreen);
        if(mainFrame != null){
            mainFrame.dispose();
            if(isFullscreen){
                mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
            else{
                mainFrame.setSize(new Dimension(700,760));
            }
            mainFrame.setUndecorated(isFullscreen);
            centreWindow(mainFrame);
            mainFrame.setVisible(true);
        }
    }

    private void centreWindow(JFrame frame) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
    }


    public void switchView(String viewName){
        LOGGER.debug("switchView - Switching view to {}", viewName);
        getCardLayout().show(mainPanel, viewName);

        if(ViewsEnum.CAMERA.equals(viewName)){
            cameraView.start();
        }
    }

    // Returns listener for button that allows to switch views in application
    public ActionListener getChangeViewListener(String viewName) {
        return arg0 -> switchView(viewName);
    }

    public CardLayout getCardLayout() {
        return cardLayout;
    }

    public void setCardLayout(CardLayout cardLayout) {
        this.cardLayout = cardLayout;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void setMainPanel(JPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    public CameraView getCameraView() {
        return cameraView;
    }

    public void setCameraView(CameraView cameraView) {
        this.cameraView = cameraView;
    }

    public ActionListener getExitApplicationListener() {
        return e -> {
            if(mainFrame != null) {
                LOGGER.info("Exiting application...");
                mainFrame.dispose();
                mainFrame.setVisible(false);
            }
        };
    }
}
