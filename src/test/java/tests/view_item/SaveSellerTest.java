package tests.view_item;

import api.CustomUtilities;
import base.BaseTest;
import org.openqa.selenium.WebDriver;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.SellerPage;
import pages.ViewItemPage;

public class SaveSellerTest extends BaseTest {

    private final static String SELLER_SAVED_TEXT = "Saved";
    private final static String SELLER_NOT_SAVED_TEXT = "Save this seller";

    @Test(dataProvider = ViewItemTest.DATA_PROVIDER_SAVE_SELLER)
    public void test(String itemId) {
        WebDriver driver = getWebDriver();
        ViewItemPage viPage = ViewItemTest.navigateToPage(driver, itemId);
        SellerPage sellerPage = new SellerPage(driver);

        // Check prerequisite
        if (!viPage.getSaveSellerLinkText().equalsIgnoreCase(SELLER_NOT_SAVED_TEXT)) {
            throw new SkipException("Expecting the seller not be saved");
        }

        SoftAssert softAssert = new SoftAssert();

        viPage.clickSaveSellerLink();
        CustomUtilities.sleep(2000);
        viPage.clickSellerLink();
        softAssert.assertEquals(sellerPage.getSaveElement().getText(), SELLER_SAVED_TEXT);
        viPage.navigateItemNumber(itemId);
        softAssert.assertEquals(viPage.getSaveSellerLinkText(), SELLER_SAVED_TEXT);

        viPage.clickSaveSellerLink();
        CustomUtilities.sleep(2000);
        softAssert.assertEquals(viPage.getSaveSellerLinkText(), SELLER_NOT_SAVED_TEXT);
        softAssert.assertAll();
    }

    @DataProvider(name = ViewItemTest.DATA_PROVIDER_SAVE_SELLER)
    public Object[][] getData() {
        return ViewItemTest.getItems(ViewItemTest.DATA_PROVIDER_SAVE_SELLER);
    }

}
