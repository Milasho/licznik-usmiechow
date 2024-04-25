package smilecounter.core.data.connectors.simple;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SimpleDatabaseOperations {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final static String COLUMN_SEPARATOR = "\t";
    private final static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    private Map<String, Long> smilesPerDay;

    public SimpleDatabaseOperations(String databaseFile) {
        try {
            smilesPerDay = loadSmilesFromFile(databaseFile);
        } catch (FileNotFoundException e) {
            LOGGER.debug("Creating new database file ({})", e.getMessage());
            smilesPerDay = new HashMap<>();
        }
    }

    private Map<String, Long> loadSmilesFromFile(String databaseFile) throws FileNotFoundException {
        Map<String, Long> result = new HashMap<>();
        File file = new File(databaseFile);
        file.getParentFile().mkdirs();
        BufferedReader br = new BufferedReader(new FileReader(file));

        String line;
        LOGGER.info("Loading smiles database from file: {}", databaseFile);
        try {
            while((line = br.readLine()) != null){
                if(StringUtils.isNotEmpty(line)){
                    String[] columns = line.split(COLUMN_SEPARATOR);
                    String date = columns[0];
                    Long smiles = columns.length > 1 ? Long.valueOf(columns[1]) : 0;
                    result.put(date, smiles);
                }
            }
            br.close();
        } catch (Exception e) {
            LOGGER.error("Error during reading database file {}: ", databaseFile, e);
        }

        return result;
    }

    public void saveNewSmiles(int smilesCount) {
        String today = getTimestampOfDate(new Date());
        Long actualSmiles = smilesPerDay.getOrDefault(today, (long) 0);
        smilesPerDay.put(today, actualSmiles + smilesCount);
    }

    private String getTimestampOfDate(Date date){
        return formatter.format(date);
    }

    private Date convertToDate(String ds){
        Date result = null;
        try {
            result = this.formatter.parse(ds);
        } catch (ParseException e) {
           LOGGER.error("Error parsing date {}: ", ds, e);
        }
        return result;
    }

    private Date removeTime(Date date){
       String timestamp = getTimestampOfDate(date);
       return convertToDate(timestamp);
    }

    public long getAllSmiles() {
        long sum = 0;
        for (Long smiles : smilesPerDay.values()) {
            sum += smiles;
        }
        return sum;
    }

    public long getSmiles(Date from, Date to) {
        long sum = 0;
        from = removeTime(from);
        to = removeTime(to);

        for (Map.Entry<String, Long> entry : smilesPerDay.entrySet()) {
            Date date = convertToDate(entry.getKey());
            Long smiles = entry.getValue();

            if(from.getTime() <= date.getTime() && date.getTime() <= to.getTime()){
                sum += smiles;
            }
        }

        return sum;
    }

    public void saveAllSmiles(String databaseFile) {
        LOGGER.info("Save smiles database to file: {}", databaseFile);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(databaseFile));
            for (Map.Entry<String, Long> entry : smilesPerDay.entrySet()) {
                String date = entry.getKey();
                Long smiles = entry.getValue();

                String line = date + COLUMN_SEPARATOR + smiles;
                bw.write(line);
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) {
            LOGGER.error("Error during saving smiles database to file: ", e);
        }
    }
}
