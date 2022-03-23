package tests;

import base.BaseTest;
import base.EnvironmentProperties;
import base.LocaleProperties;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class SmokeTest extends BaseTest {

    @Test
    public void testTitle() {
        WebDriver driver = getWebDriver();
        String expectedTitle = EnvironmentProperties.getInstance().getLocaleProperties()
                .getProperty(LocaleProperties.KEY_TITLE);
        assertEquals(driver.getTitle(), expectedTitle);
    }

}
