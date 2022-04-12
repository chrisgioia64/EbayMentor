package api.helpers;

import api.constants.Endpoints;
import base.EnvironmentProperties;
import base.locale.LocaleProperties;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * Functions that perform GET/POST requests to retrieve
 * API responses from endpoints.
 */
public class ViewItemService {

    private final static String URL = EnvironmentProperties.getInstance().getLocaleProperties()
            .getProperty(LocaleProperties.KEY_URL);

    private Map<String, Object> baseHeaders;

    public ViewItemService() {
        RestAssured.baseURI = URL;
        RestAssured.useRelaxedHTTPSValidation();
        baseHeaders = new HashMap<>();
        baseHeaders.put("Accept", "*/*");
        baseHeaders.put("Accept-Encoding", "gzip, deflate, br");
        baseHeaders.put("Accept-Language", "en-US,en;q=0.9");
        baseHeaders.put("Connection", "keep-alive");
        baseHeaders.put("Content-Type", "application/json; charset=utf-8");
        baseHeaders.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.82 Safari/537.36");
    }

    public Response GET(Map<String, String> queryParameters, String endpoint) {
        Response response = RestAssured.given()
                .headers(baseHeaders)
                .queryParams(queryParameters)
                .when()
                .get(endpoint)
                .then()
                .extract().response();
        return response;
    }

    public Response getAdsResponse() {
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put("ticket", "aeaa7fe077904cfa9f689269d8ec9254");
        return GET(queryParameters, Endpoints.REDEEM_TICKET);
    }

    public Response getAddToCartResponse() {
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put("item", "303835193497");
        queryParameters.put("quantity", "1");
        return GET(queryParameters, Endpoints.ADD_TO_CART);
    }

}
