package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class WatchlistPage extends EbayPage {

    private final static String SUB_URL = "mye/myebay/watchlist";

    private final static By SELECTOR_DELETE_BUTTON =
            By.cssSelector(".items-action button[data-template='DELETE_ITEMS_TEMPLATE']");

    private final static By SELECTOR_ITEM = By.cssSelector(".m-item");
    private final static By SELECTOR_ITEM_TITLE_LINK = By.cssSelector(".item-title a");
    private final static By SELECTOR_CHECKBOX = By.cssSelector(".m-checkbox input");

    public WatchlistPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void navigateToPage() {
        navigateToSuburl(SUB_URL);
    }

    /**
     * Attempts to delete the item from the watchlist
     * Returns true if it exists and deletion successful
     * Returns false if the product does not exist
     */
    public boolean deleteProduct(String itemNumber) {
       Optional<WebElement> itemElement = driver.findElements(SELECTOR_ITEM)
               .stream()
               .filter(x -> x.findElement(SELECTOR_ITEM_TITLE_LINK)
                       .getAttribute("href").contains(itemNumber))
               .findFirst();

       if (itemElement.isPresent()) {
           itemElement.get().findElement(SELECTOR_CHECKBOX).click();
           driver.findElement(SELECTOR_DELETE_BUTTON).click();
           return true;
       } else {
           return false;
       }
    }


}
