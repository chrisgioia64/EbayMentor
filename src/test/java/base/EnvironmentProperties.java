package base;

import base.locale.LocaleProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

public class EnvironmentProperties {

    private static final String RESOURCES_FOLDER = "src//test//resources//";
    private static final String LOCALE_FOLDER = "locale//";

    private Properties prop;
    private final static Logger LOGGER = LogManager.getLogger(EnvironmentProperties.class);
    private final static EnvironmentProperties INSTANCE = new EnvironmentProperties();

    // Environment Property Keys
    private static final String KEY_BROWSER = "browser";
    private static final String KEY_LOCALE = "locale";
    private static final String KEY_USE_EXISTING_BROWSER = "use_existing_browser";
    private static final String KEY_BROWSER_PORT = "port";
    private static final String KEY_PARALLEL = "parallel";
    private static final String KEY_PRODUCT_NUMBERS = "product_numbers";

    private static final String KEY_USE_REMOTE = "use_remote";
    public static final String SAUCE_USERNAME = "sauce_username";
    public static final String SAUCE_ACCESS_KEY = "sauce_accessKey";


    private LocaleProperties localeProperties;

    private boolean useRemote;

    public static EnvironmentProperties getInstance() {
        return INSTANCE;
    }

    private EnvironmentProperties() {
        initializeProperties();
        this.useRemote = isUseRemote();
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


    public BrowserType getBrowser() {
        String browserName = getConfigurationValue(KEY_BROWSER, BrowserType.CHROME.getName());
        for (BrowserType type : BrowserType.values()) {
            if (type.getName().equals(browserName)) {
                return type;
            }
        }
        LOGGER.info("Cannot find browser type with name {}", browserName);
        return null;
    }

    private String getLocaleFilePath() {
        String locale = getConfigurationValue(KEY_LOCALE, "us");
        String filename = "locale_" + locale + ".properties";
        String fullpath = RESOURCES_FOLDER + "//" + LOCALE_FOLDER + "//" + filename;
        return fullpath;
    }

    public LocaleProperties getLocaleProperties() {
        if (localeProperties == null) {
            localeProperties = new LocaleProperties(getLocaleFilePath());
        }
        return localeProperties;
    }

    public Optional<Set<Integer>> getProductNumber() {
        String numbers = getConfigurationValue(KEY_PRODUCT_NUMBERS, "");
        Set<Integer> productNumbers = new HashSet<>();
        if (numbers == null || numbers.equals("")) {
            LOGGER.info("Using all product numbers");
            return Optional.empty();
        }
        String[] numsAry = numbers.split(",");
        for (String num : numsAry) {
            try {
                Integer n = Integer.parseInt(num);
                productNumbers.add(n);
            } catch (NumberFormatException ex) {
                LOGGER.warn("Could not parse product number: {}. Using all product numbers", num);
                return Optional.empty();
            }
        }
        LOGGER.info("Using the following product numbers: {}", productNumbers);
        return Optional.of(productNumbers);
    }

    /**
     * Order of configuration
     * 1. Read from system property
     * 2. Read from property file
     * 3. Use default value
     */
    private String getConfigurationValue(String key, String defaultValue) {
        // Read from system property
        String propertyValue = System.getProperty(key);
        if (propertyValue != null) {
            LOGGER.info("For the system property {}, using the value {}",
                    key, propertyValue);
            return propertyValue;
        }

        // Read from the property file
        propertyValue = getProperty(key);
        if (propertyValue != null) {
            LOGGER.info("Reading from the environment file. " +
                            "For key {}, using the value {}",
                    key, propertyValue);
            return propertyValue;
        }

        // Using the default value
        LOGGER.warn("Using default value for key {} of {}", key, defaultValue);
        return defaultValue;
    }


    private boolean getBooleanFlag(String key) {
        String flag = getProperty(key);
        if (flag == null) {
            LOGGER.warn("No {} flag specified. Defaulting to false", key);
            return false;
        }
        if (flag.equalsIgnoreCase("true")) {
            LOGGER.info("{} flag set to true", key);
            return true;
        } else if (flag.equalsIgnoreCase("false")) {
            LOGGER.info("{} flag set to false", key);
            return false;
        } else {
            LOGGER.warn("{} flag must be true or false. Defaulting to false",
                    key);
            return false;
        }
    }

    public boolean isUseRemote() {
        String value = getConfigurationValue(KEY_USE_REMOTE, "false");
        if (value.equals("true")) {
            return true;
        } else if (value.equals("false")) {
            return false;
        } else {
            LOGGER.warn("Unrecognized boolean value for use_remote: " + value + " Using false.");
            return false;
        }
    }

    public boolean isUseExistingBrowser() {
        return getBooleanFlag(KEY_USE_EXISTING_BROWSER);
    }

    public boolean isParallel() {
        return getBooleanFlag(KEY_PARALLEL);
    }

    public int getBrowserPort() {
        String numProducts = getProperty(KEY_BROWSER_PORT);
        if (numProducts == null) {
            LOGGER.error("There is no port property specified");
            return 0;
        }
        try {
            int result = Integer.parseInt(numProducts);
            LOGGER.info("The port property is set to " + result);
            return result;
        } catch (NumberFormatException e) {
            LOGGER.error("The port property is not an integer");
        }
        return 0;
    }

}
