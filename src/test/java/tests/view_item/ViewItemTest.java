package tests.view_item;

import base.BaseTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import pages.ViewItemPage;
import pages.WatchlistPage;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Driver;
import java.util.Properties;

import static org.testng.AssertJUnit.assertEquals;

public class ViewItemTest extends BaseTest {

    private final static Logger LOGGER = LogManager.getLogger(ViewItemTest.class);

    // keys for the test items in the test items file
    public static final String DATA_PROVIDER_WATCHLIST_1 = "watchlist_1";
    public static final String DATA_PROVIDER_VIEW_ITEM = "set_quantity";


    public static ViewItemPage navigateToPage(WebDriver driver, String item) {
        ViewItemPage itemPage = new ViewItemPage(driver);
        itemPage.navigateItemNumber(item);
        LOGGER.info("Performing test for product name {}", itemPage.getProductTitle());
        return itemPage;
    }

    @Test
    public void testTitle() {
        WebDriver driver = getWebDriver();
        ViewItemPage viPage = new ViewItemPage(driver);
        viPage.navigateItemNumber("303835193497");
//        viPage.navigateItemNumber("192238685975");

        LOGGER.info(viPage.getProductTitle());
        LOGGER.info(viPage.getBrandText());
        LOGGER.info(viPage.getQuantityString());
        LOGGER.info(viPage.getNumberAvailable());
        LOGGER.info(viPage.getPriceAsDouble());

        LOGGER.info("------");
        LOGGER.info(viPage.getBuyItNowText());
        WebElement element = viPage.getCartButton();
        LOGGER.info(element.getText());
        LOGGER.info(viPage.getWatchingText());

        LOGGER.info("------");
        LOGGER.info(viPage.getNumWatchers());
        LOGGER.info(viPage.isWatchlistLinkVisible());
        LOGGER.info(viPage.getSaveSellerLinkText());
        LOGGER.info(viPage.getContactSellerLinkText());
        LOGGER.info("------");
        LOGGER.info(viPage.getProductRatingsText());
        LOGGER.info(viPage.getRatingsPanelProductRatingText());
    }

    @Test
    public void testWatchlistPage() {
        WebDriver driver = getWebDriver();
        WatchlistPage watchlistPage = new WatchlistPage(driver);
        watchlistPage.navigateToPage();

        String itemNumber = "303835193497";
        boolean deleted = watchlistPage.deleteProduct(itemNumber);
        LOGGER.info("Deleted product {} --- {}", itemNumber, deleted);
    }

    private final static String TEST_ITEMS_PROPERTY_FILE
            = "src//test//resources//view_item//test_items.properties";

    private static Properties testItemProperties;

    private static void initializeProperties() {
        testItemProperties = new Properties();
        try {
            testItemProperties.load(new FileInputStream(TEST_ITEMS_PROPERTY_FILE));
        } catch (IOException e) {
            LOGGER.error("Could not load environment file: " + TEST_ITEMS_PROPERTY_FILE);
            e.printStackTrace();
        }
    }

    public static Object[][] getItems(String key) {
        if (testItemProperties == null) {
            initializeProperties();
        }
        String items = testItemProperties.getProperty(key);
        if (items == null) {
            LOGGER.warn("For test_items properties, " +
                    "could not grab the items for key {}", key);
            return null;
        }
        String[] ary = items.split(",");
        Object[][] result = new Object[ary.length][1];
        for (int i = 0; i < ary.length; i++) {
            result[i][0] = ary[i];
        }
        return result;
    }
}
