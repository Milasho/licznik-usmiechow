package smilecounter.core.data.connectors.mongodb;

import com.mongodb.QueryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.jongo.Aggregate;
import org.jongo.MongoCollection;
import smilecounter.core.data.connectors.mongodb.enums.MongoCollections;
import smilecounter.core.data.connectors.mongodb.utils.MongoOperations;
import smilecounter.core.data.interfaces.DatabaseConnector;
import smilecounter.core.data.model.LocalisationData;
import smilecounter.core.data.model.SmilesOnDay;
import smilecounter.core.data.model.Snapshot;
import smilecounter.core.data.model.TestResult;

import javax.enterprise.context.ApplicationScoped;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class MongoDatabaseConnector implements DatabaseConnector{
    private MongoOperations database;

    public void init(String connectionUrl) throws UnknownHostException {
        database = new MongoOperations();
        database.startConnection(connectionUrl);
    }

    public void close(){
       if(database != null){
           database.stopConnection();
       }
    }

    @Override
    public void saveSnapshot(Snapshot snapshot) {
        database.insert(MongoCollections.SMILES.getName(), snapshot);
    }

    @Override
    public void saveTestResult(TestResult testResult) {
        database.insert(MongoCollections.TESTS.getName(), testResult);
    }

    @Override
    public long getSmilesCount() {
        return database.count(MongoCollections.SMILES.getName());
    }

    @Override
    public long getSmilesCount(Date from, Date to) {
        QueryBuilder query = QueryBuilder.start("date").greaterThanEquals(from).and("date").lessThanEquals(to);
        return database.count(MongoCollections.SMILES.getName(), query.get().toString());
    }

    @Override
    public long getSmilesCountFromLocalisation(String localisation) {
        String withContentQuery = "{localisation: {$eq : '" + localisation + "'}}";
        return database.count(MongoCollections.SMILES.getName(), withContentQuery);
    }

    @Override
    public long getSmilesCountFromLocalisation(String localisation, Date from, Date to) {
        QueryBuilder query = QueryBuilder.start("date").greaterThanEquals(from).and("date").lessThanEquals(to)
                .and("localisation").is(localisation);
        return database.count(MongoCollections.SMILES.getName(), query.get().toString());
    }

    @Override
    public List<Snapshot> getSmiles() {
        return database.find(MongoCollections.SMILES.getName(), Snapshot.class);
    }

    @Override
    public List<Snapshot> getSmilesWithPhoto() {
        String withContentQuery = "{content: {$ne : null}}";
        String sortDesc = "{_id: -1}";
        return database.find(MongoCollections.SMILES.getName(), Snapshot.class, withContentQuery, sortDesc);
    }

    @Override
    public Snapshot getSmile(String id) {
        return database.findOne(MongoCollections.SMILES.getName(), Snapshot.class, id);
    }

    @Override
    public List<LocalisationData> getBestLocalisations(Integer limit) {
        MongoCollection col = database.getDriver(MongoCollections.SMILES.getName());

        Aggregate aggregate = col.aggregate("{$group: {_id: '$localisation', smiles: {$sum: 1}}}")
                .and("{$sort: {smiles: -1}}");

        if(limit != null){
            aggregate = aggregate.and("{$limit:" + limit +"}");
        }

        Aggregate.ResultsIterator<LocalisationData> cursor = aggregate.as(LocalisationData.class);

        List<LocalisationData> result = new ArrayList<>();
        while(cursor.hasNext()){
            result.add(cursor.next());
        }
        return result;
    }

    @Override
    public List<SmilesOnDay> getSmilesPerDayBetweenDates(Date from, Date to) {
        MongoCollection col = database.getDriver(MongoCollections.SMILES.getName());

        String group = "{$group: {_id: {year : {$year: '$date'}, month: {$month: '$date'}, day: {$dayOfMonth: '$date'}}, smiles:{$sum: 1}, date:{$first: '$date'}}}";
        String sort = "{$sort: {'date': 1}}";
        QueryBuilder query = QueryBuilder.start("date").greaterThanEquals(from).and("date").lessThanEquals(to);
        String match = "{$match: " + query.get().toString() + "}";
        Aggregate aggregate = col.aggregate(match).and(group).and(sort);

        Aggregate.ResultsIterator<SmilesOnDay> cursor = aggregate.as(SmilesOnDay.class);

        List<SmilesOnDay> result = new ArrayList<>();
        while(cursor.hasNext()){
            result.add(cursor.next());
        }
        return result;
    }

    @Override
    public long getSmilesCountWithPhoto() {
        String withContentQuery = "{content: {$ne : null}}";
        return database.count(MongoCollections.SMILES.getName(), withContentQuery);
    }

    @Override
    public List<TestResult> getServiceTestResults() {
        return database.find(MongoCollections.TESTS.getName(), TestResult.class);
    }

    @Override
    public void saveSnapshots(List<Snapshot> snapshotsToSave) {
        MongoCollection driver = database.getDriver(MongoCollections.SMILES.getName());
        for(Snapshot snapshot : snapshotsToSave){
            if(StringUtils.isNotEmpty(snapshot.getContent())){
                driver.update("{content: '"+ snapshot.getContent() + "'}").upsert().with(snapshot);
            }
            else{
                driver.insert(snapshot);
            }
        }
    }
}
