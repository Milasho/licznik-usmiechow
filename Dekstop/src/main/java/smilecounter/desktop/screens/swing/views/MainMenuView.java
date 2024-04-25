package smilecounter.desktop.screens.swing.views;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smilecounter.core.utils.ResourcesLoader;
import smilecounter.desktop.screens.swing.common.LanguageManager;
import smilecounter.desktop.screens.swing.common.ViewsCommon;
import smilecounter.desktop.screens.swing.common.ViewsEnum;
import smilecounter.desktop.screens.swing.listeners.GlobalListenersRegister;
import smilecounter.desktop.screens.swing.listeners.LocaleChangeListener;
import smilecounter.desktop.utils.WeldUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static javax.swing.GroupLayout.Alignment.CENTER;
import static javax.swing.GroupLayout.Alignment.TRAILING;

public class MainMenuView extends JPanel implements LocaleChangeListener {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final long serialVersionUID = 1L;
    private static final String LOGO_IMAGE = "images/main_menu_bg.png";
    private static final Font TITLE_FONT = new Font("Non-serif", Font.BOLD, 55);

    private ViewsCommon viewsCommon = WeldUtils.getClassFromWeld(ViewsCommon.class);
    private LanguageManager languageManager = WeldUtils.getClassFromWeld(LanguageManager.class);

    private JButton settingsButton;
    private JButton cameraButton;
    private JButton exitButton;
    private JLabel logo;
    private JLabel title;

    public MainMenuView(){
        loadImage();
        GlobalListenersRegister listenersRegister = WeldUtils.getClassFromWeld(GlobalListenersRegister.class);
        listenersRegister.addLocaleChangeListener(this);
        initGui();
    }

    private void loadImage(){
        Image image = null;
        try {
            image = ResourcesLoader.getImage(LOGO_IMAGE, getClass());
        } catch (IOException e) {
            LOGGER.error("Error during loading image resource (logo): {}. ", LOGO_IMAGE, e);
        }

        if(image != null){
           logo = new JLabel(new ImageIcon(image));
        }
        else{
            logo = new JLabel();
        }
    }

    private void initGui(){
        settingsButton = new JButton();
        settingsButton.addActionListener(viewsCommon.getChangeViewListener(ViewsEnum.SETTINGS));

        cameraButton = new JButton();
        cameraButton.addActionListener(viewsCommon.getChangeViewListener(ViewsEnum.CAMERA));

        exitButton = new JButton();
        exitButton.addActionListener(viewsCommon.getExitApplicationListener());

        title = new JLabel();
        title.setFont(TITLE_FONT);

        initLayout();
        refreshTextsFromLocales();

        this.setVisible(true);
    }

    private void initLayout(){
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // Set the vertical layout
        layout.setVerticalGroup(layout.createParallelGroup()
                .addComponent(title)
                .addComponent(logo)
                .addGroup(
                    layout.createSequentialGroup()
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cameraButton)
                            .addComponent(settingsButton)
                            .addComponent(exitButton)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                )
        );

        // Set the horizontal layout
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(CENTER)
                        .addComponent(title)
                        .addComponent(logo)
                )
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(TRAILING)
                        .addComponent(cameraButton)
                        .addComponent(settingsButton)
                        .addComponent(exitButton)
                )
        );
    }

    @Override
    public void refreshTextsFromLocales() {
        settingsButton.setText(languageManager.getLocale("mainMenu.buttons.settings"));
        cameraButton.setText(languageManager.getLocale("mainMenu.buttons.cameraView"));
        exitButton.setText(languageManager.getLocale("mainMenu.buttons.exit"));
        title.setText(languageManager.getLocale("application.title"));
    }
}
