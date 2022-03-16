package api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class RestTemplateUtils {

    private final static Logger LOGGER = LogManager.getLogger(RestTemplateUtils.class);
    private final RestTemplate restTemplate;
    private ResponseEntity<String> responseEntity;

    public RestTemplateUtils() {
        restTemplate = new RestTemplate();
    }

    public ResponseEntity<String> getRedeemResponse() {
        String url = "https://www.ebay.com/nap/napkinapi/v1/ticketing/redeem";
        String queryParameters = "?ticket=a7ad8ab4ecd843b7832383fd59ac7077";

        return restTemplate.getForEntity(url + queryParameters, String.class);
    }

    public ResponseEntity<String> getPubConfigResponse() {
        String url = "https://securepubads.g.doubleclick.net/pagead/ppub_config?ippd=www.ebay.com";
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }

}
