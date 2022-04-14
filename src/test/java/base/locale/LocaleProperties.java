package base.locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Corresponds to a locale property file
 */
public class LocaleProperties {

    /** The locale as a two-letter abbreviation. */
    public final static String KEY_LOCALE = "locale";

    /** The base url of the locale (e.g. "www.ebay.com" for us). */
    public final static String KEY_URL = "url";

    /** The page title of the home page. */
    public final static String KEY_TITLE = "title";

    /** The language of the locale. */
    public final static String KEY_LANGUAGE = "language";

    public final static String KEY_TEXT_WATCHER = "watcher_text";

    public final static String KEY_COUNTRY_PREFIX = "country_";

    /** The display text of the country dropdown value where no country is selected. */
    public final static String KEY_TEXT_SELECT = "select_text";

    /** The display text of "Worldwide" when the shipping is to all countries.
     *  Varies based on language. */
    public final static String KEY_TEXT_WORLDWIDE = "worldwide_text";

    public final static String KEY_TAB_DESCRIPTION = "tab_description";
    public final static String KEY_TAB_SHIPPING = "tab_shipping";

    public final static String KEY_TO_COLUMN = "to_column_name";

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

    public String getCountry(String countryDisplayName) {
        String key = KEY_COUNTRY_PREFIX + countryDisplayName.replace(" ", "_");
        return getProperty(key);
    }

    public String getProperty(String key) {
        if (prop.get(key) == null) {
            return null;
        }
        return prop.get(key).toString();
    }

}
