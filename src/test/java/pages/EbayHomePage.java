package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class EbayHomePage extends EbayPage {

    public static final String URL = "";

    private final static Logger LOGGER = LogManager.getLogger(EbayHomePage.class);

    // Problem 1
    private final static String SHOP_BY_CATEGORY_BUTTON = "#gh-shop-a";
    private final static String CATEGORY_TABLE = "#gh-sbc";
    private final static String CATEGORY_HEADING = "h3.gh-sbc-parent";

    // Problem 2
    private final static String SEARCH_BAR = "#gh-ac";
    private final static String SEARCH_BUTTON = "#gh-btn";

    private final static String COUNT_DIV = ".srp-controls__count";
    private final static String SPAN_BOLD = ".BOLD";

    // Problem 3
    private final static String BANNER_DIV_ID = "#rtm_list1";

    private final static String CAROUSEL_PLAYBACK = "button[class='carousel__playback']";
    private final static String CAROUSEL_NEXT = "button.carousel__control--next";
    private final static String CAROUSEL_LIST = ".carousel__list";
    private final static String CAROUSEL_LIST_ITEMS = "li";

    // Problem 4
    private final static String CAROUSEL_CONTAINER = ".hl-standard-carousel__container";

    // Problem 5
    private final static String DAILY_DEALS_LINK = "#gh-p-1 a";
    private final static String FEATURED_CARD = ".ebayui-dne-item-featured-card";
    private final static String FEATURED_ITEM_TEXT = ".ebayui-dne-item-featured-card .col h3 span span";


    private final static String LINK_TEXT_ELECTRONICS = "Electronics";

    public EbayHomePage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void navigateToPage() {
        navigateToSuburl(URL);
    }


    public void clickCarouselNext() {
        click(CAROUSEL_NEXT);
    }

    public void clickDailyDealsLink() {
        click(DAILY_DEALS_LINK);
    }

    public List<String> getRecentViews() {
        List<WebElement> elements = driver.findElements(By.cssSelector(CAROUSEL_CONTAINER));
        LOGGER.info("There are {} elements", elements.size());
        if (elements.size() >= 3) {
            WebElement item = elements.get(2);
            return null;
        }
        return null;
    }

    public List<String> getFeaturedItems() {
        return driver.findElements(By.cssSelector(FEATURED_ITEM_TEXT))
                .stream().map(x -> x.getText()).collect(Collectors.toList());
    }

    public boolean isFeaturedCardExists() {
        return elementExists(FEATURED_CARD);
    }

    /**
     * Verify that out of all carousel items, only one is not hidden
     */
    public void verifyCarouselListItems() {
        WebElement carouselList = driver.findElement(By.cssSelector(CAROUSEL_LIST));
        List<WebElement> items = carouselList.findElements(By.cssSelector(CAROUSEL_LIST_ITEMS));
        int numVisible = 0;
        for (WebElement item : items) {
            String value = item.getAttribute("aria-hidden");
            LOGGER.info("Carousel Item is hidden {}", value);
            if (value == null) {
                numVisible++;
            }
        }
        assertEquals(1, numVisible);
    }

    public void clickShopByCategoryButton() {
        click(SHOP_BY_CATEGORY_BUTTON);
    }

    public int getNumberResults() {
        String text = "";
        try {
            WebElement element = driver.findElement(By.cssSelector(COUNT_DIV));
            WebElement innerElement = element.findElement(By.cssSelector(SPAN_BOLD));
            text = innerElement.getText();
            return Integer.parseInt(text.replace(",",""));
        } catch (NoSuchElementException ex) {
            LOGGER.warn("Element not found for getNumberResults");
            LOGGER.warn(ex.getMessage());
            return -1;
        } catch (NumberFormatException ex) {
            LOGGER.warn("Could not parse integer from {}", text);
            return -1;
        }
    }

    public Map<String, List<String>> getCategoryMap() {
        Map<String, List<String>> map = new HashMap<>();
        WebElement element = driver.findElement(By.cssSelector(CATEGORY_TABLE));
        List<WebElement> tdElements = element.findElements(By.cssSelector("td"));
        for (int i = 0; i < tdElements.size(); i++) {
            WebElement tdElement = tdElements.get(i);
            List<WebElement> headings = tdElement.findElements(By.cssSelector(CATEGORY_HEADING));
            List<WebElement> listItems = tdElement.findElements(By.cssSelector("li"));
            for (WebElement heading : headings) {
                System.out.println(heading.getText());
            }
            System.out.println("----------");
            if (i <= 1) {
                assertEquals(headings.size() * 4, listItems.size());
            } else if (i == 2) {
                assertEquals(headings.size() / 2 * 4, listItems.size());
            }

            for (int j = 0; j < headings.size(); j++) {
                String headingText = headings.get(j).getText();
                List<String> list = new LinkedList<>();
                for (int k = j * 4; k < j * 4 + 4; k++) {
                    if (k < listItems.size()) {
                        WebElement listElement = listItems.get(k);
                        list.add(listElement.getText());
                    }
                }
                if (list.size() > 0) {
                    map.put(headingText, list);
                }
            }
        }
        return map;
    }

    public void searchQuery(String searchQuery) {
        sendKeys(SEARCH_BAR, searchQuery);
        click(SEARCH_BUTTON);
    }

    public WebElement getElectronicsLink() {
        List<WebElement> elements = driver.findElements(By.linkText(LINK_TEXT_ELECTRONICS));
        if (elements.size() != 1) {
            LOGGER.warn("Found {} electronics link", elements.size());
            return null;
        }
        LOGGER.info("Using electronics link with text {}", elements.get(0).getText());
        return elements.get(0);
    }


}
