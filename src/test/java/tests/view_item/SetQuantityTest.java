package tests.view_item;

import api.CustomUtilities;
import base.BaseTest;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.ViewItemPage;
import pages.WatchlistPage;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

public class SetQuantityTest extends BaseTest {

    @Test(dataProvider = ViewItemTest.DATA_PROVIDER_VIEW_ITEM)
    public void test(String itemId) {
        WebDriver driver = getWebDriver();
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

    @DataProvider(name=ViewItemTest.DATA_PROVIDER_VIEW_ITEM)
    public Object[][] getData() {
        return ViewItemTest.getItems(ViewItemTest.DATA_PROVIDER_VIEW_ITEM);
    }
}
