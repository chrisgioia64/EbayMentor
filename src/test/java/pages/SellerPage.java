package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SellerPage extends EbayPage {

    /** The link for saving/unsaving the seller from the list of saved sellers. */
    public static final By SELECTOR_SAVE_LINK = By.cssSelector("#w1-2 a");

    public SellerPage(WebDriver driver) {
        super(driver);
    }

    public void navigateSeller(String sellerName) {
        navigateToSuburl("usr/" + sellerName);
    }

    public WebElement getSaveElement() {
        return driver.findElement(SELECTOR_SAVE_LINK);
    }

    @Override
    public void navigateToPage() {
        throw new IllegalArgumentException("there is no specific save seller page");
    }
}
