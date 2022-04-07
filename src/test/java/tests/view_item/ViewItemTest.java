package tests.view_item;

import base.CustomUtilities;
import base.BaseTest;
import base.TestNgLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.testng.ITest;
import org.testng.SkipException;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import pages.CartPage;
import pages.SellerPage;
import pages.ViewItemPage;
import pages.WatchlistPage;
import tests.TestGroups;

import javax.swing.text.View;
import java.io.*;
import java.util.List;
import java.util.Optional;

import static org.testng.AssertJUnit.*;
import static org.testng.AssertJUnit.assertTrue;

public class ViewItemTest extends BaseTest implements ITest {

    private final static Logger LOGGER = LogManager.getLogger(ViewItemTest.class);

    private WebDriver driver;
    private ViewItemPage viPage;
    private WatchlistPage watchlistPage;
    private SellerPage sellerPage;
    private CartPage cartPage;

    private ProductList.Product product;
    private String itemId;
    private final static String DATA_PROVIDER_PRODUCT = "product";

    @Factory(dataProvider = DATA_PROVIDER_PRODUCT)
    public ViewItemTest(ProductList.Product product) {
        this.product = product;
        this.itemId = product.getItemId();
    }

    @BeforeMethod
    public void setup() {
        driver = getWebDriver(false);
        viPage = ViewItemTest.navigateToPage(driver, itemId);
        watchlistPage = new WatchlistPage(driver);
        sellerPage = new SellerPage(driver);
        cartPage = new CartPage(driver);
    }

    @AfterMethod
    public void teardown() {
        // TODO
    }

    /**
     * Navogates to the View Item page for product with item number ITEM
     */
    public static ViewItemPage navigateToPage(WebDriver driver, String item) {
        ViewItemPage itemPage = new ViewItemPage(driver);
        itemPage.navigateItemNumber(item);
        LOGGER.info("Performing test for product name {}", itemPage.getProductTitle());
        return itemPage;
    }

    /**
     * Checks that various necessary web elements are displayed
     */
    @Test(groups = TestGroups.GUEST_OK,
            description = "Check necessary web elements are displayed")
    public void testDisplayElements() {
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(viPage.getProductTitleElement().isDisplayed(),
                "Product title element should be displayed");
        softAssert.assertTrue(viPage.getBrandElement().isDisplayed(),
                "Condition text should be displayed");
        softAssert.assertTrue(viPage.getQuantityTextbox().isDisplayed(),
                "Quantity textbox should be displayed");
        softAssert.assertAll();
    }

    /** Tests that the number of watchers is at least 0. */
    @Test(groups = TestGroups.GUEST_OK,
        description = "The number of watchers should be >= 0")
    public void testNumberWatchersNonnegative() {
        assertTrue("The number of watchers should be non-negative",
                viPage.getNumWatchers() >= 0);
    }


    /**
     * Check correct behavior by performing adding/removing of item from watchlist
     * via a custom operation (e.g. clicking watchlist button, clicking watchlist link)
     * that is specified by {@link WatchlistTestTemplate}
     *
     * Performs the following checks:
     * - does the button text change to the appropriate text
     * - does the watchlist link at the upper right of the portion have its
     *    visibility toggle on/off
     * - the number of watchers is updated
     */
    @Test(dataProvider = "templates", groups = TestGroups.LOGIN_REQUIRED,
        description = "Add/remove items from the watchlist")
    public void testWatchlist(WatchlistTestTemplate template) {
        // Checking Prerequisite
        if (!viPage.getWatchingText().equals("Add to Watchlist")) {
            throw new SkipException("Prerequisite for test not fulfilled");
        }

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,200)", "");

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(viPage.isWatchlistLinkVisible(),
                "When item not watched, watch link should be visible");

        int numWatchers = viPage.getNumWatchers();
        template.addToWatchlist(viPage, watchlistPage, itemId);
        viPage.navigateItemNumber(itemId);

        softAssert.assertEquals(viPage.getWatchingText(), "Watching",
                "When item is being watched");
        softAssert.assertEquals(viPage.getNumWatchers(), numWatchers + 1,
                "number of watchers when item is watched");
        softAssert.assertFalse(viPage.isWatchlistLinkVisible(),
                "When item is watched, watch link should not be visible");

        template.removeFromWatchlist(viPage, watchlistPage, itemId);
        viPage.navigateItemNumber(itemId);
        js.executeScript("window.scrollBy(0,200)", "");

        softAssert.assertEquals(viPage.getWatchingText(), "Add to Watchlist",
                "When item is no longer being watched");
        softAssert.assertEquals(viPage.getNumWatchers(), numWatchers,
                "number of watchers when item is no longer watched");
        softAssert.assertTrue(viPage.isWatchlistLinkVisible(),
                "when item is no longer watched, watch link should be visible");
        softAssert.assertAll();
    }

    @DataProvider(name="templates")
    public static Object[][] getTemplates() {
        return new Object[][] {
                {new WatchlistTestTemplate.ClickButtonTemplate() },
//                {new WatchlistTestTemplate.WatchlistLinkTemplate()}
                {new WatchlistTestTemplate.WatchlistPageTemplate()}
        };
    }

    private final static String SELLER_SAVED_TEXT = "Saved";
    private final static String SELLER_NOT_SAVED_TEXT = "Save this seller";

    /**
     * Tests the "save seller" link on the right portion of the page.
     * Verifies that clicking "Save this seller" link once changes text to "Saved,
     * and clicking on it again changes text back to original text "Save this seller"
     */
    @Test(groups = TestGroups.LOGIN_REQUIRED,
            description = "Add/Remove the seller from saved sellers")
    public void testSaveSeller() {
        // Check prerequisite
        if (!viPage.getSaveSellerLinkText().equalsIgnoreCase(SELLER_NOT_SAVED_TEXT)) {
            throw new SkipException("Expecting the seller not be saved");
        }

        SoftAssert softAssert = new SoftAssert();

        // Save the seller
        viPage.clickSaveSellerLink();
        CustomUtilities.sleep(2000);
        viPage.clickSellerLink();
        softAssert.assertEquals(sellerPage.getSaveElement().getText(), SELLER_SAVED_TEXT,
                "On seller page, expected link text to change to " + SELLER_SAVED_TEXT);
        viPage.navigateItemNumber(itemId);
        softAssert.assertEquals(viPage.getSaveSellerLinkText(), SELLER_SAVED_TEXT,
                "On VI page, expected link text to change to " + SELLER_SAVED_TEXT);

        // Unsave the seller
        viPage.clickSaveSellerLink();
        CustomUtilities.sleep(2000);
        softAssert.assertEquals(viPage.getSaveSellerLinkText(), SELLER_NOT_SAVED_TEXT,
                "On VI page, expected link text to change back to " + SELLER_NOT_SAVED_TEXT);
        softAssert.assertAll();
    }

    /**
     * Tests "Add to Cart" functionality
     * 1. Click "Add to Cart" button on VI page
     * 2. When popup window appears, click "Go to Cart" which takes you to Cart page
     * 3. From the cart page, verify that the quantity of the item is 1
     * 4. From the cart page, delete the item
     * 5. Navigate back to the VI page.
     * 6. From VI page, verify that the "Add to Cart" button appears.
     */
    @Test(groups = TestGroups.LOGIN_REQUIRED,
            description = "Add/remove the item from the cart")
    public void testAddToCart() {
        // Checking prerequisite
        WebElement cartButton = viPage.getCartButton();
        if (!cartButton.getText().contains("Add to cart")) {
            throw new SkipException("The product is already in the cart");
        }

        cartButton.click();
        CustomUtilities.sleep(4000); // TODO : replace with explicit wait

        // do not need to switch windows to handle popup
        viPage.clickAddToCartInsidePopup();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(cartPage.getQuantity(itemId), "1",
                "The quantity of the product on the cart page should be 1");

        softAssert.assertTrue(cartPage.deleteItem(itemId),
                "The product was not deleted from the cart page");
        CustomUtilities.sleep(2000);
        viPage.navigateItemNumber(itemId);
        cartButton = viPage.getCartButton();
        softAssert.assertTrue(cartButton.getText().contains("Add to cart"),
                "The product should have been removed from the cart");
        CustomUtilities.sleep(2000);

        softAssert.assertAll();
    }

    /**
     * Tests various input to the Set Quantity field
     */
    @Test(groups = TestGroups.GUEST_OK,
            description = "Test various quantities for item")
    public void testSetQuantity() {
        int numAvailable = viPage.getNumberAvailable();
        WebElement textbox = viPage.getQuantityTextbox();
        textbox.clear();
        textbox.sendKeys("1");
        CustomUtilities.sleep(1000);
        WebElement errorBox = viPage.getQuantityErrorBox();
        if (numAvailable == 0) {
            assertTrue("Error box should be displayed", errorBox.isDisplayed());
        } else {
            assertFalse("Error box should not be displayed", errorBox.isDisplayed());
        }

        textbox.clear();
        textbox.sendKeys("0");
        CustomUtilities.sleep(1000);
        assertTrue("Error box should be displayed when entering a quantity of 0",
                errorBox.isDisplayed());

        textbox.clear();
        textbox.sendKeys(numAvailable + "");
        CustomUtilities.sleep(1000);
        assertFalse("Error box should not be displayed when entering quantity of " + numAvailable,
                errorBox.isDisplayed());

        textbox.clear();
        textbox.sendKeys((numAvailable + 1) + "");
        CustomUtilities.sleep(1000);
        assertTrue("Error box should be displayed when entering quantity one more than allowable",
                errorBox.isDisplayed());
    }

    @Test(groups = TestGroups.GUEST_OK,
        description = "Tests that the product details is displayed correctly")
    public void testProductDetails() {
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(viPage.getProductTitle(), product.getProductTitle());
        softAssert.assertEquals(viPage.getBrandText(), product.getCondition());
        softAssert.assertEquals(viPage.getSellerLinkText(), product.getSellerName());
        softAssert.assertEquals(viPage.getPriceAsDouble(), product.getPrice());
        softAssert.assertAll();
    }

    @Test(groups = TestGroups.GUEST_OK,
        description = "Image panel should be displayed")
    public void testImageDisplayed() {
        assertTrue("The main image should be displayed", viPage.getImageButton().isDisplayed());
    }

    /**
     * Tests that if there is a single image (with no previous and forward button),
     * then there should be no image thumbnail on the left
     * If there is more than a single image, then there should be an image thumbnail
     * on the left, and we should be able to view all the image thumbnails
     * by repeatedly clicking on the next button until it becomes disabled
     */
    @Test(groups=TestGroups.GUEST_OK,
        description = "Can navigate through all the images")
    public void testImageNavigate() {
        List<WebElement> elements = viPage.getImageThumbnails();

        Optional<WebElement> prevButton = viPage.getImagePreviousButton();
        Optional<WebElement> nextButton = viPage.getImageNextButton();

        if (!prevButton.isPresent()) {
            assertEquals("If previous button is not present, there should be no image thumbnails",
                    elements.size(), 0);
        } else {
            assertTrue("Next button should be present", nextButton.isPresent());
            int count = 0;
            assertTrue("Prev button should be disabled",
                    isNavigationButtonDisabled(prevButton.get()));
            assertNthThumbnail(elements, count);
            while (count < elements.size() - 1) {
                CustomUtilities.sleep(1000);
                assertTrue("Next button should be enabled",
                        !isNavigationButtonDisabled(nextButton.get()));
                nextButton.get().click();
                count++;
                assertNthThumbnail(elements, count);
            }
            assertTrue("Prev button should be enabled",
                    !isNavigationButtonDisabled(prevButton.get()));
            assertTrue("Next button should be disabled",
                    isNavigationButtonDisabled(nextButton.get()));
        }
    }

    private boolean isNavigationButtonDisabled(WebElement element) {
        return element.getAttribute("class").contains("disabled");
    }

    private void assertNthThumbnail(List<WebElement> elements, int index) {
        if (index < elements.size()) {
            WebElement element = elements.get(index);
            assertTrue("The element at index " + index + " should be selected",
                    element.getAttribute("class").contains("selected"));
        }
    }

    /**
     * If the product ratings text is present at the top of the page directly
     * below the product title, then there should be a section at the bottom
     * of the page dedicated to product ratings.
     * If no product ratings text is present, then there should not be a section
     * at the bottom of the page
     */
    @Test(groups=TestGroups.GUEST_OK,
        description = "Consistent information for product ratings")
    public void testProductRatingsSectionAvailable() {
        Optional<WebElement> productRatingSection = viPage.getProductRatingSection();
        if (viPage.containsProductRatingAtTop()) {
            assertTrue("Ratings and Review section must be present",
                    productRatingSection.isPresent());
            assertTrue("Ratings and Review section must be displayed",
                    productRatingSection.get().isDisplayed());
        } else {
            assertFalse("Ratings and Review section must not be present",
                    productRatingSection.isPresent());
        }
    }

    /**
     * Tests that the "Similar sponsored items" image panel is displayed
     * on startup of the VI page.
     * This seems to only be the case for "US" locale.
     * "UK" locale is treated differently.
     */
    @Test(groups=TestGroups.GUEST_OK,
        description = "Similar sponsored items panel is displayed on startup",
        enabled = false)
    public void testSimilarSponsoredItemsAvailable() {
        viPage.scrollDownAndWait(900, 4000);

        LOGGER.info("Searching for {}", ViewItemPage.TITLE_SIMILAR_SPONSORED_ITEMS);
        Optional<WebElement> element = viPage.getSimilarSponsoredItemsPanel();
        assertTrue("Similar sponsored items panel must be present",
                element.isPresent());
        assertTrue("Similar sponsored items panel must be displayed",
                element.get().isDisplayed());
    }

    /**
     * Tests that the "Sponsored items based on your recent views" image panel is not initially
     * present nor displayed, but when scrolling down the page, the image panel
     * becomes present and displayed
     */
    @Test(groups=TestGroups.GUEST_OK,
            description = "Recent views panel is displayed when scrolling down",
            enabled = false)
    public void testSponsoredItemsRecentView() {
        LOGGER.info("Searching for {}", ViewItemPage.TITLE_SPONSORED_ITEMS_RECENT);

        Optional<WebElement> element = viPage.getSponsoredRecentItemPanel();
        assertFalse("Panel should not be present", element.isPresent());
        for (int i = 0; i < 5; i++) {
            viPage.scrollDownAndWait(1000, 1000);
        }

        LOGGER.info("After scrolling down");
        element = viPage.getSponsoredRecentItemPanel();
        assertTrue("Panel should be present", element.isPresent());
        assertTrue("Panel should be displayed", element.get().isDisplayed());
    }

    /**
     * Tests that we can navigate through the image panel for
     * "Similar sponsored items"
     */
    @Test(groups=TestGroups.GUEST_OK,
        description = "Can navigate through 'Similar sponsored items' panel",
        enabled = false)
    public void testSimilarSponsoredItemsNavigate() {
        viPage.scrollDownAndWait(900, 4000);

        TestNgLogger.log("Testing that we can navigate through the image panel for 'Similar sponsored items'");
        helperTestItemPanelClickThrough(ViewItemPage.TITLE_SIMILAR_SPONSORED_ITEMS, viPage);
    }

    /**
     * Tests that we can navigate through the image panel for
     * "Sponsored items based on your recent views"
     */
    @Test(groups=TestGroups.GUEST_OK,
        description = "Can navigate through 'recent views' panel",
        enabled = false)
    public void testSponsoredItemsRecentViewsNavigate() {
        viPage.scrollDownAndWait(2000, 2000);
        viPage.scrollDownAndWait(2000, 2000);
        TestNgLogger.log("Testing that we can navigate through the image panel for " +
                "'Sponsored items based on your recent views'");

        helperTestItemPanelClickThrough(ViewItemPage.TITLE_SPONSORED_ITEMS_RECENT, viPage);
    }

    private void helperTestItemPanelClickThrough(String itemPanelSelector,
                                                 ViewItemPage viPage) {
        Optional<WebElement> element = viPage.getSimilarSponsoredItemsPanel();
        assertTrue("Panel should be present", element.isPresent());
        Optional<WebElement> forwardButton = viPage.getForwardButton(element.get());
        assertTrue("Forward button should be present", forwardButton.isPresent());
        List<WebElement> items = viPage.getProductItemsFromImagePanel(element.get());

        for (int i = 0; i < 3; i++) {
            long numDisplayed = viPage.getNumberItemsVisibleInCarousel(items);
            LOGGER.info("num displayed: " + numDisplayed);
            CustomUtilities.sleep(2000);
            assertTrue("Number of displayed must be 5", numDisplayed == 5);
            if (forwardButton.get().isEnabled()) {
                forwardButton.get().click();
            } else {
                break;
            }
        }
    }

    @Test(groups = TestGroups.GUEST_OK,
        description = "Item number displays correctly under 'Description' pane")
    public void testItemNumberDisplay() {
        Optional<WebElement> element = viPage.getItemNumberElement();
        assertTrue("element should be present", element.isPresent());
        assertTrue("element should be displayed", element.get().isDisplayed());
        assertEquals("item number should match",
                product.getItemId(), element.get().getText());
    }

    @Test(groups = TestGroups.GUEST_OK,
        description = "Toggling between 'Description' and 'Shipping' tabs")
    public void testToggleTabs() {
        Actions actions = new Actions(driver);
        actions.moveToElement(viPage.getTabPanel());
        viPage.scrollDownAndWait(200, 0);
        viPage.toggleDescriptionTab();
        CustomUtilities.sleep(3000);
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(viPage.isDescriptionTabDisplayed(),
                "Description tab should be displayed");
        softAssert.assertFalse(viPage.isShippingTabDisplayed(),
                "Shipping tab should not be displayed");

        viPage.toggleShippingTab();
        CustomUtilities.sleep(3000);
        softAssert.assertFalse( viPage.isDescriptionTabDisplayed(),
                "Description tab should not be displayed");
        softAssert.assertTrue(viPage.isShippingTabDisplayed(),
                "Shipping tab should be displayed");
        softAssert.assertAll();
    }

    @Test(groups = TestGroups.GUEST_OK,
        description = "The quantity input in the shipping tab displays correctly")
    public void testQuantityInShippingTab() {
        Actions actions = new Actions(driver);
        actions.moveToElement(viPage.getTabPanel());
        viPage.scrollDownAndWait(200, 0);

        viPage.toggleDescriptionTab();
        CustomUtilities.sleep(3000);
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertFalse(viPage.isQuantityInputDisplayed(),
                "Quantity input should not be displayed");
        viPage.toggleShippingTab();
        CustomUtilities.sleep(3000);
        if (viPage.getQuantityString().equalsIgnoreCase("1")) {
            softAssert.assertFalse(viPage.isQuantityInputDisplayed(),
                    "Since the quantity is 1, no 'quantity input' should not be displayed");
        } else {
            softAssert.assertTrue(viPage.isQuantityInputDisplayed(),
                    "'Quantity input' should be displayed");
        }
        softAssert.assertAll();
    }

    @Test(groups = TestGroups.GUEST_OK,
            description = "Country dropdown in 'Shipping and Payments' tab")
    public void testCountryDropdownVisible() {
        Actions actions = new Actions(driver);
        actions.moveToElement(viPage.getTabPanel());
        viPage.scrollDownAndWait(200, 0);

        viPage.toggleDescriptionTab();
        CustomUtilities.sleep(3000);
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertFalse(viPage.isCountryDropdownDisplayed(),
                "Country dropdown should not be displayed");

        viPage.toggleShippingTab();
        CustomUtilities.sleep(3000);
        softAssert.assertTrue(viPage.isCountryDropdownDisplayed(),
                "Country dropdown should be displayed");
        softAssert.assertAll();
    }

    @Test(groups = TestGroups.GUEST_OK,
        description = "Changing country dropdown selection toggles zip code display")
    public void testZipCodeToggle() {
        Actions actions = new Actions(driver);
        actions.moveToElement(viPage.getTabPanel());
        viPage.scrollDownAndWait(200, 2000);
        viPage.toggleShippingTab();

        viPage.selectCountry("-Select-");
        assertFalse("Zip code input should not be displayed", viPage.isZipCodeInputDisplayed());

        viPage.selectCountry("United States");
        assertTrue("Zip code input should be displayed", viPage.isZipCodeInputDisplayed());
    }

    @Test(groups = TestGroups.GUEST_OK,
        description = "Set quantity for quantity input in shipping tab")
    public void testSetQuantityInputInShippingTab() {
        Actions actions = new Actions(driver);
        actions.moveToElement(viPage.getTabPanel());
        viPage.scrollDownAndWait(200, 2000);
        viPage.toggleShippingTab();

        int numAvailable = viPage.getNumberAvailable();
        if (numAvailable != 1) {
            WebElement textbox = viPage.getQuantityInputInShippingTab();
            textbox.clear();
            textbox.sendKeys("1");
            CustomUtilities.sleep(1000);
            WebElement errorBox = viPage.getQuantityErrorBoxInShippingTab();
            if (numAvailable == 0) {
                assertTrue("Error box should be displayed", errorBox.isDisplayed());
            } else {
                assertFalse("Error box should not be displayed", errorBox.isDisplayed());
            }

            textbox.clear();
            textbox.sendKeys("0");
            CustomUtilities.sleep(1000);
            assertTrue("Error box should be displayed when entering a quantity of 0",
                    errorBox.isDisplayed());

            textbox.clear();
            textbox.sendKeys(numAvailable + "");
            CustomUtilities.sleep(1000);
            assertFalse("Error box should not be displayed when entering quantity of " + numAvailable,
                    errorBox.isDisplayed());

            textbox.clear();
            textbox.sendKeys((numAvailable + 1) + "");
            CustomUtilities.sleep(1000);
            assertTrue("Error box should be displayed when entering quantity one more than allowable",
                    errorBox.isDisplayed());
        }
    }

    @DataProvider(name=DATA_PROVIDER_PRODUCT)
    public static Object[][] getTests() {
        String filename = "src//test//resources//view_item//product_list.yaml";
        Yaml yaml = new Yaml(new Constructor(ProductList.class));
        LOGGER.info("yaml initialized to take in a ProductList class");
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ProductList productList = yaml.load(inputStream);
        List<ProductList.Product> includedProducts = productList.getIncludedProducts();

        Object[][] result = new Object[includedProducts.size()][1];
        for (int i = 0; i < includedProducts.size(); i++) {
            result[i][0] = includedProducts.get(i);
        }
        return result;
    }

    @Override
    public String getTestName() {
        return "ViewItem__ " + product.getProductTitle();
    }

    @Override
    public String toString() {
        return "ViewItem:" + product.getProductTitle();
    }
}
