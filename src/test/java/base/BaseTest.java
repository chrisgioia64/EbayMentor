package base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import java.util.ArrayList;
import java.util.List;

public class BaseTest {

    private final static List<WebDriver> driverPool = new ArrayList<>();
    private final static Logger LOGGER = LogManager.getLogger(BaseTest.class);

    private static String URL = "";

    @BeforeSuite
    public void setup() {
        URL = EnvironmentProperties.getInstance().getLocaleProperties().getProperty(
                LocaleProperties.KEY_URL
        );
    }

    public WebDriver getWebDriver(boolean loadUrl) {
        BrowserType type = EnvironmentProperties.getInstance().getBrowser();
        WebDriver driver = DriverFactory.getInstance().getWebdriver(type);
        if (loadUrl) {
            driver.get(URL);
        }
        driverPool.add(driver);
        return driver;
    }

    public WebDriver getWebDriver() {
        return getWebDriver(true);
    }

    @AfterSuite
    public void teardown() {
//        driverPool.forEach(WebDriver::quit);
    }

}
