package smilecounter.web.model;

import java.io.Serializable;

public class SmilesPhotos implements Serializable {
    private long smilesWithoutPhoto;
    private long smilesWithPhoto;

    public SmilesPhotos(){}

    public long getSmilesWithoutPhoto() {
        return smilesWithoutPhoto;
    }

    public void setSmilesWithoutPhoto(long smilesWithoutPhoto) {
        this.smilesWithoutPhoto = smilesWithoutPhoto;
    }

    public long getSmilesWithPhoto() {
        return smilesWithPhoto;
    }

    public void setSmilesWithPhoto(long smilesWithPhoto) {
        this.smilesWithPhoto = smilesWithPhoto;
    }
}
