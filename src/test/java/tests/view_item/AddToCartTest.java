package tests.view_item;

import api.CustomUtilities;
import base.BaseTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.CartPage;
import pages.ViewItemPage;

public class AddToCartTest extends BaseTest {

    private final static Logger LOGGER = LogManager.getLogger(AddToCartTest.class);

    @Test(dataProvider = ViewItemTest.DATA_PROVIDER_ADD_TO_CART)
    public void test(String item) {
        WebDriver driver = getWebDriver();
        ViewItemPage viPage = ViewItemTest.navigateToPage(driver, item);
        CartPage cartPage = new CartPage(driver);

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
        softAssert.assertEquals(cartPage.getQuantity(item), "1",
                "The quantity of the product on the cart page should be 1");

        softAssert.assertTrue(cartPage.deleteItem(item),
                "The product was not deleted from the cart page");
        CustomUtilities.sleep(2000);
        viPage.navigateItemNumber(item);
        cartButton = viPage.getCartButton();
        softAssert.assertTrue(cartButton.getText().contains("Add to cart"),
                "The product should have been removed from the cart");
        CustomUtilities.sleep(2000);

        softAssert.assertAll();
    }

    @DataProvider(name = ViewItemTest.DATA_PROVIDER_ADD_TO_CART)
    public Object[][] getData() {
        return ViewItemTest.getItems(ViewItemTest.DATA_PROVIDER_ADD_TO_CART);
    }

}
