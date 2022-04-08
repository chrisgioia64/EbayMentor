package pages;

import base.CustomUtilities;
import base.EnvironmentProperties;
import base.LocaleProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import tests.view_item.ViewItemTest;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
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

    public long getNumberDisplayedElements(List<WebElement> elements) {
        return elements.stream().filter(x -> x.isDisplayed()).count();
    }

    public long getNumberClickableElements(List<WebElement> elements) {
        int count = 0;
        for (WebElement element : elements) {
            try {
                wait.until(ExpectedConditions.elementToBeClickable(element));
                count++;
//                LOGGER.info("element is clickable ");
            } catch (TimeoutException ex) {
//                LOGGER.info("element is not clickable");
            }
        }
        return count;
    }


    public void scrollDownAndWait(int pixels, int millisecondsWait) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0," + pixels + ")", "");
        CustomUtilities.sleep(millisecondsWait);
    }

    public WebElement findElement(WebElement parentElement, By selector) {
        return wait.until(ExpectedConditions.visibilityOf(parentElement.findElement(selector)));
    }


    public WebElement getElement(By selector) {
        return wait.until(ExpectedConditions.visibilityOf(driver.findElement(selector)));
    }

    public Optional<WebElement> getElementOptional(By selector) {
        return driver.findElements(selector).stream().findFirst();
    }

    public boolean isDisplayed(By selector) {
        try {
            WebElement element = driver.findElement(selector);
            boolean b = element.isDisplayed();
            return b;
        } catch (NoSuchElementException ex) {
            return false;
        }
    }

    /**
     * Click on the element with the given CSS Selector
     */
    protected void click(String cssSelector) {
        click(By.cssSelector(cssSelector));
    }

    protected void click(By selector) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(
                driver.findElement(selector)));
        element.click();
    }

    protected boolean elementExists(WebElement element, By cssSelector) {
        try {
            element.findElement(cssSelector);
            return true;
        } catch (NoSuchElementException ex) {
            return false;
        }
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

    public boolean focusable(By selector) {
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOf(
                    driver.findElement(selector)));
            Actions actions = new Actions(driver);
            if (element.equals(driver.switchTo().activeElement())) {
                return true;
            } else {
                return false;
            }
        } catch (NoSuchElementException ex) {
            return false;
        }
    }

    public boolean focusable(WebElement element) {
        Actions actions = new Actions(driver);
        try {
            WebElement focusedElement = driver.switchTo().activeElement();
            if (element.equals(focusedElement)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            LOGGER.info("There was an error when trying to switch to element: " + element);
            return false;
        }
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

