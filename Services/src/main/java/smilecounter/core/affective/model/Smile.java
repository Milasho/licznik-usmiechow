package smilecounter.core.affective.model;

import java.io.Serializable;

public class Smile extends HumanFeature implements Serializable {
    private double confidence;

    public Smile(){}

    public Smile(double x, double y, double width, double height, double confidence){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.confidence = confidence;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("SMILE: [{X: ").append(x);
        sb.append(", Y: ").append(y);
        sb.append("}, {WIDTH: ").append(width);
        sb.append(", HEIGHT: ").append(height);
        sb.append("}]");
        return sb.toString();
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"x\":\"").append(x).append("\"");
        sb.append(",").append("\"y\":\"").append(y).append("\"");
        sb.append(",").append("\"width\":\"").append(width).append("\"");
        sb.append(",").append("\"height\":\"").append(height).append("\"");
        sb.append(",").append("\"confidence\":\"").append(confidence).append("\"");
        sb.append("}");
        return sb.toString();
    }
}
