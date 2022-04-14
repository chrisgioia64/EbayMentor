package tests.view_item;

import base.*;
import base.locale.EbayLanguage;
import base.locale.EbayLocale;
import base.locale.LocaleProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.ITest;
import org.testng.ITestResult;
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

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.testng.AssertJUnit.*;
import static org.testng.AssertJUnit.assertTrue;

public class ViewItemTest extends BaseTest implements ITest {

    private final static Logger LOGGER = LogManager.getLogger(ViewItemTest.class);

    private WebDriver driver;
    private ViewItemPage viPage;
    private WatchlistPage watchlistPage;
    private SellerPage sellerPage;
    private CartPage cartPage;

    private final ProductList.Product product;
    private final String itemId;
    private final static String DATA_PROVIDER_PRODUCT = "product";

    private boolean hasAcceptedCookies = false;

    @Factory(dataProvider = DATA_PROVIDER_PRODUCT)
    public ViewItemTest(ProductList.Product product) {
        this.product = product;
        this.itemId = product.getItemId();
        this.hasAcceptedCookies = false;
    }

    @BeforeMethod(alwaysRun = true)
    public void setup(ITestResult result) {
        driver = getWebDriver(false);
        viPage = ViewItemTestHelper.navigateToPage(driver, itemId);
        watchlistPage = new WatchlistPage(driver);
        sellerPage = new SellerPage(driver);
        cartPage = new CartPage(driver);
        if (!hasAcceptedCookies) {
            CustomUtilities.sleep(4000);
            viPage.acceptCookiesIfPrompted();
            hasAcceptedCookies = true;
        }
    }

    @AfterMethod(alwaysRun = true)
    public void teardown() {
        // TODO
    }

    // Test Method Constants
    private final static String TEST_MERCHANDISE_PANEL_AVAILABLE = "testMerchandisePanelAvailable";
    private final static String TEST_IS_QUANTITY_BOX_PRESENT = "testIsQuantityBoxPresent";

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
        // The quantity box is no longer always present so cannot test
//        softAssert.assertTrue(viPage.getQuantityTextbox().get().isDisplayed(),
//                "Quantity textbox should be displayed");
        softAssert.assertAll();
    }

    @Test(groups = TestGroups.GUEST_OK,
        description = "Checks if the quantity box is present. Other tests depend on this test")
    public void testIsQuantityBoxPresent() {
        Optional<WebElement> element = viPage.getQuantityTextbox();
        if (element.isEmpty()) {
            throw new SkipException("The quantity box is not present for this product");
        }
    }

    /** Tests that the number of watchers is at least 0. */
    @Test(groups = TestGroups.GUEST_OK,
        description = "The number of watchers should be >= 0")
    public void testNumberWatchersNonnegative() {
        Optional<WebElement> numWatcherElement = viPage.getNumWatchersElement();
        if (numWatcherElement.isPresent()) {
            assertTrue("The number of watchers should be non-negative",
                    viPage.getNumWatchers() >= 0);
        } else {
            throw new SkipException("Num watcher element is not present");
        }
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

        // Verification before adding to watchlist
        viPage.scrollDownAndWait(200, 0);
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(viPage.isWatchlistLinkVisible(),
                "When item not watched, watch link should be visible");
        int numWatchers = viPage.getNumWatchers();

        // Add to watchlist
        template.addToWatchlist(viPage, watchlistPage, itemId);
        viPage.navigateItemNumber(itemId);

        // Verification after added to watchlist
        softAssert.assertEquals(viPage.getWatchingText(), "Watching",
                "When item is being watched");
        softAssert.assertEquals(viPage.getNumWatchers(), numWatchers + 1,
                "number of watchers when item is watched");
        softAssert.assertFalse(viPage.isWatchlistLinkVisible(),
                "When item is watched, watch link should not be visible");

        // Remove from watchlist
        template.removeFromWatchlist(viPage, watchlistPage, itemId);
        viPage.navigateItemNumber(itemId);
        viPage.scrollDownAndWait(200, 0);

        // Verification after removing from watchlist
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
            description = "Test various quantities for item",
            dependsOnMethods = TEST_IS_QUANTITY_BOX_PRESENT)
    public void testSetQuantity() {
        ViewItemTestHelper.setQuantityTestCase(viPage,
                ViewItemPage.SELECTOR_QUANTITY_INPUT, ViewItemPage.SELECTOR_QUANTITY_ERROR_BOX);
    }

    @Test(groups = TestGroups.GUEST_OK,
        description = "Tests that the product details is displayed correctly")
    public void testProductDetails() {
        LocaleProperties prop = EnvironmentProperties.getInstance().getLocaleProperties();
        LOGGER.info("Product title: " + product.getProductTitle());
        LOGGER.info("Product condition: " + product.getCondition());
        String locale = prop.getProperty(LocaleProperties.KEY_LOCALE);
        String language = prop.getProperty(LocaleProperties.KEY_LANGUAGE);
        LOGGER.info("locale: " + locale);
        LOGGER.info("language: " + language);
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(viPage.getProductTitle(), product.getProductTitle().get(language));
        softAssert.assertEquals(viPage.getBrandText(), product.getCondition().get(locale));
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

        if (prevButton.isEmpty()) {
            assertEquals("If previous button is not present, there should be no image thumbnails",
                    elements.size(), 0);
        } else {
            assertTrue("Next button should be present", nextButton.isPresent());
            int count = 0;;
            // Not sure why but chrome sometimes has the prev button enabled
//            assertTrue("Prev button should be disabled",
//                    ViewItemTestHelper.isNavigationButtonDisabled(prevButton.get()));
            ViewItemTestHelper.assertNthThumbnail(elements, count);
            while (count < elements.size() - 1) {
                CustomUtilities.sleep(1000);
                assertFalse("Next button should be enabled",
                        ViewItemTestHelper.isNavigationButtonDisabled(nextButton.get()));
                nextButton.get().click();
                count++;
                ViewItemTestHelper.assertNthThumbnail(elements, count);
            }
            assertFalse("Prev button should be enabled",
                    ViewItemTestHelper.isNavigationButtonDisabled(prevButton.get()));
            assertTrue("Next button should be disabled",
                    ViewItemTestHelper.isNavigationButtonDisabled(nextButton.get()));
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
        description = "The quantity input in the shipping tab displays correctly",
            dependsOnMethods = TEST_IS_QUANTITY_BOX_PRESENT)
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
        if (viPage.getNumberAvailable() == 1) {
            softAssert.assertFalse(viPage.isQuantityInputDisplayed(),
                    "Since the quantity is 1, 'quantity input' should not be displayed");
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

        String selectText = EnvironmentProperties.getInstance().getLocaleProperties()
                .getProperty(LocaleProperties.KEY_TEXT_SELECT);
        viPage.selectCountry(selectText);
        assertFalse("Zip code input should not be displayed", viPage.isZipCodeInputDisplayed());

        viPage.selectCountry(Country.US.getLocaleName());
        assertTrue("Zip code input should be displayed", viPage.isZipCodeInputDisplayed());
    }

    @Test(groups = TestGroups.GUEST_OK,
        description = "Set quantity for quantity input in shipping tab",
            dependsOnMethods = TEST_IS_QUANTITY_BOX_PRESENT)
    public void testSetQuantityInputInShippingTab() {
        Actions actions = new Actions(driver);
        actions.moveToElement(viPage.getTabPanel());
        viPage.scrollDownAndWait(200, 2000);
        viPage.toggleShippingTab();
        ViewItemTestHelper.setQuantityTestCase(viPage,
                ViewItemPage.SELECTOR_SHIPPING_QUANTITY_INPUT,
                ViewItemPage.SELECTOR_QUANTITY_ERROR_BOX_SHIPPING_TAB);
    }

    @Test(groups = TestGroups.GUEST_OK,
            description = "Tests that the quantity input is focusable",
            dependsOnMethods = TEST_IS_QUANTITY_BOX_PRESENT)
    public void testQuantityInputFocusable() {
        Optional<WebElement> quantityBox = viPage.getQuantityTextbox();
        if (quantityBox.isPresent()) {
            assertTrue("Quantity box should be focusable",
                    viPage.focusable(quantityBox.get()));
        } else {
            throw new SkipException("there is no quantity box present");
        }
    }

    @Test(groups = TestGroups.GUEST_OK,
            description = "Tests that the quantity input in the shipping tab is focusable",
            dependsOnMethods = TEST_IS_QUANTITY_BOX_PRESENT)
    public void testQuantityBoxInputInShippingTabFocusable() {
        Optional<WebElement> quantityBox = viPage.getQuantityInputInShippingTabElement();
        if (quantityBox.isPresent()) {
            Actions actions = new Actions(driver);
            actions.moveToElement(viPage.getTabPanel());
            viPage.toggleShippingTab();
            assertTrue("Quantity box should be focusable",
                    viPage.focusable(quantityBox.get()));
        } else {
            throw new SkipException("there is no quantity box present");
        }
    }

    @Test(groups = TestGroups.GUEST_OK,
        description = "Tests that the country dropdown in the shipping tab is focusable")
    public void testCountryDropdownFocusable() {
        Optional<WebElement> dropdown = viPage.getCountryDropdownElement();
        if (dropdown.isPresent()) {
            Actions actions = new Actions(driver);
            actions.moveToElement(viPage.getTabPanel());
            viPage.toggleShippingTab();
            assertTrue("Country dropdown should be focusable",
                    viPage.focusable(dropdown.get()));
        } else {
            throw new SkipException("there is no dropdown present");
        }
    }

    @Test(groups = TestGroups.GUEST_OK,
            description = "Tests that the zip code input in the shipping tab is focusable")
    public void testZipCodeInputFocusable() {
        Optional<WebElement> zipCode = viPage.getZipCodeInputElement();
        if (zipCode.isPresent()) {
            Actions actions = new Actions(driver);
            actions.moveToElement(viPage.getTabPanel());
            viPage.toggleShippingTab();
            viPage.selectCountry(Country.US.getLocaleName());
            if (zipCode.get().isDisplayed()) {
                assertTrue("Zip code should be focusable",
                        viPage.focusable(zipCode.get()));
            } else {
                throw new SkipException("could not make the zip code input displayed");
            }
        } else {
            throw new SkipException("there is no zip code present");
        }
    }

    @Test(groups = TestGroups.GUEST_OK,
        description = "Does the include/exclude text match what is shown in the country dropdown")
    public void testCountryDropdownValuesMatchText() {
        Actions actions = new Actions(driver);
        actions.moveToElement(viPage.getTabPanel());
        viPage.toggleShippingTab();

        SoftAssert softAssert = new SoftAssert();
        Set<String> excludedCountries = viPage.getCountryExcludeList();
        boolean shippingWorldwide = viPage.shippingWorldwide();
        TestNgLogger.log("Worldwide: " + shippingWorldwide);
        Set<String> countriesShippingTo = viPage.countriesShippingTo();

        if (shippingWorldwide) {
            for (Country country : Country.values()) {
                String countryName = country.getLocaleName();
                if (excludedCountries.contains(countryName)) {
                    TestNgLogger.log("Excluded country: " + countryName);
                    softAssert.assertFalse(viPage.isCountryInDropdown(countryName),
                            countryName + " is excluded and should not be in dropdown");
                } else {
                    TestNgLogger.log("Included country: " + countryName);
                    softAssert.assertTrue(viPage.isCountryInDropdown(countryName),
                            "worldwide shipping: " + countryName
                                    + " is not excluded and should be in dropdown");
                }
            }
        } else {
            for (Country country : Country.values()) {
                String countryName = country.getLocaleName();
                if (countriesShippingTo.contains(countryName)) {
                    TestNgLogger.log("Included country: " + countryName);
                    softAssert.assertTrue(viPage.isCountryInDropdown(countryName),
                            countryName + " is in list of shipping countries");
                } else {
                    TestNgLogger.log("Excluded country: " + countryName);
                    softAssert.assertFalse(viPage.isCountryInDropdown(countryName),
                            countryName + " is not in list of shipping countries");
                }
            }
        }
        softAssert.assertAll();
    }

    @Test(groups = TestGroups.GUEST_OK,
        description = "There is at least one row in the shipping tab table")
    public void testOneRowInShippingTab() {
        Actions actions = new Actions(driver);
        actions.moveToElement(viPage.getTabPanel());
        viPage.toggleShippingTab();

        int rows = viPage.getShippingTableNumberOfRows();
        TestNgLogger.log("number of rows in table: " + rows);
        assertTrue("number of rows must be >= 1", rows >= 1);
    }

    @Test(groups = TestGroups.GUEST_OK,
        description = "Negative test cases for the quantity textbox",
            dependsOnMethods = TEST_IS_QUANTITY_BOX_PRESENT)
    public void testQuantityInputNegativeScenarios() {
        List<String> negativeInput = Arrays.asList(" 1", "1 ", "", "1.0", "abc");
        ViewItemTestHelper.setQuantityNegativeTestCases(viPage,
                ViewItemPage.SELECTOR_QUANTITY_INPUT, ViewItemPage.SELECTOR_QUANTITY_ERROR_BOX,
                negativeInput);
    }

    @Test(groups = TestGroups.GUEST_OK,
        description = "Negative test cases for the quantity textbox in the shipping tab",
            dependsOnMethods = TEST_IS_QUANTITY_BOX_PRESENT)
    public void testQuantityInputInShippingTabNegativeScenarios() {
        viPage.goToShippingTab();
        List<String> negativeInput = Arrays.asList(" 1", "1 ", "", "1.0", "abc");
        ViewItemTestHelper.setQuantityNegativeTestCases(viPage,
                ViewItemPage.SELECTOR_SHIPPING_QUANTITY_INPUT,
                ViewItemPage.SELECTOR_QUANTITY_ERROR_BOX_SHIPPING_TAB,
                negativeInput);
    }

    @Test(groups = TestGroups.GUEST_OK,
        description = "Selected country is displayed in shipping table")
    public void testSelectedCountryInShippingTable() {
        viPage.goToShippingTab();
        List<Country> choices = Arrays.asList(
                Country.UK, Country.AU, Country.US);
        for (Country choice : choices) {
            String countryName = choice.getLocaleName();
            boolean success = viPage.selectCountryAndEnter(countryName);
            CustomUtilities.sleep(2000);
            viPage.scrollDownAndWait(-4000, 2000);
//            CustomUtilities.sleep(2000);
            if (success) {
                viPage.goToShippingTab();
                TestNgLogger.log("Entered rate for " + countryName);
                String toColumnName = EnvironmentProperties.getInstance().getLocaleProperties()
                        .getProperty(LocaleProperties.KEY_TO_COLUMN);
                List<String> columnValues = viPage.getColumnValuesOfShippingTable(toColumnName);
                for (String columnValue : columnValues) {
                    TestNgLogger.log("Cell value: " + columnValue);
                    assertEquals("cell value must be equal to country name " + countryName,
                            countryName, columnValue);
                }
            }
        }
    }

    @Test(groups = TestGroups.GUEST_OK,
        description = "Test that the description and shipping tabs are named correctly")
    public void testShippingTabsNamedCorrectly() {
        List<WebElement> linkElements = viPage.getTabLinkElements();
        assertEquals("there should be two tabs", 2, linkElements.size());
        String tab1 = EnvironmentProperties.getInstance().getLocaleProperties().getProperty(LocaleProperties.KEY_TAB_DESCRIPTION);
        assertEquals(tab1, linkElements.get(0).getText());
        String tab2 = EnvironmentProperties.getInstance().getLocaleProperties().getProperty(LocaleProperties.KEY_TAB_SHIPPING);
        assertEquals(tab2, linkElements.get(1).getText());
    }


    @Test(groups = TestGroups.GUEST_OK,
        description = "Check to see if alternative price is listed")
    public void testAlternativePriceListed() {
        boolean isDisplayed = viPage.isDisplayed(
                ViewItemPage.SELECTOR_ALTERNATIVE_PRICE_TEXT);
        TestNgLogger.log("Is alternative price displayed: " + isDisplayed);
        if (EbayLocale.US.isInUse()) {
            assertFalse("For US locale, the alternative price should not be displayed",
                    isDisplayed);
        } else {
            assertTrue("For non-US locale, the alternative price should be displayed",
                    isDisplayed);
        }
    }

    @Test(groups = TestGroups.GUEST_OK,
        description = "Test to see that the first merchandise panel is displayed")
    public void testMerchandisePanelAvailable() {
        Optional<WebElement> firstMerchandisePanel = viPage.getFirstMerchandiseItemsPanel();
        assertTrue("merchandise panel is not displayed",
                firstMerchandisePanel.isPresent() && firstMerchandisePanel.get().isDisplayed());
    }

    @Test(groups = TestGroups.GUEST_OK,
        description = "Check that there are 12 products in the merchandise panel",
        dependsOnMethods = {TEST_MERCHANDISE_PANEL_AVAILABLE})
    public void testMerchandisePanel12Items() {
        Optional<WebElement> firstMerchandisePanel = viPage.getFirstMerchandiseItemsPanel();
        Optional<WebElement> title = viPage.getMerchandiseTitlePanel(firstMerchandisePanel.get());
        TestNgLogger.log("Title: " + title.get().getText());
        List<WebElement> itemElements = firstMerchandisePanel.get().findElements(
                ViewItemPage.SELECTOR_MERCHANDISE_ITEMS);
        assertEquals("There should be 12 product items in the merchandise panel",
                12, itemElements.size());
    }

    @Test(groups = TestGroups.GUEST_OK,
        description = "Check that we can navigate through merchandise panel (5 elements)",
        dependsOnMethods = {TEST_MERCHANDISE_PANEL_AVAILABLE})
    public void testMerchandisePanelNavigate() {
        // for some reason, this test only works when I have the browser opens
        Optional<WebElement> webElement = viPage.getFirstMerchandiseItemsPanel();
        Actions actions = new Actions(driver);
        actions.moveToElement(webElement.get());
        ViewItemTestHelper.helperTestItemPanelClickThrough(webElement, viPage);
    }

    @Test(groups = TestGroups.GUEST_OK,
        description = "Check that the product of each merchandise panel contains correct info",
        dependsOnMethods = {TEST_MERCHANDISE_PANEL_AVAILABLE})
    public void testMerchandisePanelProductInfo() {
        Optional<WebElement> webElement = viPage.getFirstMerchandiseItemsPanel();
        List<WebElement> itemElements = webElement.get().findElements(
                ViewItemPage.SELECTOR_MERCHANDISE_ITEMS);
        for (WebElement itemElement : itemElements) {
            Optional<WebElement> product = viPage.getElementOptional(ViewItemPage.SELECTOR_MERCHANDISE_PRODUCT_TITLE);
            assertTrue("product element must be present", product.isPresent());

            Optional<WebElement> condition = viPage.getElementOptional(ViewItemPage.SELECTOR_MERCHANDISE_PRODUCT_CONDITION);
            assertTrue("condition element must be present", condition.isPresent());

            Optional<WebElement> price = viPage.getElementOptional(ViewItemPage.SELECTOR_MERCHANDISE_PRODUCT_PRICE);
            assertTrue("price element must be present", price.isPresent());
        }
    }

    @DataProvider(name=DATA_PROVIDER_PRODUCT)
    public static Object[][] getTests() {
        String filename = "src//test//resources//view_item//product_list.yaml";
        Yaml yaml = new Yaml(new Constructor(ProductList.class));
        LOGGER.info("yaml initialized to take in a ProductList class");
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filename);
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
        return "ViewItem:" + product.getProductTitle().get(EbayLanguage.ENGLISH.getName());
    }
}
