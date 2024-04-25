package smilecounter.web.model;

import java.io.Serializable;

public class SimpleSnapshot implements Serializable {
    private static final long serialVersionUID = -6952652088624896724L;
    private String link;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
