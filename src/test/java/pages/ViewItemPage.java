package pages;

import base.CustomUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ViewItemPage extends EbayPage {

    private final static Logger LOGGER = LogManager.getLogger(ViewItemPage.class);

    // Selectors
    public static final String SELECTOR_ADD_TO_WATCHLIST_LINK = "#linkTopAct";

    // Selectors in the top middle
    public static final String SELECTOR_PRODUCT_TITLE = "div[data-testid='x-item-title'] span";
    public static final String SELECTOR_BRAND = "div[data-testid='d-item-condition'] .d-item-condition-value span.clipped";
    public static final By SELECTOR_QUANTITY_INPUT = By.cssSelector("#qtyTextBox");
    public static final String SELECTOR_NUM_AVAILABLE = "#qtySubTxt";
    public static final By SELECTOR_QUANTITY_ERROR_BOX = By.cssSelector("#qtyErrMsg div");
    public static final String SELECTOR_PRICE_TEXT = "#prcIsum";
    public static final String SELECTOR_PRODUCT_RATINGS_TEXT = "#_rvwlnk";

    // Three main buttons
    public static final String SELECTOR_BUY_IT_NOW = "#binBtn_btn";
    public static final String SELECTOR_VIEW_IN_CART = "#vi-viewInCartBtn";
    public static final String SELECTOR_ADD_CART = "#atcRedesignId_btn";
    public static final String SELECTOR_WATCHING = "#vi-atl-lnk-99";

    public static final String SELECTOR_NUM_WATCHERS = "#why2buy .w2b-cnt:nth-child(3)";

    // Seller Information
    public static final String SELECTOR_SELLER_LINK = ".ux-seller-section__item:nth-child(1) a";
    public static final String SELECTOR_SAVE_SELLER_LINK
            = "div[data-testid='x-about-this-seller'] .follow-ebay-wrapper button .follow-ebay_text";
    public static final String SELECTOR_CONTACT_SELLER_LINK
            = "div[data-testid='x-about-this-seller'] div div:nth-child(3) div:nth-child(2) a";


    // Rating Panel at the bottom of the page
    private static final String SELECTOR_PRODUCT_RATING_DIV = "#review-ratings-cntr";
    private static final String SELECTOR_RATING_PANEL = "#rwid";
    private static final String SELECTOR_RATING_PANEL_PRODUCT_RATINGS_TEXT = "#rwid .ebay-reviews-count";
    /** The popup for the ratings. */
    private static final String SELECTOR_PRODUCT_RATING_POPUP = "#histogramid";
    private static final String SELECTOR_PRODUCT_RATING_NUMBER = ".ebay-review-start-rating";

    // Image Carousel Elements
    private static final By SELECTOR_IMAGE_BUTTON = By.cssSelector("#linkMainImg");
    private static final By SELECTOR_IMAGE_NEXT_BUTTON = By.cssSelector("button.next-arr");
    private static final By SELECTOR_IMAGE_PREV_BUTTON = By.cssSelector("button.prev-arr");
    private static final By SELECTOR_IMAGE_THUMBNAILS_ALIGN = By.cssSelector("#vertical-align-items-wrapper li");

    // Other Merchandise Carousels
    private static final By SELECTOR_MERCHANDISE_PANEL = By.cssSelector(".merch-module");
    private static final By SELECTOR_MERCHANDISE_PANEL_TITLE = By.cssSelector(".merch-title");
    private static final By SELECTOR_MERCHANDISE_BUTTON = By.cssSelector(".carousel__control");
    private static final By SELECTOR_MERCHANDISE_ITEMS = By.cssSelector(".carousel__viewport li");

    public static final String TITLE_SIMILAR_SPONSORED_ITEMS = "Similar sponsored items";
    public static final String TITLE_SPONSORED_ITEMS_RECENT = "Sponsored items based on your recent views";

    // Tabs -- Description and Shipping
    private static final By SELECTOR_ITEM_DESCRIPTION_DIV = By.cssSelector("#descItemNumber");
    private static final By SELECTOR_TAB_PANEL = By.cssSelector("#BottomPanelDF .tabbable");
    public static final By SELECTOR_DESCRIPTION_TAB_BUTTON
            = By.cssSelector("#BottomPanelDF .tabbable .nav li:nth-child(1)");
    public static final By SELECTOR_SHIPPING_TAB_BUTTON
            = By.cssSelector("#BottomPanelDF .tabbable .nav li:nth-child(2)");
    public static final By SELECTOR_DESCRIPTION_PANE
            = By.cssSelector("#BottomPanelDF .tabbable .tab-content-m .tab-pane:nth-child(1)");
    public static final By SELECTOR_SHIPPING_PANE
            = By.cssSelector("#BottomPanelDF .tabbable .tab-content-m .tab-pane:nth-child(2)");
    public static final By SELECTOR_COUNTRY_DROPDOWN = By.cssSelector("#shCountry");
    public static final By SELECTOR_ZIP_CODE_INPUT = By.cssSelector("#shZipCode");
    public static final By SELECTOR_SHIPPING_QUANTITY_INPUT = By.cssSelector("#shQuantity");
    public static final By SELECTOR_QUANTITY_ERROR_BOX_SHIPPING_TAB = By.cssSelector("#shQuantity-errTxt");

    // The Web Element inside the popup that happens when clicking "Add to Cart"
    private final static By SELECTOR_INSIDE_POPUP_ADD_TO_CART_LINK
            = By.cssSelector(".app-atc-layer-redesign-content-wrapper .btn-scnd:nth-child(2)");

    public ViewItemPage(WebDriver driver) {
        super(driver);
    }

    public WebElement getQuantityInputInShippingTab() {
        return driver.findElement(SELECTOR_SHIPPING_QUANTITY_INPUT);
    }

    public WebElement getQuantityErrorBoxInShippingTab() {
        return driver.findElement(SELECTOR_QUANTITY_ERROR_BOX_SHIPPING_TAB);
    }

    public void selectCountry(String displayedCountry) {
        WebElement element = driver.findElement(SELECTOR_COUNTRY_DROPDOWN);
        Select select = new Select(element);
        select.selectByVisibleText(displayedCountry);
    }

    public boolean isZipCodeInputDisplayed() {
        return isDisplayed(SELECTOR_ZIP_CODE_INPUT);
    }

    public boolean isQuantityInputDisplayed() {
        return isDisplayed(SELECTOR_SHIPPING_QUANTITY_INPUT);
    }

    public Optional<WebElement> getQuantityInputInShippingTabElement() {
        return driver.findElements(SELECTOR_SHIPPING_QUANTITY_INPUT).stream().findFirst();
    }

    public WebElement getTabPanel() {
        return driver.findElement(SELECTOR_TAB_PANEL);
    }

    public void toggleDescriptionTab() {
        click(SELECTOR_DESCRIPTION_TAB_BUTTON);
    }

    public void toggleShippingTab() {
        click(SELECTOR_SHIPPING_TAB_BUTTON);
    }

    public boolean isDescriptionTabDisplayed() {
        return isDisplayed(SELECTOR_DESCRIPTION_PANE);
    }

    public boolean isShippingTabDisplayed() {
        return isDisplayed(SELECTOR_SHIPPING_PANE);
    }

    public boolean isCountryDropdownDisplayed() {
        return isDisplayed(SELECTOR_COUNTRY_DROPDOWN);
    }

    public Optional<WebElement> getItemNumberElement() {
        return driver.findElements(SELECTOR_ITEM_DESCRIPTION_DIV).stream().findFirst();
    }

    public boolean containsProductRatingAtTop() {
        return elementExists(SELECTOR_PRODUCT_RATING_DIV);
    }

    public int getNumberItemsVisibleInCarousel(List<WebElement> elements) {
        int count = 0;
        List<Integer> items = new LinkedList<>();
        int index = 0;
        for (WebElement element : elements) {
            String attributeValue = element.getAttribute("aria-hidden");
            if (attributeValue == null) {
                count++;
                items.add(index);
            }
//            LOGGER.info(attributeValue);
            index++;
        }
        LOGGER.info("Items visible: " + items.toString());
        return count;
    }

    public Optional<WebElement> getBackButton(WebElement element) {
        List<WebElement> list = element.findElements(SELECTOR_MERCHANDISE_BUTTON);
        if (list.size() == 2) {
            return Optional.of(list.get(0));
        } else {
            return Optional.empty();
        }
    }

    public Optional<WebElement> getForwardButton(WebElement element) {
        List<WebElement> list = element.findElements(SELECTOR_MERCHANDISE_BUTTON);
        if (list.size() == 2) {
            return Optional.of(list.get(1));
        } else {
            return Optional.empty();
        }
    }

    public List<WebElement> getProductItemsFromImagePanel(WebElement imageElement) {
        List<WebElement> list = imageElement.findElements(SELECTOR_MERCHANDISE_ITEMS);
        return list;
    }

    public Optional<WebElement> getMerchandiseItemsPanel(String title) {
        return driver.findElements(SELECTOR_MERCHANDISE_PANEL)
                .stream()
                .filter(x -> {
                    try {
                        WebElement childElement = findElement(x, SELECTOR_MERCHANDISE_PANEL_TITLE);
                        LOGGER.info("text: " + childElement.getText());
                        return childElement.getText().contains(title);
                    } catch (NoSuchElementException ex) {
                        return false;
                    }
                })
                .findFirst();
    }

    public Optional<WebElement> getSimilarSponsoredItemsPanel() {
        return getMerchandiseItemsPanel(TITLE_SIMILAR_SPONSORED_ITEMS);
    }

    public Optional<WebElement> getSponsoredRecentItemPanel() {
        return getMerchandiseItemsPanel(TITLE_SPONSORED_ITEMS_RECENT);
    }

    public Optional<WebElement> getProductRatingSection() {
        return driver.findElements(By.cssSelector(SELECTOR_RATING_PANEL)).stream().findFirst();
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

    public WebElement getProductTitleElement() {
        return driver.findElement(By.cssSelector(SELECTOR_PRODUCT_TITLE));
    }

    public WebElement getBrandElement() {
        return driver.findElement(By.cssSelector(SELECTOR_BRAND));
    }


    public String getProductTitle() {
        return getText(SELECTOR_PRODUCT_TITLE);
    }

    public String getBrandText() {
        return getText(SELECTOR_BRAND);
    }

    public String getQuantityString() {
        WebElement element = getQuantityTextbox().get();
        return element.getAttribute("value");
    }

    public Optional<WebElement> getQuantityTextbox() {
        return driver.findElements(SELECTOR_QUANTITY_INPUT).stream().findFirst();
    }

    public WebElement getQuantityErrorBox() {
        return driver.findElement(SELECTOR_QUANTITY_ERROR_BOX);
    }

    private String getNumberAvailableText() {
        return getText(SELECTOR_NUM_AVAILABLE);
    }

    public Optional<WebElement> getNumberAvailableElement() {
        return driver.findElements(By.cssSelector(SELECTOR_NUM_AVAILABLE)).stream().findFirst();
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

    public WebElement getImageButton() {
        return driver.findElement(SELECTOR_IMAGE_BUTTON);
    }

    @Override
    public void navigateToPage() {
        throw new IllegalArgumentException("there is no specific view item page");
    }
}
