package smilecounter.core.data.connectors.mongodb.utils;

import com.mongodb.*;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.jongo.Find;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class MongoOperations {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private MongoClient mongo;
    private DB db;
    private Jongo jongo;

    @SuppressWarnings("deprecation")
    public void startConnection(String url) throws UnknownHostException {
        if(mongo == null){
            LOGGER.debug("startConnection - url: {}", url);
            mongo = new MongoClient(new MongoClientURI(url));
            db = mongo.getDB(getDatabaseName(url));
            jongo = new Jongo(db);
            LOGGER.debug("Connected to database.");
        }
    }

    public void stopConnection(){
        if(mongo != null){
            LOGGER.debug("Stopping connection with MongoDB database {}...", mongo.getAddress());
            try{
                mongo.close();
            }
            catch (Exception e){
                LOGGER.error("Exception during closing MongoDB: ", e);
            }
        }
        else{
            LOGGER.error("ERROR! There is no active MongoDB database to disconnect!");
        }
    }

    private String getDatabaseName(String url){
        String result = null;
        if(StringUtils.isNotEmpty(url)){
            String[] parts = url.split("/");
            if(parts.length > 0){
                result = parts[parts.length - 1];
            }
        }
        return result;
    }

    public DBCollection getCollection(String collection){
        return db.getCollection(collection);
    }

    public DBCursor getDocumentsByField(String collection, String fieldName, String fieldValue){
        DBCollection c = getCollection(collection);
        BasicDBObject query = new BasicDBObject();
        query.put(fieldName, fieldValue);
        return c.find(query);
    }

    public <T> List<T> find(String collection, Class<T> clazz){
        return find(collection, clazz, null, null);
    }

    public <T> List<T> find(String collection, Class<T> clazz, String query, String sort){
        MongoCollection col = jongo.getCollection(collection);
        List<T> result = new ArrayList<>();
        if(col != null){
            Find find = StringUtils.isNotEmpty(query) ? col.find(query) : col.find();
            if(StringUtils.isNotEmpty(sort)){
                find = find.sort(sort);
            }
            MongoCursor<T> cursor = find.as(clazz);
            while(cursor.hasNext()){
                result.add(cursor.next());
            }
            try {cursor.close();}
            catch (IOException e) {
                LOGGER.error("Error during closing cursor: {}", e);
            }
        }

        return result;
    }

    public void insert(String collection, Object object){
        MongoCollection col = jongo.getCollection(collection);
        col.insert(object);
    }

    public <T> T findOne(String collection, Class<T> clazz, String id){
        MongoCollection col = jongo.getCollection(collection);
        return col.findOne(new ObjectId(id)).as(clazz);
    }

    public long count(String collection){
        MongoCollection col = jongo.getCollection(collection);
        return col.count();
    }

    public long count(String collection, String query){
        MongoCollection col = jongo.getCollection(collection);
        return col.count(query);
    }

    public MongoCollection getDriver(String collection){
        return jongo.getCollection(collection);
    }
}
