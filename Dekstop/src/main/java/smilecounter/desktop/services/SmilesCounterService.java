package smilecounter.desktop.services;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smilecounter.core.affective.model.Face;
import smilecounter.core.data.connectors.mongodb.MongoDatabaseConnector;
import smilecounter.core.data.connectors.simple.SimpleDatabaseConnector;
import smilecounter.core.data.model.Snapshot;
import smilecounter.core.utils.ImagesHelper;
import smilecounter.desktop.config.ApplicationConfiguration;
import smilecounter.desktop.config.UserSettings;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class SmilesCounterService {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final static String BASE_64_FORMAT = "data:image/png;base64,%s";

    @Inject private ApplicationConfiguration appConfig;
    @Inject private SimpleDatabaseConnector simpleConnector;
    @Inject private MongoDatabaseConnector mongoConnector;
    @Inject private UserSettings userSettings;

    private smilecounter.core.data.interfaces.DatabaseConnector databaseConnector;

    public void init(){
        databaseConnector = simpleConnector;

        String url = appConfig.getDatabaseConnectionUrl();

        if(!appConfig.isDatabaseSimpleType() && StringUtils.isNotEmpty(url)){
            try {
                mongoConnector.init(url);
                databaseConnector = mongoConnector;
            } catch (UnknownHostException e) {
                LOGGER.error("Error during initializing mongo db connection {} => ", url, e);
            }
        }
    }

    public void connectToDatabase(boolean online) {
        boolean oldValue = appConfig.isDatabaseSimpleType();
        if(oldValue == online){
            if(!oldValue){
                mongoConnector.close();
            }
            appConfig.setDatabaseSimpleType(!online);
            init();
        }
    }

    public void saveImage(List<Face> detectedFaces, BufferedImage image){
        Thread t = new Thread(() -> {
           saveImageToDatabase(detectedFaces, image);
        });
        t.setDaemon(true);
        t.start();
    }

    public Long getGlobalSmilesCounter(){
        return databaseConnector.getSmilesCount();
    }

    public Long getGlobalSmilesFromTodayCounter(){
        return databaseConnector.getSmilesCount(changeDateWithDays(0), changeDateWithDays(1));
    }

    public Long getGlobalSmilesFromLastWeekCounter(){
        return databaseConnector.getSmilesCount(changeDateWithDays(-7), changeDateWithDays(1));
    }

    public Long getGlobalSmilesFromLastMonthCounter(){
        return databaseConnector.getSmilesCount(changeDateWithDays(-30), changeDateWithDays(1));
    }

    public Long getCurrentLocationSmilesCounter(){
        String location = userSettings.getLocationName();
        return databaseConnector.getSmilesCountFromLocalisation(location);
    }

    public Long getCurrentLocationSmilesFromTodayCounter(){
        String location = userSettings.getLocationName();
        return databaseConnector.getSmilesCountFromLocalisation(location, changeDateWithDays(0), changeDateWithDays(1));
    }

    public Long getCurrentLocationSmilesFromLastWeekCounter(){
        String location = userSettings.getLocationName();
        return databaseConnector.getSmilesCountFromLocalisation(location, changeDateWithDays(-7), changeDateWithDays(1));
    }

    public Long getCurrentLocationSmilesFromLastMonthCounter(){
        String location = userSettings.getLocationName();
        return databaseConnector.getSmilesCountFromLocalisation(location, changeDateWithDays(-30), changeDateWithDays(1));
    }

    public void saveImageToDatabase(List<Face> detectedFaces, BufferedImage image) {
        Snapshot snapshot = new Snapshot();
        snapshot.setDate(new Date());
        snapshot.setDetectedSmiles(detectedFaces);
        snapshot.setLocalisation(userSettings.getLocationName());
        snapshot.setAuthor("Smilecounter App");
        if(appConfig.isDatabaseStoreFaces()){
            try {
                String base64 = ImagesHelper.convertBufferedImageToBase64(image);
                snapshot.setContent(String.format(BASE_64_FORMAT, base64));
            } catch (IOException e) {
                LOGGER.error("Error during converting image to base64: ", e);
            }
        }

        LOGGER.debug("Saving {} smiles to database...", detectedFaces.size());
        databaseConnector.saveSnapshot(snapshot);
    }

    private Date changeDateWithDays(int n){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, n);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }
}
