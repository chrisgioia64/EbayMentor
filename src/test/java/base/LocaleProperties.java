package base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Corresponds to a locale property file
 */
public class LocaleProperties {

    public final static String KEY_URL = "url";
    public final static String KEY_TITLE = "title";

    private Properties prop;
    private final static Logger LOGGER = LogManager.getLogger(LocaleProperties.class);

    public LocaleProperties(String path) {
        this.prop = new Properties();
        try {
            prop.load(new FileInputStream(path));
        } catch (IOException e) {
            LOGGER.error("Could not load the locale file: " + path);
            e.printStackTrace();
        }
    }

    public String getProperty(String key) {
        if (prop.get(key) == null) {
            return null;
        }
        return prop.get(key).toString();
    }

}
