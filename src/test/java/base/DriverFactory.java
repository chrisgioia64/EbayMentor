package base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.HashMap;
import java.util.Map;

public class DriverFactory {

    private static final DriverFactory INSTANCE = new DriverFactory();

    /** The folder that contains the webdrivers such as chromedriver.exe. */
    private static final String DRIVER_DIR = "drivers";

    private static final Logger LOGGER = LogManager.getLogger(DriverFactory.class);

    private final static Map<BrowserType, WebDriver> singleThreadMap = new HashMap<>();

    private DriverFactory() {
        initializaLocalProperties();
    }

    public static DriverFactory getInstance() {
        return INSTANCE;
    }

    private void initializaLocalProperties() {
        System.setProperty("webdriver.edge.driver", getDriverLocation("msedgedriver.exe"));
        System.setProperty("webdriver.chrome.driver",getDriverLocation("chromedriver_99.exe"));
        System.setProperty("webdriver.gecko.driver", getDriverLocation("geckodriver.exe"));
        System.setProperty("webdriver.ie.driver", getDriverLocation("IEDriverServer.exe"));
    }

    private static String getDriverLocation(String baseName) {
        return DRIVER_DIR + "/" + baseName;
    }

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
        }
        return switch (type) {
            case FIREFOX -> new FirefoxDriver();
            case CHROME -> new ChromeDriver();
            case EDGE -> new EdgeDriver();
            default -> {
                throw new IllegalArgumentException("browser type specified is unrecognized: " + type);
            }
        };
    }

}
