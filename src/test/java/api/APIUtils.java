package api;

import io.restassured.response.Response;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static io.restassured.RestAssured.given;

/**
 * Utility methods for performing API requests
 * See the list of API methods at:
 * https://www.automationexercise.com/api_list
 */
public class APIUtils {

    public static final Logger LOGGER = LogManager.getLogger(APIUtils.class);

    public static Response getEbayResponseDemdex() {
        Response r = given().relaxedHTTPSValidation()
                .queryParam("d_visid_ver", "4.6.0")
                .queryParam("d_fieldgroup", "MC")
                .queryParam("d_rtbd", "json")
                .queryParam("d_ver", 2)
                .queryParam("d_orgid", "A71B5B5B54F607AB0A4C98A2@AdobeOrg")
                .queryParam("d_nsid", 0)
                .queryParam("ts", 164685646)
                .when()
                .get("https://dpm.demdex.net/id")
                .then()
                .extract().response();
        return r;
    }

    /**
     * API 7 / API 10
     * Returns the response object from calling verify login
     */
    private static Response getResponseVerifyLogin(String email, String password) {

        return given().relaxedHTTPSValidation()
                .contentType(ContentType.URLENC)
                .formParam("email", email)
                .formParam("password", password)
                .when()
                .post("api/verifyLogin")
                .then()
                .extract().response();
    }

    /**
     * API 1 : Get all products list
     */
    public static Response getResponseProductList() {
        return given().relaxedHTTPSValidation()
                .when()
                .get("api/productsList")
                .then()
                .extract().response();
    }

    /**
     * API 2 -- Post to all products list
     */
    public static Response postResponseProductList() {
        return given().relaxedHTTPSValidation()
                .when()
                .post("api/productsList")
                .then()
                .extract().response();
    }

    /**
     * API 3 - Get all brands list
     */
    public static Response getResponseBrandList() {
        return given().relaxedHTTPSValidation()
                .when()
                .get("api/brandsList")
                .then()
                .extract().response();
    }

    /**
     * API 4 - Put to all brands
     */
    public static Response putResponseBrandList() {
        return given().relaxedHTTPSValidation()
                .when()
                .put("api/brandsList")
                .then()
                .extract().response();
    }

    /**
     * API 5 -- POST to Search Product
     */
    public static Response postSearchProduct(String searchQuery) {
        return given().relaxedHTTPSValidation()
                .contentType(ContentType.URLENC)
                .formParam("search_product", searchQuery)
                .when()
                .post("api/searchProduct")
                .then()
                .extract().response();
    }



}
