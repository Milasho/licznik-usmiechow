package smilecounter.core.data.connectors.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smilecounter.core.data.interfaces.DatabaseConnector;
import smilecounter.core.data.model.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class SimpleDatabaseConnector implements DatabaseConnector {
    private final static String USER_HOME_DIR = System.getProperty("user.home");
    private final static String DB_FILE_FORMAT = "%s/.smilecounter/database.dat";
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private SimpleDatabaseOperations operations;
    private String databaseFile;

    @PostConstruct
    public void init(){
        databaseFile  = String.format(DB_FILE_FORMAT, USER_HOME_DIR);
        LOGGER.info("Simple database will store data in {}", databaseFile);
        operations = new SimpleDatabaseOperations(databaseFile);
    }

    @PreDestroy
    public void destroy(){
        operations.saveAllSmiles(databaseFile);
    }

    @Override
    public void saveSnapshot(Snapshot snapshot) {
        if(snapshot.getDetectedSmiles() != null){
            operations.saveNewSmiles(snapshot.getDetectedSmiles().size());
        }
    }

    @Override
    public void saveTestResult(TestResult testResult) {
    }

    @Override
    public long getSmilesCount() {
        return operations.getAllSmiles();
    }

    @Override
    public long getSmilesCount(Date from, Date to) {
        return operations.getSmiles(from, to);
    }

    @Override
    public long getSmilesCountFromLocalisation(String localisation) {
        return operations.getAllSmiles();
    }

    @Override
    public long getSmilesCountFromLocalisation(String localisation, Date from, Date to) {
        return operations.getSmiles(from, to);
    }

    @Override
    public List<Snapshot> getSmiles() {
        return null;
    }

    @Override
    public List<Snapshot> getSmilesWithPhoto() {
        return null;
    }

    @Override
    public Snapshot getSmile(String id) {
        return null;
    }

    @Override
    public List<LocalisationData> getBestLocalisations(Integer limit) {
        return null;
    }

    @Override
    public List<SmilesOnDay> getSmilesPerDayBetweenDates(Date from, Date to) {
        return null;
    }

    @Override
    public long getSmilesCountWithPhoto() {
        return 0;
    }

    @Override
    public List<TestResult> getServiceTestResults() {
        return null;
    }

    @Override
    public void saveSnapshots(List<Snapshot> snapshotsToSave) {

    }
}
