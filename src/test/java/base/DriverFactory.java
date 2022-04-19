package base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.AbstractDriverOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class DriverFactory {

    private static final DriverFactory INSTANCE = new DriverFactory();

    /**
     * The folder that contains the webdrivers such as chromedriver.exe.
     */
    private static final String DRIVER_DIR = "drivers";

    private static final Logger LOGGER = LogManager.getLogger(DriverFactory.class);

    private final static Map<BrowserType, WebDriver> singleThreadMap = new HashMap<>();

    private final static ThreadLocal<WebDriver> multiThreadMap = new ThreadLocal<>();

    private boolean isRemote;
    private Map<BrowserType, MutableCapabilities> capabilityMap;
    private String sauceUrl;

    private DriverFactory() {
        initializaLocalProperties();
        initializeSauceProperties();
        this.isRemote = EnvironmentProperties.getInstance().isUseRemote();
    }

    public static DriverFactory getInstance() {
        return INSTANCE;
    }

    private void initializaLocalProperties() {
        System.setProperty("webdriver.edge.driver", getDriverLocation("msedgedriver.exe"));
        System.setProperty("webdriver.chrome.driver", getDriverLocation("chromedriver_99.exe"));
        System.setProperty("webdriver.gecko.driver", getDriverLocation("geckodriver.exe"));
        System.setProperty("webdriver.ie.driver", getDriverLocation("IEDriverServer.exe"));
    }


    private void initializeSauceProperties() {
        this.capabilityMap = new HashMap<>();
        AbstractDriverOptions chromeCapabilities = createCapabilities(BrowserType.CHROME);
        capabilityMap.put(BrowserType.CHROME, chromeCapabilities);

        AbstractDriverOptions firefoxCapabilities = createCapabilities(BrowserType.FIREFOX);
        capabilityMap.put(BrowserType.FIREFOX, firefoxCapabilities);

        AbstractDriverOptions edgeCapabilities = createCapabilities(BrowserType.EDGE);
        capabilityMap.put(BrowserType.EDGE, edgeCapabilities);
        String sauceUrl = String.format(" https://ondemand.us-west-1.saucelabs.com/wd/hub");
        this.sauceUrl = sauceUrl;
    }

    private AbstractDriverOptions createOptions(BrowserType type) {
        if (type.equals(BrowserType.CHROME)) {
            return new ChromeOptions();
        } else if (type.equals(BrowserType.FIREFOX)) {
            return new FirefoxOptions();
        } else if (type.equals(BrowserType.EDGE)) {
            return new EdgeOptions();
        } else {
            throw new IllegalArgumentException("browser type specified is unrecognized: " + type);
        }
    }

    private AbstractDriverOptions createCapabilities(BrowserType type) {
        AbstractDriverOptions sauceOptions = createOptions(type);
        String sauceUser = EnvironmentProperties.getInstance().getProperty(EnvironmentProperties.SAUCE_USERNAME);
        String sauceKey = EnvironmentProperties.getInstance().getProperty(EnvironmentProperties.SAUCE_ACCESS_KEY);
        sauceOptions.setCapability("username", sauceUser);
        sauceOptions.setCapability("accessKey", sauceKey);
        AbstractDriverOptions capabilities = createOptions(type);
        capabilities.setCapability("platformName", "Windows 10");
        capabilities.setCapability("sauce:options", sauceOptions);
        capabilities.setBrowserVersion("latest");
        return capabilities;
    }

    private WebDriver createWebdriverHelper(BrowserType type) {
        if (this.isRemote) {
            try {
                return createRemoteWebdriver(type);
            } catch (MalformedURLException e) {
                LOGGER.warn("There was a problem with the url of the remote driver " + e.getMessage());
                return null;
            }
        } else {
            return createLocalWebdriver(type);
        }
    }

    private WebDriver createLocalWebdriver(BrowserType type) {
        if (type.equals(BrowserType.CHROME)) {
            return new ChromeDriver();
        } else if (type.equals(BrowserType.FIREFOX)) {
            return new FirefoxDriver();
        } else if (type.equals(BrowserType.EDGE)) {
            return new EdgeDriver();
        } else {
            throw new IllegalArgumentException("browser type specified is unrecognized: " + type);
        }
    }

    private WebDriver createRemoteWebdriver(BrowserType type) throws MalformedURLException {
        MutableCapabilities capabilities = this.capabilityMap.get(type);
        return new RemoteWebDriver(new URL(sauceUrl), capabilities);
    }

    private static String getDriverLocation(String baseName) {
        return DRIVER_DIR + "/" + baseName;
    }

    /**
     * @param type
     * @return
     */
    public WebDriver getWebdriver(BrowserType type) {
        WebDriver driver = singleThreadMap.get(type);
        if (driver == null) {
            driver = createWebdriver(type);
            singleThreadMap.put(type, driver);
        }
        return driver;

    }

    private WebDriver createWebdriver(BrowserType type) {
        boolean useExistingBrowser = EnvironmentProperties.getInstance().isUseExistingBrowser();
        if (useExistingBrowser) {
            int port = EnvironmentProperties.getInstance().getBrowserPort();
            ChromeOptions options = new ChromeOptions();
            options.setExperimentalOption("debuggerAddress", "127.0.0.1:" + port);
            WebDriver driver = new ChromeDriver(options);
            return driver;
        } else {
            return createWebdriverHelper(type);
        }
    }

}
