package base;

import base.locale.LocaleProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.util.ArrayList;
import java.util.List;

public class BaseTest {

    private final static List<WebDriver> driverPool = new ArrayList<>();
    private final static Logger LOGGER = LogManager.getLogger(BaseTest.class);

    private static String URL = "";

    @BeforeSuite(alwaysRun = true)
    public void setup() {
        URL = EnvironmentProperties.getInstance().getLocaleProperties().getProperty(
                LocaleProperties.KEY_URL
        );
    }

    public WebDriver getWebDriver(boolean loadUrl, boolean newDriver) {
        BrowserType type = EnvironmentProperties.getInstance().getBrowser();
        WebDriver driver = DriverFactory.getInstance().getWebdriver(type, newDriver);
        if (loadUrl) {
            driver.get(URL);
        }
        driverPool.add(driver);
        return driver;
    }

    public WebDriver getWebDriver() {
        return getWebDriver(true, false);
    }

    public void quitDriver(WebDriver driver) {
        driver.quit();
    }

    @AfterSuite(alwaysRun = true)
    public void teardown() {
//        driverPool.forEach(WebDriver::quit);
    }

}
