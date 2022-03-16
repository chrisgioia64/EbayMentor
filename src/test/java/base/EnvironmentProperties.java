package base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class EnvironmentProperties {

    private static final String RESOURCES_FOLDER = "src//test//resources//";
    private final static String KEY_URL = "url";
    private Properties prop;
    private final static Logger LOGGER = LogManager.getLogger(EnvironmentProperties.class);
    private final static EnvironmentProperties INSTANCE = new EnvironmentProperties();

    public static EnvironmentProperties getInstance() {
        return INSTANCE;
    }

    private EnvironmentProperties() {
        initializeProperties();
    }

    private void initializeProperties() {
        this.prop = new Properties();
        String envFile = getEnvironmentFile();
        try {
            prop.load(new FileInputStream(envFile));
        } catch (IOException e) {
            LOGGER.error("Could not load environment file: " + envFile);
            e.printStackTrace();
        }
    }

    private final static String ENVIRONMENT_FILE_SYSTEM_PROPERTY = "env_file";

    private final static String DEV_ENV_FILE = "dev.properties";

    private static String getEnvironmentFile() {
        String envFile = System.getProperty(ENVIRONMENT_FILE_SYSTEM_PROPERTY);
        if (envFile == null) {
            LOGGER.warn("No env_file system property specified. Using " + DEV_ENV_FILE);
            return RESOURCES_FOLDER + DEV_ENV_FILE;
        }
        LOGGER.info("Using the following environment file {} ", envFile);
        return RESOURCES_FOLDER + envFile;
    }

    public String getProperty(String key) {
        if (prop.get(key) == null) {
            return null;
        }
        return prop.get(key).toString();
    }

    public static final String DEFAULT_URL = "https://www.ebay.com/";

    public String getUrl() {
        String urlValue = getProperty(KEY_URL);
        if (urlValue == null) {
            LOGGER.warn("No url value specified in environment value. Using default US website" +
                    " " + DEFAULT_URL);
            return DEFAULT_URL;
        } else {
            LOGGER.info("Using the following url: " + urlValue);
            return urlValue;
        }
    }

}
