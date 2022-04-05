package tests.view_item;

import api.helpers.ViewItemService;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class ViewItemApiTest {

    ViewItemService service;

    private final static Logger LOGGER = LogManager.getLogger(ViewItemApiTest.class);

    @BeforeClass
    public void setup() {
        service = new ViewItemService();
    }

    @Test
    public void testRedeem() {
        Response response = service.getAdsResponse();
        Assert.assertEquals(response.statusCode(), HttpStatus.SC_NO_CONTENT);
    }

    @Test
    public void testAddToCart() {
        Response response = service.getAddToCartResponse();
        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);
        Assert.assertTrue(response.asString().contains("true"));
    }

}
