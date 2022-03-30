package tests.view_item;

import api.CustomUtilities;
import base.BaseTest;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.ViewItemPage;

import static base.BaseTest.getWebDriver;

public class WatchlistTest extends BaseTest {

    public static final String DATA_PROVIDER_WATCHLIST_1 = "watchlist_1";

    @Test(dataProvider = DATA_PROVIDER_WATCHLIST_1)
    public void testWatchlist1(String itemId) {
        WebDriver driver = getWebDriver();
        ViewItemPage viPage = ViewItemTest.navigateToPage(driver, itemId);

        if (!viPage.getWatchingText().equals("Add to Watchlist")) {
            throw new SkipException("Prerequisite for test not fulfilled");
        }

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,200)", "");

        viPage.clickWatchButton();
        CustomUtilities.sleep(2000);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(viPage.getWatchingText(), "Watching");
        viPage.clickWatchButton();
        viPage.navigateItemNumber(itemId); // reload the page
        js.executeScript("window.scrollBy(0,200)", "");
        softAssert.assertEquals(viPage.getWatchingText(), "Add to Watchlist");
        softAssert.assertAll();
        CustomUtilities.sleep(2000);

    }

    @DataProvider(name=DATA_PROVIDER_WATCHLIST_1)
    public Object[][] getData1() {
        return ViewItemTest.getItems(DATA_PROVIDER_WATCHLIST_1);
    }

}
