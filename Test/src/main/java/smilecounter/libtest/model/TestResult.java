package smilecounter.libtest.model;

import smilecounter.core.affective.model.Face;

import java.util.List;

public class TestResult {
	private long detectionTime;
	private List<Face> foundFaces;
    public long getDetectionTime() {
        return detectionTime;
    }
    public void setDetectionTime(long detectionTime) {
        this.detectionTime = detectionTime;
    }
    public List<Face> getFoundFaces() {
        return foundFaces;
    }
    public void setFoundFaces(List<Face> foundFaces) {
        this.foundFaces = foundFaces;
    }
	
}
