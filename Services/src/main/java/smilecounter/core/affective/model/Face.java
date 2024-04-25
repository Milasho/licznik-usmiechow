package smilecounter.core.affective.model;

import java.io.Serializable;

public class Face extends HumanFeature implements Serializable {
    private Smile smile;
    private HumanFeature nose;

    public Face(){}

    public Face(double x, double y, double width, double height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("FACE: [{X: ").append(x);
        sb.append(", Y: ").append(y);
        sb.append("}, {WIDTH: ").append(width);
        sb.append(", HEIGHT: ").append(height);
        sb.append("}]");
        return sb.toString();
    }

    public Smile getSmile() {
        return smile;
    }

    public void setSmile(Smile smile) {
        this.smile = smile;
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"x\":\"").append(x).append("\"");
        sb.append(",").append("\"y\":\"").append(y).append("\"");
        sb.append(",").append("\"width\":\"").append(width).append("\"");
        sb.append(",").append("\"height\":\"").append(height).append("\"");
        sb.append(",").append("\"smile\":").append(smile.toJson());
        sb.append("}");
        return sb.toString();
    }

    public HumanFeature getNose() {
        return nose;
    }

    public void setNose(HumanFeature nose) {
        this.nose = nose;
    }
}
