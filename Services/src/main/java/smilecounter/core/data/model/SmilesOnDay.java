package smilecounter.core.data.model;

import java.io.Serializable;

public class SmilesOnDay implements Serializable{
    private String date;
    private long smiles;

    public SmilesOnDay(){}

    public long getSmiles() {
        return smiles;
    }

    public void setSmiles(long smiles) {
        this.smiles = smiles;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
