package tests.view_item;

import api.CustomUtilities;
import base.BaseTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.ViewItemPage;
import pages.WatchlistPage;

import java.sql.Driver;

import static base.BaseTest.getWebDriver;

public class WatchlistTest extends BaseTest {

    public static final String DATA_PROVIDER_WATCHLIST_1 = "watchlist_1";
    private WatchlistTestTemplate template;
    private final static Logger LOGGER = LogManager.getLogger(WatchlistTest.class);

    @Factory(dataProvider = "templates")
    public WatchlistTest(WatchlistTestTemplate template) {
        this.template = template;
    }

    @DataProvider(name="templates")
    public static Object[][] getTemplates() {
        return new Object[][] {
                {new WatchlistTestTemplate.ClickButtonTemplate() },
//                {new WatchlistTestTemplate.WatchlistLinkTemplate()}
                {new WatchlistTestTemplate.WatchlistPageTemplate()}
        };
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
    @Test(dataProvider = DATA_PROVIDER_WATCHLIST_1)
    public void testWatchlist1(String itemId) {
        WebDriver driver = getWebDriver();
        ViewItemPage viPage = ViewItemTest.navigateToPage(driver, itemId);
        WatchlistPage watchlistPage = new WatchlistPage(driver);

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

    @DataProvider(name=DATA_PROVIDER_WATCHLIST_1)
    public Object[][] getData1() {
        return ViewItemTest.getItems(DATA_PROVIDER_WATCHLIST_1);
    }

}
