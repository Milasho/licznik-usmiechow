package smilecounter.desktop.screens.fxml;

public enum Screens {
    CAMERA("/screens/camera.fxml"),
    MAIN_MENU("/screens/main_menu.fxml"),
    SETTINGS("/screens/settings.fxml");

    private final String path;

    Screens(String path) {
        this.path = path;
    }

    public String getPath(){
        return path;
    }
}
