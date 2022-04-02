package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.Optional;

public class CartPage extends EbayPage {

    private final static String SUBURL = "";

    /** Selector for the container for a single product item. */
    private final static By SELECTOR_ITEM = By.cssSelector(".cart-bucket");

    /** The relative selector (from SELECTOR_ITEM) for the product title link. */
    private final static By SELECTOR_ITEM_LINK = By.cssSelector("a[data-test-id='cart-item-link']");

    /** The relative selector (from SELECTOR_ITEM) for the dropdown for the quantity. */
    private final static By SELECTOR_QUANTITY_DROPDOWN = By.cssSelector("select[data-test-id='qty-dropdown']");

    /** The relative selector (from SELECTOR_ITEM) for the remove button link. */
    private final static By SELECTOR_REMOVE_BUTTON = By.cssSelector("button[data-test-id='cart-remove-item']");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Returns the quantity selected for the product with item number specified
     * If no product exists, return 0
     */
    public String getQuantity(String itemNumber) {
        Optional<String> dropdown =
                driver.findElements(SELECTOR_ITEM).stream()
                        .filter(x -> x.findElement(SELECTOR_ITEM_LINK).getAttribute("href").contains(itemNumber))
                        .map(x -> {
                            if (elementExists(x, SELECTOR_QUANTITY_DROPDOWN)) {
                                return new Select(x.findElement(SELECTOR_QUANTITY_DROPDOWN))
                                        .getFirstSelectedOption().getText();
                            } else {
                                return "1";
                            }
                        })
                        .findFirst();
        return dropdown.orElse("0");
    }

    /**
     * Attempts to delete the item. Returns true if there was a product, false otherwise.
     */
    public boolean deleteItem(String itemNumber) {
       Optional<WebElement> element =
                driver.findElements(SELECTOR_ITEM).stream()
                        .filter(x -> x.findElement(SELECTOR_ITEM_LINK).getAttribute("href").contains(itemNumber))
                        .map(x -> x.findElement(SELECTOR_REMOVE_BUTTON))
                        .findFirst();
       if (element.isPresent()) {
           element.get().click();
           return true;
       } else {
           return false;
       }
    }

    @Override
    public void navigateToPage() {
        navigateToSuburl(SUBURL);
    }
}
