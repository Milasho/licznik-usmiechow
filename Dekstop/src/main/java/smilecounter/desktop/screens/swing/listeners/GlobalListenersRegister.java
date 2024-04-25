package smilecounter.desktop.screens.swing.listeners;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class GlobalListenersRegister {
    @Inject
    private AbstractListenerRegister<LocaleChangeListener> localeChangeRegister;

    public void localeChangeEvent(){
        List<LocaleChangeListener> listeners = localeChangeRegister.getListeners();
        for(LocaleChangeListener listener : listeners){
            listener.localeChanged();
        }
    }

    public void addLocaleChangeListener(LocaleChangeListener listener){
        localeChangeRegister.addLocaleListener(listener);
    }
}