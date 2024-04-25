package smilecounter.core.data.model;

import org.jongo.marshall.jackson.oid.MongoId;
import org.jongo.marshall.jackson.oid.MongoObjectId;
import smilecounter.core.affective.model.Face;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Snapshot implements Serializable{
    private static final long serialVersionUID = -201536052609210529L;
    private String content;
    private String author;
    private Date date;
    private Boolean permissionToSave;
    private String localisation;
    private List<Face> detectedSmiles;
    private FaceStatus faceStatus;
    private double width;
    private double height;

    @MongoObjectId @MongoId private String id;

    public Snapshot(){}

    public Snapshot(String content, String localisation){
        this.localisation = localisation;
        this.content = content;
    }

    public Snapshot(String content, List<Face> detectedSmiles, String localisation){
        this.content = content;
        this.detectedSmiles = detectedSmiles;
        this.date = new Date();
        this.permissionToSave = true;
        this.localisation = localisation;
    }

    public Snapshot(String author, String content, Date date, String localisation, boolean permissionToSave){
        this.author = author;
        this.content = content;
        this.date = date;
        this.permissionToSave = permissionToSave;
        this.localisation = localisation;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public Boolean isPermissionToSave() {
        return permissionToSave;
    }
    public void setPermissionToSave(Boolean permissionToSave) {
        this.permissionToSave = permissionToSave;
    }
    public String getLocalisation() {
        return localisation;
    }
    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("ImageSnapshot: [Author: ").append(author);
        if(date != null){
            sb.append(", date: ").append(date.toString());
        }
        sb.append(", content: ").append(content);
        sb.append("]");
        return sb.toString();
    }
    public List<Face> getDetectedSmiles() {
        return detectedSmiles;
    }
    public void setDetectedSmiles(List<Face> detectedSmiles) {
        this.detectedSmiles = detectedSmiles;
    }

    public String toJson(){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"author\":\"").append(author).append("\"");
        sb.append(",").append("\"date\":\"").append(date).append("\"");
        sb.append(",").append("\"localisation\":\"").append(localisation).append("\"");
        if(permissionToSave){
            sb.append(",").append("\"content\":\"").append(content).append("\"");
            if(detectedSmiles != null && detectedSmiles.size() > 0){
                sb.append(",").append("\"detectedSmiles\":[");
                Face firstFace = detectedSmiles.get(0);
                for(Face face : detectedSmiles){
                    if(firstFace != face){
                        sb.append(",");
                    }
                    sb.append(face.toJson());
                }
                sb.append("]");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FaceStatus getFaceStatus() {
        return faceStatus;
    }

    public void setFaceStatus(FaceStatus faceStatus) {
        this.faceStatus = faceStatus;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }
}
