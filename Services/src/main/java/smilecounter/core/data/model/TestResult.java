package smilecounter.core.data.model;

import org.jongo.marshall.jackson.oid.MongoId;
import org.jongo.marshall.jackson.oid.MongoObjectId;

import java.io.Serializable;
import java.util.List;

public class TestResult implements Serializable {
    private List<ServiceTestResult> serviceTestResults;
    private Integer gender;
    private Integer age;
    private Integer affectiveFuture;
    private Integer groupingSmiles;
    private String additionalData;

    @MongoObjectId @MongoId private String id;

    public TestResult(){}

    public List<ServiceTestResult> getServiceTestResults() {
        return serviceTestResults;
    }

    public void setServiceTestResults(List<ServiceTestResult> serviceTestResults) {
        this.serviceTestResults = serviceTestResults;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getAffectiveFuture() {
        return affectiveFuture;
    }

    public void setAffectiveFuture(Integer affectiveFuture) {
        this.affectiveFuture = affectiveFuture;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }

    public Integer getGroupingSmiles() {
        return groupingSmiles;
    }

    public void setGroupingSmiles(Integer groupingSmiles) {
        this.groupingSmiles = groupingSmiles;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
