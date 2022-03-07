package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * This is the base page object classes from which all page objects extend
 */
public abstract class PageObject {

    protected final WebDriver driver;

    private static final Logger LOGGER = LogManager.getLogger(PageObject.class);

    private WebDriverWait wait;

    public PageObject(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    protected void selectByValue(WebElement element, String value, String elementName) {
        Select select = new Select(element);
        try {
            select.selectByValue(value);
        } catch (NoSuchElementException ex) {
            LOGGER.info("In {}, the select element '{}' does not have option with value '{}'",
                    this.getClass().getName(), elementName, value);
        }
    }

    /**
     * Send input to the element with the given CSS selector
     */
    protected void sendKeys(String cssSelector, String input) {
        // explicit wait
        WebElement element = wait.until(ExpectedConditions.visibilityOf(
                driver.findElement(By.cssSelector(cssSelector))));
        element.clear();
        element.sendKeys(input);
    }

    /**
     * Click on the element with the given CSS Selector
     */
    protected void click(String cssSelector) {
        // explicit wait
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(
                driver.findElement(By.cssSelector(cssSelector))));
        element.click();
    }

    protected boolean elementExists(String cssSelector) {
        try {
            driver.findElement(By.cssSelector(cssSelector));
            return true;
        } catch (NoSuchElementException ex) {
            return false;
        }
    }

    protected boolean linkExists(String linkText) {
        try {
            driver.findElement(By.linkText(linkText));
            return true;
        } catch (NoSuchElementException ex) {
            return false;
        }
    }

}

