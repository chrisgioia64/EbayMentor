package pages;

import base.EnvironmentProperties;
import base.LocaleProperties;
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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    protected WebElement getElement(String cssSelector) {
        WebElement element = wait.until(ExpectedConditions.visibilityOf(
                driver.findElement(By.cssSelector(cssSelector))));
        return element;
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
//            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(cssSelector)));
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

    protected String getText(String cssSelector) {
        WebElement element = driver.findElement(By.cssSelector(cssSelector));
        return element.getText();
    }

    protected String getText(String cssSelector, String defaultText) {
        try {
            WebElement element = driver.findElement(By.cssSelector(cssSelector));
            return element.getText();
        } catch (NoSuchElementException ex) {
            return defaultText;
        }
    }

    /**
     * Returns the first web element that exist from the list of selectors
     * @param cssSelectors
     * @return
     */
    public WebElement getFirstAvailableElement(List<String> cssSelectors) {
        for (String cssSelector : cssSelectors) {
            if (elementExists(cssSelector)) {
                return driver.findElement(By.cssSelector(cssSelector));
            }
        }
        LOGGER.warn("Could not find an available element out of CSS selectors {}", cssSelectors);
        return null;
    }

    /**
     * Use case is for a list of products that have their own container, where each
     * container has more specific information like links, controls, etc.
     * When we want to obtain a stream of the inner elements (e.g. link elements of all products)
     * @param outerSelector
     * @param innerSelector
     */
    protected Stream<WebElement> getInnerElements(By outerSelector, By innerSelector) {
        return driver.findElements(outerSelector).stream().
                map(x -> x.findElement(innerSelector));
    }

    /** Navigates to the url of the given page.
     *  Throws an exception when additional information (e.g. product ID) is needed
     */
    public abstract void navigateToPage();

    private String getFullUrl(String subUrl) {
        String URL = EnvironmentProperties.getInstance().getLocaleProperties().getProperty(
                LocaleProperties.KEY_URL
        );
        return URL + "/" + subUrl;
    }

    /**
     * Navigate the webdriver to the suburl given
     * @param subUrl the portion of the url that comes after the domain name
     */
    protected void navigateToSuburl(String subUrl) {
        String fullUrl = getFullUrl(subUrl);
        driver.get(fullUrl);
    }

}

