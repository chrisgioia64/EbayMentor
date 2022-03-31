package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public abstract class EbayPage extends PageObject {

    private final static String SELECTOR_SEARCH_BOX = "#gh-ac";

    public EbayPage(WebDriver driver) {
        super(driver);
    }

    public WebElement getSearchBox() {
        WebElement element = driver.findElement(By.cssSelector(SELECTOR_SEARCH_BOX));
        return element;
    }
}
