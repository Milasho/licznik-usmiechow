package smilecounter.desktop.screens.swing.listeners;

public interface LocaleChangeListener {
    void refreshTextsFromLocales();

    default void localeChanged(){
        refreshTextsFromLocales();
    };
}