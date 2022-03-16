package tests;

import api.APIUtils;
import api.RestTemplateUtils;
import base.BaseTest;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.*;

public class APITest extends BaseTest  {

    private final static Logger LOGGER = LogManager.getLogger(APITest.class);

    private RestTemplateUtils utils;

    @BeforeClass
    public void before() {
        utils = new RestTemplateUtils();
    }


    @Test
    public void testDemdex() {
        Response r = APIUtils.getEbayResponseDemdex();
        LOGGER.info(r.statusCode());
        LOGGER.info(r.asString());
    }

    @Test
    public void testRedeem() {
        ResponseEntity<String> entity = utils.getRedeemResponse();
        assertEquals(entity.getStatusCodeValue(), 204);
        assertNull(entity.getBody());
        LOGGER.info("Status code: " + entity.getStatusCodeValue());
    }

    @Test
    public void testPubConfigAds() {
        ResponseEntity<String> response = utils.getPubConfigResponse();
        LOGGER.info(response.getStatusCodeValue());
        LOGGER.info(response.getBody());
    }
}
