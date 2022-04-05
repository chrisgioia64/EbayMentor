package pages;

import base.CustomUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ViewItemPage extends EbayPage {

    private final static Logger LOGGER = LogManager.getLogger(ViewItemPage.class);

    // Selectors
    public static final String SELECTOR_ADD_TO_WATCHLIST_LINK = "#linkTopAct";

    public static final String SELECTOR_PRODUCT_TITLE = "div[data-testid='x-item-title'] span";
    public static final String SELECTOR_BRAND = "div[data-testid='d-item-condition'] .d-item-condition-value span.clipped";
    public static final String SELECTOR_QUANTITY_INPUT = "#qtyTextBox";
    public static final String SELECTOR_NUM_AVAILABLE = "#qtySubTxt";
    public static final String SELECTOR_QUANTITY_ERROR_BOX = "#qtyErrMsg div";
    public static final String SELECTOR_PRICE_TEXT = "#prcIsum";

    public static final String SELECTOR_BUY_IT_NOW = "#binBtn_btn";
    public static final String SELECTOR_VIEW_IN_CART = "#vi-viewInCartBtn";
    public static final String SELECTOR_ADD_CART = "#atcRedesignId_btn";
    //    public static final String SELECTOR_WATCHING = "#vi-atl-lnk-99 .vi-atw-txt";
    public static final String SELECTOR_WATCHING = "#vi-atl-lnk-99";

    public static final String SELECTOR_NUM_WATCHERS = "#why2buy .w2b-cnt:nth-child(3)";

    // Seller Information
    public static final String SELECTOR_SELLER_LINK = ".ux-seller-section__item:nth-child(1) a";
    public static final String SELECTOR_SAVE_SELLER_LINK
            = "div[data-testid='x-about-this-seller'] .follow-ebay-wrapper button .follow-ebay_text";
    public static final String SELECTOR_CONTACT_SELLER_LINK
            = "div[data-testid='x-about-this-seller'] div div:nth-child(3) div:nth-child(2) a";

    public static final String SELECTOR_PRODUCT_RATINGS_TEXT = "#_rvwlnk";

    public static final String SELECTOR_RATING_PANEL_PRODUCT_RATINGS_TEXT = "#rwid .ebay-reviews-count";

    private static final By SELECTOR_IMAGE_NEXT_BUTTON = By.cssSelector("button.next-arr");
    private static final By SELECTOR_IMAGE_PREV_BUTTON = By.cssSelector("button.prev-arr");

    private static final By SELECTOR_IMAGE_THUMBNAILS_ALIGN = By.cssSelector("#vertical-align-items-wrapper li");

    private final static By SELECTOR_INSIDE_POPUP_ADD_TO_CART_LINK
            = By.cssSelector(".app-atc-layer-redesign-content-wrapper .btn-scnd:nth-child(2)");

    public ViewItemPage(WebDriver driver) {
        super(driver);
    }

    public List<WebElement> getImageThumbnails() {
        List<WebElement> elements = driver.findElements(SELECTOR_IMAGE_THUMBNAILS_ALIGN);
        return elements;
    }

    public Optional<WebElement> getImagePreviousButton() {
        return driver.findElements(SELECTOR_IMAGE_PREV_BUTTON).stream().findFirst();
    }

    public Optional<WebElement> getImageNextButton() {
        return driver.findElements(SELECTOR_IMAGE_NEXT_BUTTON).stream().findFirst();
    }

    public String getProductTitle() {
        return getText(SELECTOR_PRODUCT_TITLE);
    }

    public String getBrandText() {
        return getText(SELECTOR_BRAND);
    }

    public String getQuantityString() {
        WebElement element = getQuantityTextbox();
        return element.getAttribute("value");
    }

    public WebElement getQuantityTextbox() {
        return driver.findElement(By.cssSelector(SELECTOR_QUANTITY_INPUT));
    }

    public WebElement getQuantityErrorBox() {
        return driver.findElement(By.cssSelector(SELECTOR_QUANTITY_ERROR_BOX));
    }

    private String getNumberAvailableText() {
        return getText(SELECTOR_NUM_AVAILABLE);
    }

    public int getNumberAvailable() {
        String text = getNumberAvailableText();
        if (text.equalsIgnoreCase("Last one")) {
            return 1;
        }
        String[] ary = text.split("\\s+");
        try {
            return Integer.parseInt(ary[0]);
        } catch (NumberFormatException e) {
            LOGGER.warn("Could not parse the number of available from text {}", text);
            return -1;
        }
    }

    private String getPriceText() {
        return getText(SELECTOR_PRICE_TEXT);
    }

    public double getPriceAsDouble() {
        String text = getPriceText();
        String[] ary = text.split("\\s+");
        if (ary.length != 2) {
            LOGGER.warn("Could not parse price text {}", text);
        }
        String dollar = ary[1];
        StringBuilder b = new StringBuilder();
        for (char c : dollar.toCharArray()) {
            if (CustomUtilities.isDigit(c) || c == '.') {
                b.append(c);
            }
        }
        try {
            return Double.parseDouble(b.toString());
        } catch (NumberFormatException e) {
            LOGGER.warn("Could not parse the currency amount {}", dollar);
            return 0.00;
        }
    }

    public String getBuyItNowText() {
        return getText(SELECTOR_BUY_IT_NOW);
    }

    public String getWatchingText() {
        return getText(SELECTOR_WATCHING);
    }

    public WebElement getWatchButton() {
        return getElement(SELECTOR_WATCHING);
    }

    public void clickWatchButton() {
        click(SELECTOR_WATCHING);
    }

    private String getNumWatchersString() {
        return getText(SELECTOR_NUM_WATCHERS);
    }

    public int getNumWatchers() {
        String text = getNumWatchersString();
        String[] ary = text.split("\\s+");
        if (ary.length != 2) {
            LOGGER.warn("Could not parse num watchers string {}", text);
        }
        try {
            return Integer.parseInt(ary[0]);
        } catch (NumberFormatException e) {
            LOGGER.warn("Could not parse the number {}", ary[0]);
            return 0;
        }
    }

    public void clickAddToCartInsidePopup() {
        click(SELECTOR_INSIDE_POPUP_ADD_TO_CART_LINK);
    }

    public boolean isWatchlistLinkVisible() {
        WebElement element = getWatchlistLink();
        return element.isDisplayed() && element.isEnabled();
    }

    public WebElement getWatchlistLink() {
        return driver.findElement(By.cssSelector(SELECTOR_ADD_TO_WATCHLIST_LINK));
    }

    public String getSaveSellerLinkText() {
        return getText(SELECTOR_SAVE_SELLER_LINK);
    }

    public void clickSaveSellerLink() {
        click(SELECTOR_SAVE_SELLER_LINK);
    }

    public String getContactSellerLinkText() {
        return getText(SELECTOR_CONTACT_SELLER_LINK);
    }

    /**
     * Return the product ratings text at the top of the page, if it exists
     */
    public String getProductRatingsText() {
       return getText(SELECTOR_PRODUCT_RATINGS_TEXT, "");
    }

    /**
     * Return the product ratings text in the "Ratings and Review" panel at the
     * bottom of the page
     */
    public String getRatingsPanelProductRatingText() {
        return getText(SELECTOR_RATING_PANEL_PRODUCT_RATINGS_TEXT, "");
    }

    public WebElement getCartButton() {
        return getFirstAvailableElement(Arrays.asList(
            SELECTOR_VIEW_IN_CART, SELECTOR_ADD_CART
        ));
    }

    public void navigateItemNumber(String itemNumber) {
        navigateToSuburl("itm/" + itemNumber);
    }

    public void clickSellerLink() {
        click(SELECTOR_SELLER_LINK);
    }

    public String getSellerLinkText() {
        WebElement element = driver.findElement(By.cssSelector(SELECTOR_SELLER_LINK));
        return element.getText();
    }

    @Override
    public void navigateToPage() {
        throw new IllegalArgumentException("there is no specific view item page");
    }
}
