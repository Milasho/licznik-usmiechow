package smilecounter.core.data.model;

import smilecounter.core.affective.enums.AffectiveServices;

import java.io.Serializable;

public class ServiceTestResult implements Serializable {
    private AffectiveServices service;
    private Integer order;
    private Integer detectedSmiles;
    private Integer smiling;
    private Integer notSmiling;
    private Long timeSpent;
    private Integer generalDetection;
    private DetectedSmileTypes smileTypes;
    private String additionalData;
    private double averageResponseTime;

    public ServiceTestResult(){}

    public AffectiveServices getService() {
        return service;
    }

    public void setService(AffectiveServices service) {
        this.service = service;
    }

    public Integer getDetectedSmiles() {
        return detectedSmiles;
    }

    public void setDetectedSmiles(Integer detectedSmiles) {
        this.detectedSmiles = detectedSmiles;
    }

    public Integer getSmiling() {
        return smiling;
    }

    public void setSmiling(Integer smiling) {
        this.smiling = smiling;
    }

    public Integer getNotSmiling() {
        return notSmiling;
    }

    public void setNotSmiling(Integer notSmiling) {
        this.notSmiling = notSmiling;
    }

    public Long getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(Long timeSpent) {
        this.timeSpent = timeSpent;
    }

    public Integer getGeneralDetection() {
        return generalDetection;
    }

    public void setGeneralDetection(Integer generalDetection) {
        this.generalDetection = generalDetection;
    }

    public DetectedSmileTypes getSmileTypes() {
        return smileTypes;
    }

    public void setSmileTypes(DetectedSmileTypes smileTypes) {
        this.smileTypes = smileTypes;
    }

    public String getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public double getAverageResponseTime() {
        return averageResponseTime;
    }

    public void setAverageResponseTime(double averageResponseTime) {
        this.averageResponseTime = averageResponseTime;
    }
}
