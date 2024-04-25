package smilecounter.core.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Properties;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;

public class ResourcesLoader {
    public static URL getResource(String resource, Class<?> clazz){
       return clazz.getResource(resource);
    }

    public static Properties getPropertiesFile(String resource, Class<?> clazz) throws FileNotFoundException, IOException {
        Properties properties = new Properties();
        InputStream stream = getInputStream(resource, clazz);
        if(stream != null){
            properties.load(stream);
        }
        return properties;
    }

    public static Properties getPropertiesFromExternalFile(String filePath) throws IOException{
        Properties properties = new Properties();
        FileInputStream stream = new FileInputStream(filePath);
        if(stream != null){
            properties.load(stream);
        }
        return properties;
    }

    public static InputStream getInputStream(String resource, Class<?> clazz){
        ClassLoader classLoader = clazz.getClassLoader();
        return classLoader.getResourceAsStream(resource);
    }

    public static String getResourceAbsolutePath(String resource, Class<?> clazz){
        return clazz.getResource(resource).getPath();
    }

    public static Image getImage(String resource, Class<?> clazz) throws IOException {
        URL res = clazz.getClassLoader().getResource(resource);
        return res != null ? ImageIO.read(res) : null;
    }

    public static String getExternalResourcePath(String resource, Class<?> clazz) throws Exception{
        InputStream stream = null;
        OutputStream resStreamOut = null;
        String result;
        try {
            stream = getInputStream(resource, clazz);
            if(stream == null) {
                throw new Exception("Cannot get resource \"" + resource + "\" from Jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];
            String tmpDir = System.getProperty("java.io.tmpdir").replace('\\', '/');
            result = tmpDir + "/" + getFileName(resource);
            resStreamOut = new FileOutputStream(result);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            stream.close();
            resStreamOut.close();
        }

        return result;
    }

    private static String getFileName(String path){
        return FilenameUtils.getName(path);
    }
}
