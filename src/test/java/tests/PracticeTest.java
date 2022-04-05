package tests;

import base.CustomUtilities;
import base.BaseTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.Test;
import pages.EbayHomePage;

import java.util.Set;

/**
 * These are tests that display certain features of Selenium
 */
public class PracticeTest extends BaseTest {

    private final static Logger LOGGER = LogManager.getLogger(PracticeTest.class);

    @Test
    public void testCutAndPaste() {
        WebDriver driver = getWebDriver();
        EbayHomePage homePage = new EbayHomePage(driver);
        Actions actions = new Actions(driver);
        WebElement searchBox = homePage.getSearchBox();
        searchBox.sendKeys("abcd");

        actions.keyDown(Keys.CONTROL);
        actions.sendKeys("a");
        actions.keyUp(Keys.CONTROL);
        actions.build().perform();

        CustomUtilities.sleep(3000);

        actions.keyDown(Keys.CONTROL);
        actions.sendKeys("x");
        actions.keyUp(Keys.CONTROL);
        actions.build().perform();

        CustomUtilities.sleep(2000);

        actions.keyDown(Keys.CONTROL);
        actions.sendKeys("v");
        actions.keyUp(Keys.CONTROL);
        actions.build().perform();


        actions.keyDown(Keys.CONTROL);
        actions.sendKeys("v");
        actions.keyUp(Keys.CONTROL);
        actions.build().perform();

        CustomUtilities.sleep(3000);
    }

    /**
     * Hovers over "Electronics" link at top of page, and you can
     * see the link get underlined
     */
    @Test
    public void testHoverOnLink() {
        WebDriver driver = getWebDriver();
        EbayHomePage homePage = new EbayHomePage(driver);
        Actions actions = new Actions(driver);
        WebElement electronicsLink = homePage.getElectronicsLink();
        actions.moveToElement(electronicsLink).build().perform();
        CustomUtilities.sleep(3000);
        // The click action doesn't seem to work with Actions object
//        actions.click(electronicsLink).build().perform();
//        electronicsLink.click();
//        CustomUtilities.sleep(3000);
    }

    @Test
    public void testSwitchBetweenWindows() {
        WebDriver driver = getWebDriver();
        EbayHomePage homePage = new EbayHomePage(driver);
        String firstTab = driver.getWindowHandle();

        driver.switchTo().newWindow(WindowType.TAB);
        String secondTab = driver.getWindowHandle();
        Set<String> windowNames = driver.getWindowHandles();
        CustomUtilities.sleep(1000);
        driver.get("https://www.google.com");
        CustomUtilities.sleep(1000);

        driver.switchTo().window(firstTab);
        CustomUtilities.sleep(2000);
        driver.switchTo().window(secondTab);
        CustomUtilities.sleep(2000);
    }


}
