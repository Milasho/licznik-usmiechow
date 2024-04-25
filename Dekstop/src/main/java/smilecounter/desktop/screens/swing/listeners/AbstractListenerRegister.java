package smilecounter.desktop.screens.swing.listeners;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import java.util.ArrayList;
import java.util.List;

@Dependent
public class AbstractListenerRegister<T>{
    private List<T> listeners;

    @PostConstruct
    public void init(){
        listeners = new ArrayList<>();
    }

    public void addLocaleListener(T listener){
        listeners.add(listener);
    }

    public List<T> getListeners(){
        return this.listeners;
    }

}