package smilecounter.web.model;

import smilecounter.core.affective.enums.AffectiveServices;

import java.io.Serializable;

public class ChartsDataService implements Serializable{
    private AffectiveServices service;
    private double averageResponseTime;
    private double detectedSmiles;
    private double timeSpent;
    private double smiling;
    private double notSmiling;

    public ChartsDataService() { }

    public AffectiveServices getService() {
        return service;
    }

    public void setService(AffectiveServices service) {
        this.service = service;
    }

    public double getAverageResponseTime() {
        return averageResponseTime;
    }

    public void setAverageResponseTime(double averageResponseTime) {
        this.averageResponseTime = averageResponseTime;
    }

    public double getDetectedSmiles() {
        return detectedSmiles;
    }

    public void setDetectedSmiles(double detectedSmiles) {
        this.detectedSmiles = detectedSmiles;
    }

    public double getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(double timeSpent) {
        this.timeSpent = timeSpent;
    }

    public double getSmiling() {
        return smiling;
    }

    public void setSmiling(double smiling) {
        this.smiling = smiling;
    }

    public double getNotSmiling() {
        return notSmiling;
    }

    public void setNotSmiling(double notSmiling) {
        this.notSmiling = notSmiling;
    }
}
