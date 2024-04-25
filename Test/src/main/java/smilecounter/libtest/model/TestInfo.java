package smilecounter.libtest.model;

import smilecounter.core.affective.model.Face;
import smilecounter.core.affective.model.Smile;
import smilecounter.libtest.utils.TestHelper;

import java.util.ArrayList;
import java.util.List;

public class TestInfo {
    private String serviceName;
    private double averageDetectionTime;
    private long foundFaces;
    private long foundSmiles;
    private long foundMouths;
    private long moreThanOneFaceOnSinglePhoto;
    private double averageMouthWidth;
    private double averageFaceWidth;
    private double averageFaceHeight;
    private double averageMouthHeight;
    private long tests;
    private List<String> smiles;
    
    public TestInfo(String serviceName){
        this.serviceName = serviceName;
        this.averageDetectionTime = 0;
        this.foundFaces = 0;
        this.foundSmiles = 0;
        this.foundMouths = 0;
        this.tests = 0;
        this.smiles = new ArrayList<>();
    }
       
    public double getAverageDetectionTime() {
        return averageDetectionTime;
    }

    public void setAverageDetectionTime(double averageDetectionTime) {
        this.averageDetectionTime = averageDetectionTime;
    }

    private long getFoundFaces() {
        return foundFaces;
    }

    private void setFoundFaces(long foundFaces) {
        this.foundFaces = foundFaces;
    }

    private long getFoundSmiles() {
        return foundSmiles;
    }

    private void setFoundSmiles(long foundSmiles) {
        this.foundSmiles = foundSmiles;
    }
    
    public void updateFrom(TestResult result, double smileConfidenceTreshold){
        setAverageDetectionTime(getAverageDetectionTime() + result.getDetectionTime());
        setFoundFaces(getFoundFaces() + result.getFoundFaces().size());
        setFoundSmiles(getFoundSmiles() + TestHelper.countFoundSmiles(result.getFoundFaces(), smileConfidenceTreshold));
        setFoundMouths(getFoundMouths() + TestHelper.countFoundMouths(result.getFoundFaces()));
        if(result.getFoundFaces().size() > 1){
            moreThanOneFaceOnSinglePhoto++;
        }
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        
        sb.append("[Service name: ").append(this.serviceName.toUpperCase()).append("]:\n");
        sb.append("{");
        sb.append("\n\tAverage detection time: ").append(this.averageDetectionTime);
        sb.append("\n\tFound faces: ").append(this.foundFaces);
        sb.append("\n\tFound smiles: ").append(this.foundSmiles);
        sb.append("\n\tFound mouths: ").append(this.foundMouths);
        sb.append("\n\tFound multiple faces: ").append(this.moreThanOneFaceOnSinglePhoto);
        sb.append("\n\tTests: ").append(this.tests);
        sb.append("\n\tAverage mouth size: ").append(this.averageMouthWidth / this.tests)
                .append("px x ").append(this.averageMouthHeight / this.tests ).append("px");
        sb.append("\n\tAverage face size: ").append(this.averageFaceWidth / this.tests).append("px x ")
                .append(this.averageFaceHeight / this.tests).append("px");
        sb.append("\n\tImages with smile: ").append(String.join("\n\t\t", smiles));
        sb.append("\n}");
        return sb.toString();
    }

    private long getFoundMouths() {
        return foundMouths;
    }

    private void setFoundMouths(long foundMouths) {
        this.foundMouths = foundMouths;
    }

    public void updateFrom(TestResult testResult, double smileConfidenceTreshold, String img) {
        setAverageDetectionTime(getAverageDetectionTime() + testResult.getDetectionTime());
        setFoundFaces(getFoundFaces() + testResult.getFoundFaces().size());
        long foundSmiles = TestHelper.countFoundSmiles(testResult.getFoundFaces(), smileConfidenceTreshold);
        setFoundSmiles(getFoundSmiles() + foundSmiles);
        setFoundMouths(getFoundMouths() + TestHelper.countFoundMouths(testResult.getFoundFaces()));
        if(foundSmiles > 0){
            smiles.add(img);
        }
        if(testResult.getFoundFaces().size() > 1){
            moreThanOneFaceOnSinglePhoto++;
        }
        updateAverageSizes(testResult);
        tests++;
    }

    private void updateAverageSizes(TestResult result){
        List<Face> faces = result.getFoundFaces();

        double averageMouthWidth = 0;
        double averageFaceWidth = 0;
        double averageFaceHeight = 0;
        double averageMouthHeight = 0;
        long facesCount = faces.size();

        for(Face face : faces){
            averageFaceHeight += face.getHeight();
            averageFaceWidth += face.getWidth();

            Smile smile = face.getSmile();
            if(smile != null){
                averageMouthHeight += smile.getHeight();
                averageMouthWidth += smile.getWidth();
            }
        }

        this.averageMouthWidth += averageMouthWidth / facesCount;
        this.averageFaceWidth += averageFaceWidth / facesCount;
        this.averageFaceHeight += averageFaceHeight / facesCount;
        this.averageMouthHeight += averageMouthHeight / facesCount;
    }
}
