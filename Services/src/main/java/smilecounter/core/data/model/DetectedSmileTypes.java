package smilecounter.core.data.model;

import java.io.Serializable;

public class DetectedSmileTypes implements Serializable{
    private boolean wideOpen;
    private boolean open;
    private boolean closed;
    private boolean nothing;

    public DetectedSmileTypes(){}
    public boolean isWideOpen() {
        return wideOpen;
    }

    public void setWideOpen(boolean wideOpen) {
        this.wideOpen = wideOpen;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public boolean isNothing() {
        return nothing;
    }

    public void setNothing(boolean nothing) {
        this.nothing = nothing;
    }

    @Override
    public String toString(){
        return "[" + wideOpen + "," + open + "," + closed + "," + nothing + "," + "]";
    }
}
