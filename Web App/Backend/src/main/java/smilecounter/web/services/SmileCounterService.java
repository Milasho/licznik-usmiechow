package smilecounter.web.services;

import org.apache.commons.lang.StringUtils;
import smilecounter.core.affective.enums.AffectiveServices;
import smilecounter.core.affective.model.Face;
import smilecounter.core.data.model.*;
import smilecounter.web.model.ChartsData;
import smilecounter.web.model.ChartsDataService;
import smilecounter.web.model.SmilesPhotos;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;

@ApplicationScoped
public class SmileCounterService {
    @Inject private AffectiveService affectiveService;
    @Inject private DatabaseService databaseService;

    public List<Face> detectSmiles(Snapshot snapshot, AffectiveServices service){
        List<Face> result = null;
        if(snapshot != null && StringUtils.isNotEmpty(snapshot.getContent())){
            String base64 = snapshot.getContent().split(",")[1];
            result = affectiveService.detectFaces(base64, service);
        }

        if(result != null && result.size() > 0 && Boolean.TRUE.equals(snapshot.isPermissionToSave())){
            Snapshot snapshotToSave = new Snapshot();
            snapshotToSave.setDetectedSmiles(result);
            snapshotToSave.setDate(snapshot.getDate());
            snapshotToSave.setContent(snapshot.getContent());
            databaseService.saveSnapshot(snapshotToSave);
        }

        return result;
    }

    public Snapshot getSmile(String id){
        return databaseService.getSmile(id);
    }

    public List<Snapshot> getSmilesWithPhoto(){
        return databaseService.getSmilesWithPhoto();
    }

    public Long getGlobalSmilesCounter(){
        return databaseService.getGlobalSmilesCounter();
    }

    public Long getGlobalSmilesCounter(Long dateFrom, Long dateTo) {
        Date from = convertToDate(dateFrom);
        Date to = convertToDate(dateTo);
        return databaseService.getGlobalSmilesCounter(from, to);
    }

    private Date convertToDate(Long timestamp){
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(timestamp);
        return date.getTime();
    }

    public List<LocalisationData> getBestLocalisations(Integer limit) {
        return databaseService.getBestLocalisations(limit);
    }

    private List<SmilesOnDay> getSmilesPerDayBetweenDates(Long dateFrom, Long dateTo){
        Date from = convertToDate(dateFrom);
        Date to = convertToDate(dateTo);
        return databaseService.getSmilesPerDayBetweenDates(from, to);
    }

    public ChartsData getChartsData(Long dateFrom, Long dateTo) {
        ChartsData data = new ChartsData();
        data.setSmilesChart(getSmilesPerDayBetweenDates(dateFrom, dateTo));
        long withPhoto = databaseService.getSmilesCountWithPhoto();
        long all = databaseService.getGlobalSmilesCounter();
        SmilesPhotos smilesPhotos = new SmilesPhotos();
        smilesPhotos.setSmilesWithoutPhoto(all - withPhoto);
        smilesPhotos.setSmilesWithPhoto(withPhoto);
        data.setPhotosChart(smilesPhotos);
        data.setServicesChart(prepareTestResults(databaseService.getServiceTestResults()));
        return data;
    }

    private List<ChartsDataService> prepareTestResults(List<TestResult> allResults){
        List<ChartsDataService> result = new ArrayList<>();

        if(allResults != null && allResults.size() > 0){
            Map<AffectiveServices, ServiceTestResult> values = new HashMap<>();
            Map<AffectiveServices, Long> counts = new HashMap<>();

            for(TestResult testResult : allResults){
                List<ServiceTestResult> serviceTestResults = testResult.getServiceTestResults();

                for(ServiceTestResult serviceTestResult : serviceTestResults){
                    ServiceTestResult actual = values.getOrDefault(serviceTestResult.getService(), new ServiceTestResult());
                    Long count = counts.getOrDefault(serviceTestResult.getService(), (long) 0);
                    sumServicesTest(serviceTestResult, actual);
                    counts.put(serviceTestResult.getService(), count + 1);
                    values.put(serviceTestResult.getService(), actual);
                }
            }

            Collection<ServiceTestResult> valuesSum = values.values();
            for(ServiceTestResult serviceTestResult : valuesSum){
                Long count = counts.get(serviceTestResult.getService());
                ChartsDataService r = averageServiceResult(serviceTestResult, count);
                result.add(r);
            }
        }

        return result;
    }

    private ChartsDataService averageServiceResult(ServiceTestResult serviceTestResult, Long count) {
        ChartsDataService r = new ChartsDataService();
        r.setService(serviceTestResult.getService());
        r.setAverageResponseTime(serviceTestResult.getAverageResponseTime() / count);
        r.setDetectedSmiles((double)serviceTestResult.getDetectedSmiles() / count);
        r.setTimeSpent((double)serviceTestResult.getTimeSpent() / count);
        r.setSmiling((double)serviceTestResult.getSmiling() / count);
        r.setNotSmiling((double)serviceTestResult.getNotSmiling() / count);
        return r;
    }

    private void sumServicesTest(ServiceTestResult serviceTestResult, ServiceTestResult actual) {
        actual.setService(serviceTestResult.getService());
        actual.setAverageResponseTime(actual.getAverageResponseTime() + serviceTestResult.getAverageResponseTime());
        if(serviceTestResult.getDetectedSmiles() != null){
            Integer detected = actual.getDetectedSmiles() != null ? actual.getDetectedSmiles() : 0;
            actual.setDetectedSmiles(detected + serviceTestResult.getDetectedSmiles());
        }
        if(serviceTestResult.getTimeSpent() != null){
            long timeSpent = actual.getTimeSpent() != null ? actual.getTimeSpent() : 0;
            actual.setTimeSpent(timeSpent + serviceTestResult.getTimeSpent());
        }
        if(serviceTestResult.getSmiling() != null){
            Integer smiling = actual.getSmiling() != null ? actual.getSmiling() : 0;
            actual.setSmiling(smiling + serviceTestResult.getSmiling());
        }
        if(serviceTestResult.getNotSmiling() != null){
            Integer notSmiling = actual.getNotSmiling() != null ? actual.getNotSmiling() : 0;
            actual.setNotSmiling(notSmiling + serviceTestResult.getNotSmiling());
        }
    }

    public void saveSnapshots(List<Snapshot> snapshotsToSave) {
        databaseService.saveSnapshots(snapshotsToSave);
    }
}
