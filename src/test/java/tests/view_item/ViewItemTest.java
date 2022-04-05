package tests.view_item;

import base.CustomUtilities;
import base.BaseTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.ITest;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import pages.CartPage;
import pages.SellerPage;
import pages.ViewItemPage;
import pages.WatchlistPage;

import java.io.*;
import java.util.List;
import java.util.Optional;

import static org.testng.AssertJUnit.*;
import static org.testng.AssertJUnit.assertTrue;

public class ViewItemTest extends BaseTest implements ITest {

    private final static Logger LOGGER = LogManager.getLogger(ViewItemTest.class);

    private WebDriver driver;
    private ViewItemPage viewItemPage;
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
        LOGGER.info("Product: " + product.toString());
    }

    @BeforeMethod
    public void setup() {
        driver = getWebDriver();
        viewItemPage = ViewItemTest.navigateToPage(driver, itemId);
        watchlistPage = new WatchlistPage(driver);
        sellerPage = new SellerPage(driver);
        cartPage = new CartPage(driver);
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
    @Test(dataProvider = "templates")
    public void testWatchlist(WatchlistTestTemplate template) {
        ViewItemPage viPage = ViewItemTest.navigateToPage(driver, itemId);

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
    @Test()
    public void testSaveSeller() {
        ViewItemPage viPage = ViewItemTest.navigateToPage(driver, itemId);

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
    @Test()
    public void testAddToCart() {
        ViewItemPage viPage = ViewItemTest.navigateToPage(driver, itemId);

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
    @Test
    public void testSetQuantity() {
        ViewItemPage viPage = ViewItemTest.navigateToPage(driver, itemId);

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

    @Test
    public void testProductDetails() {
        ViewItemPage viPage = ViewItemTest.navigateToPage(driver, itemId);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(viPage.getProductTitle(), product.getProductTitle());
        softAssert.assertEquals(viPage.getBrandText(), product.getCondition());
        softAssert.assertEquals(viPage.getSellerLinkText(), product.getSellerName());
        softAssert.assertEquals(viPage.getPriceAsDouble(), product.getPrice());
        softAssert.assertAll();
    }

    /**
     * Tests that if there is a single image (with no previous and forward button),
     * then there should be no image thumbnail on the left
     * If there is more than a single image, then there should be an image thumbnail
     * on the left, and we should be able to view all the image thumbnails
     * by repeatedly clicking on the next button until it becomes disabled
     */
    @Test
    public void testImageNavigate() {
        ViewItemPage viPage = ViewItemTest.navigateToPage(driver, itemId);

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
        Object[][] result = new Object[productList.getProducts().size()][1];
        for (int i = 0; i < productList.getProducts().size(); i++) {
            result[i][0] = productList.getProducts().get(i);
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
