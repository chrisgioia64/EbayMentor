package tests;

import api.APIUtils;
import base.BaseTest;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

public class APITest extends BaseTest  {

    private final static Logger LOGGER = LogManager.getLogger(APITest.class);


    @Test
    public void testDemdex() {
        Response r = APIUtils.getEbayResponseDemdex();
        LOGGER.info(r.statusCode());
        LOGGER.info(r.asString());
    }
}
