package smilecounter.core.data.interfaces;

import smilecounter.core.data.model.*;

import java.util.Date;
import java.util.List;

public interface DatabaseConnector {
    void saveSnapshot(Snapshot snapshot);
    void saveTestResult(TestResult testResult);
    long getSmilesCount();
    long getSmilesCount(Date from, Date to);
    long getSmilesCountFromLocalisation(String localisation);
    long getSmilesCountFromLocalisation(String localisation, Date from, Date to);
    List<Snapshot> getSmiles();
    List<Snapshot> getSmilesWithPhoto();
    Snapshot getSmile(String id);
    List<LocalisationData> getBestLocalisations(Integer limit);
    List<SmilesOnDay> getSmilesPerDayBetweenDates(Date from, Date to);
    long getSmilesCountWithPhoto();
    List<TestResult> getServiceTestResults();
    void saveSnapshots(List<Snapshot> snapshotsToSave);
}
