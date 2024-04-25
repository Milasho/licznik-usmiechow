package smilecounter.core.affective.enums;

import org.apache.commons.lang3.StringUtils;
import org.opencv.core.Core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum AffectiveServices {
    OPENIMAJ("OpenIMAJ", null, null),
    OPEN_CV("OpenCV", Collections.singletonList(Core.NATIVE_LIBRARY_NAME), Collections.singletonList("libopencv_java330")),
    LUXAND("Luxand", Collections.singletonList(getFaceSDKLibName()), Collections.singletonList(getFaceSDKLibName())),
    CUSTOM("Custom", Arrays.asList(Core.NATIVE_LIBRARY_NAME, getFaceSDKLibName()), Arrays.asList("libopencv_java330", getFaceSDKLibName()));

    private static String OS;

    private final String name;
    private final List<String> fileNames;
    private final List<String> libNames;

    AffectiveServices(String name, List<String> libNames, List<String> fileNames) {
        this.name = name;
        this.fileNames = fileNames;
        this.libNames = libNames;
    }

    public String getName() {
        return this.name;
    }

    public List<String> getLibNames() {
        return this.libNames;
    }

    public List<String> getFileNames(){
        return this.fileNames;
    }

    public static AffectiveServices getFromName(String name) {
        AffectiveServices result = null;
        switch (StringUtils.defaultString(name).toLowerCase()) {
            case "openimaj":
                result = AffectiveServices.OPENIMAJ;
                break;
            case "opencv":
                result = AffectiveServices.OPEN_CV;
                break;
            case "luxand":
                result = AffectiveServices.LUXAND;
                break;
            case "custom":
                result = AffectiveServices.CUSTOM;
                break;
        }

        return result;
    }

    public static List<AffectiveServices> getAllServices(){
        return Arrays.asList(AffectiveServices.OPENIMAJ, AffectiveServices.CUSTOM,
                AffectiveServices.LUXAND, AffectiveServices.OPEN_CV);
    }

    private static String getFaceSDKLibName(){
        if(OS == null){
            OS = System.getProperty("os.name");
        }
        return OS == null || OS.startsWith("Windows") ? "facesdk" : "libfsdk";
    }

}
