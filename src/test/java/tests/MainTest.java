package tests;

import base.BaseTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.EbayHomePage;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class MainTest extends BaseTest {

    private final static Logger LOGGER = LogManager.getLogger(MainTest.class);

    /** Search by category. */
    @Test
    public void test1() {
        WebDriver driver = BaseTest.getWebDriver();
        EbayHomePage homePage = new EbayHomePage(driver);
        homePage.clickShopByCategoryButton();
        Map<String, List<String>> categoryMap = homePage.getCategoryMap();

        for (String key : categoryMap.keySet()) {
            List<String> list = categoryMap.get(key);
            LOGGER.info("Category: " + key + " -> " + list);
            assertEquals(4, list.size());
        }
    }

    /** Verify at least one search result */
    @Test(dataProvider = "searchQuery")
    public void test2(String searchQuery) {
        WebDriver driver = getWebDriver();
        EbayHomePage homePage = new EbayHomePage(driver);
        homePage.searchQuery(searchQuery);
        int searchResults = homePage.getNumberResults();
        LOGGER.info("For search query {}, got {} search results",
                searchQuery, searchResults);
        assertTrue(searchResults > 0);
    }

    private final static List<String> SEARCH_QUERIES =
            Arrays.asList("java programming", "baseball hat");

    @DataProvider(name="searchQuery")
    public Object[][] getSearchQueries() {
        Object[][] result = new Object[SEARCH_QUERIES.size()][1];
        for (int i = 0; i < SEARCH_QUERIES.size(); i++) {
            result[i][0] = SEARCH_QUERIES.get(i);
        }
        return result;
    }


    /** Verify that exactly one of the carousel items is visible. */
    @Test
    public void test3() {
        WebDriver driver = getWebDriver();
        EbayHomePage homePage = new EbayHomePage(driver);
        for (int i = 0; i < 5; i++) {
            homePage.clickCarouselNext();
            sleepMs(1000);
        }
        homePage.verifyCarouselListItems();
    }

    @Test
    public void test4() {
        WebDriver driver = getWebDriver();
        EbayHomePage homePage = new EbayHomePage(driver);
        homePage.getRecentViews();
        for (int i = 0; i < 5; i++) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.scrollBy(0,400)", "");
            homePage.getRecentViews();
        }
    }

    @Test
    public void test5() {
        WebDriver driver = getWebDriver();
        EbayHomePage homePage = new EbayHomePage(driver);
        homePage.clickDailyDealsLink();
        assertTrue(homePage.isFeaturedCardExists());
        LOGGER.info("The featured items are: ");
        homePage.getFeaturedItems().forEach(x -> LOGGER.info(x));
    }

    public static void sleepMs(int ms) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
