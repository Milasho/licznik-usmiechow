package smilecounter.core.data.model;

import java.io.Serializable;

public class LocalisationData implements Serializable {
    private String _id;
    private Long smiles;

    public LocalisationData(){}

    public Long getSmiles() {
        return smiles;
    }

    public void setSmiles(Long smiles) {
        this.smiles = smiles;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
