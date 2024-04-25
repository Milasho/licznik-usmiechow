package smilecounter.web.services;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smilecounter.core.data.connectors.mongodb.MongoDatabaseConnector;
import smilecounter.core.data.connectors.mongodb.enums.MongoCollections;
import smilecounter.core.data.connectors.simple.SimpleDatabaseConnector;
import smilecounter.core.data.model.*;
import smilecounter.web.config.ApplicationConfiguration;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class DatabaseService {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Inject private ApplicationConfiguration appConfig;
    @Inject private SimpleDatabaseConnector simpleConnector;
    @Inject private MongoDatabaseConnector mongoConnector;

    private smilecounter.core.data.interfaces.DatabaseConnector databaseConnector;

    @PostConstruct
    public void init(){
        databaseConnector = simpleConnector;

        String url = appConfig.getDatabaseConnectionUrl();

        if(StringUtils.isNotEmpty(url)){
            try {
                mongoConnector.init(url);
                databaseConnector = mongoConnector;
            } catch (UnknownHostException e) {
                LOGGER.error("Error during initializing mongo db connection {} => ", url, e);
            }
        }
    }

    public Snapshot getSmile(String id){
        return databaseConnector.getSmile(id);
    }

    public List<Snapshot> getSmilesWithPhoto(){
        return databaseConnector.getSmilesWithPhoto();
    }

    public Long getGlobalSmilesCounter(){
        return databaseConnector.getSmilesCount();
    }

    public Long getGlobalSmilesCounter(Date dateFrom, Date dateTo){
        return databaseConnector.getSmilesCount(dateFrom, dateTo);
    }

    public void saveSnapshot(Snapshot snapshotToSave) {
        databaseConnector.saveSnapshot(snapshotToSave);
    }

    public void saveTestResult(TestResult testResult){
        databaseConnector.saveTestResult(testResult);
    }

    public List<LocalisationData> getBestLocalisations(Integer limit){
        return databaseConnector.getBestLocalisations(limit);
    }

    public List<SmilesOnDay> getSmilesPerDayBetweenDates(Date from, Date to){
        return databaseConnector.getSmilesPerDayBetweenDates(from, to);
    }

    public long getSmilesCountWithPhoto() {
        return databaseConnector.getSmilesCountWithPhoto();
    }

    public List<TestResult> getServiceTestResults() {
        return databaseConnector.getServiceTestResults();
    }

    public void saveSnapshots(List<Snapshot> snapshotsToSave) {
        databaseConnector.saveSnapshots(snapshotsToSave);
    }
}
