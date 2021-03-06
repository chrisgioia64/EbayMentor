package tests.view_item;

import base.CustomUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import pages.ViewItemPage;

import java.util.List;
import java.util.Optional;

import static org.testng.AssertJUnit.*;

public class ViewItemTestHelper {

    private final static Logger LOGGER = LogManager.getLogger(ViewItemTestHelper.class);

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
     * Click on the "forward" button repeatedly in a panel of images
     */
    public static void helperTestItemPanelClickThrough(Optional<WebElement> merchandiseElement,
                                                 ViewItemPage viPage) {
        Optional<WebElement> forwardButton = viPage.getForwardButton(
                merchandiseElement.get());
        assertTrue("Forward button should be present", forwardButton.isPresent());
        List<WebElement> items = viPage.getProductItemsFromImagePanel(
                merchandiseElement.get());

        for (int i = 0; i < 3; i++) {
            long numDisplayed = viPage.getNumberItemsVisibleInCarousel(items);
            CustomUtilities.sleep(2000);
            assertEquals("Number of displayed must be 5", 5, numDisplayed);
            if (forwardButton.get().isEnabled()) {
                forwardButton.get().click();
            } else {
                break;
            }
        }
    }

    /**
     * There are two quantity textboxes. One at the top of the page, and one in
     * "Shipping" tab. This is a generic method that performs a test case given
     * the selectors of the textbox and error message box.
     */
    public static void setQuantityTestCase(ViewItemPage viPage,
                                    By textboxSelector, By errorBoxSelector) {
        int numAvailable = viPage.getNumberAvailable();
        Optional<WebElement> textbox = viPage.getElementOptional(textboxSelector);
        Optional<WebElement> errorBox = viPage.getElementOptional(errorBoxSelector);
        if (textbox.isPresent() && numAvailable >= 1) {
            assertTrue("Error box should be present", errorBox.isPresent());
            enterAndAssert(textbox.get(), errorBox.get(), "1", false);
            enterAndAssert(textbox.get(), errorBox.get(), "0", true);
            enterAndAssert(textbox.get(), errorBox.get(), numAvailable + "", false);
            enterAndAssert(textbox.get(), errorBox.get(), (numAvailable + 1) + "", true);
        }
    }

    private static void enterAndAssert(WebElement textboxElement, WebElement errorBox,
                                String input, boolean errorDisplay) {
        textboxElement.clear();
        textboxElement.sendKeys(input);
        CustomUtilities.sleep(1000);
        if (errorDisplay) {
            assertTrue("Error box should be present when entering " + input,
                    errorBox.isDisplayed());
        } else {
            assertFalse("Error box should not be present when entering " + input,
                    errorBox.isDisplayed());
        }
    }

    /**
     * There are two quantity textboxes. One at the top of the page, and one in
     * "Shipping" tab. This is a generic method that enters in input that should
     * result in an error.
     */
    public static void setQuantityNegativeTestCases(ViewItemPage viPage,
                                                    By textboxSelector, By errorBoxSelector,
                                                    List<String> negativeInputs) {
        int numAvailable = viPage.getNumberAvailable();
        Optional<WebElement> textbox = viPage.getElementOptional(textboxSelector);
        Optional<WebElement> errorBox = viPage.getElementOptional(errorBoxSelector);
        if (textbox.isPresent() && numAvailable >= 1) {
            assertTrue("Error box should be present", errorBox.isPresent());
            for (String negativeInput : negativeInputs) {
                enterAndAssert(textbox.get(), errorBox.get(), negativeInput, true);
            }
        }
    }


    public static boolean isNavigationButtonDisabled(WebElement element) {
        return element.getAttribute("class").contains("disabled");
    }

    public static void assertNthThumbnail(List<WebElement> elements, int index) {
        if (index < elements.size()) {
            WebElement element = elements.get(index);
            assertTrue("The element at index " + index + " should be selected",
                    element.getAttribute("class").contains("selected"));
        }
    }


}
