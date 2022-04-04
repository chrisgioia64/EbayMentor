package tests.view_item;

import base.BaseTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.ViewItemPage;
import pages.WatchlistPage;

import java.util.Arrays;
import java.util.List;

public class ViewItemExamples extends BaseTest {

    private final static Logger LOGGER = LogManager.getLogger(ViewItemTest.class);

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

    @Test
    public void testCartPage() {
        WebDriver driver = getWebDriver();
        CartPage cartPage = new CartPage(driver);
        driver.get("https://cart.ebay.com");

        List<String> items = Arrays.asList("353384596361", "232406019645", "303835193497");
        for (String item : items) {
            LOGGER.info(item + " : " + cartPage.getQuantity(item));
        }

    }
}
