package base;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.util.ArrayList;
import java.util.List;

public class BaseTest {

    private final static List<WebDriver> driverPool = new ArrayList<>();

    private static String URL = "";

    @BeforeSuite
    public void setup() {
        URL = EnvironmentProperties.getInstance().getUrl();
    }

    public static WebDriver getWebDriver() {
        BrowserType type = BrowserType.CHROME;
        WebDriver driver = DriverFactory.getInstance().getWebdriver(type);
        driver.get(URL);
        driverPool.add(driver);
        return driver;
    }

    @AfterSuite
    public void teardown() {
//        driverPool.forEach(WebDriver::quit);
    }

}
