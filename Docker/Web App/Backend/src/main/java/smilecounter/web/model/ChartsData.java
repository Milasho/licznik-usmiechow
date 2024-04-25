package smilecounter.web.model;

import smilecounter.core.data.model.SmilesOnDay;

import java.io.Serializable;
import java.util.List;

public class ChartsData implements Serializable{
    private List<SmilesOnDay> smilesChart;
    private SmilesPhotos photosChart;
    private List<ChartsDataService> servicesChart;

    public ChartsData(){}


    public List<SmilesOnDay> getSmilesChart() {
        return smilesChart;
    }

    public void setSmilesChart(List<SmilesOnDay> smilesChart) {
        this.smilesChart = smilesChart;
    }

    public SmilesPhotos getPhotosChart() {
        return photosChart;
    }

    public void setPhotosChart(SmilesPhotos photosChart) {
        this.photosChart = photosChart;
    }

    public List<ChartsDataService> getServicesChart() {
        return servicesChart;
    }

    public void setServicesChart(List<ChartsDataService> servicesChart) {
        this.servicesChart = servicesChart;
    }
}
