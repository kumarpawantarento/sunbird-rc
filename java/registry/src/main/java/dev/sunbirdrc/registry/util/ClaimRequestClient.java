package dev.sunbirdrc.registry.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.sunbirdrc.pojos.dto.ClaimDTO;
import dev.sunbirdrc.registry.controller.RegistryController;
import dev.sunbirdrc.registry.model.dto.BarCode;
import dev.sunbirdrc.registry.model.dto.MailDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


import java.util.HashMap;

@Component
public class ClaimRequestClient {
    private static Logger logger = LoggerFactory.getLogger(RegistryController.class);
    private final String claimRequestUrl;
    private final RestTemplate restTemplate;
    private static final String CLAIMS_PATH = "/api/v1/claims";
    private static final String FETCH_CLAIMS_PATH = "/api/v1/getClaims";

    private static final String MAIL_SEND_URL = "api/v1/sendMail";
    private static final String BAR_CODE_API = "api/v1/barcode";

    private static final String GCS_CODE_API = "api/v1/files/upload";

    ClaimRequestClient(@Value("${claims.url}") String claimRequestUrl, RestTemplate restTemplate) {
        this.claimRequestUrl = claimRequestUrl;
        this.restTemplate = restTemplate;
    }

    public HashMap<String, Object> riseClaimRequest(ClaimDTO claimDTO) {
        HashMap<String, Object> hashMap = restTemplate.postForObject(claimRequestUrl + CLAIMS_PATH, claimDTO, HashMap.class);
        logger.info("Claim has successfully risen {}", hashMap.toString());
        return hashMap;
    }

    public void sendMail(MailDto mail) {
        restTemplate.postForObject(claimRequestUrl + MAIL_SEND_URL, mail, HashMap.class);
        logger.info("Mail has successfully sent ...");
    }

    public String saveFileToGCS(byte[] file, String name) {
        HttpHeaders headers = new HttpHeaders();
        //headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("accept", MediaType.MULTIPART_FORM_DATA_VALUE);
        String serviceUrl = claimRequestUrl + "/"+GCS_CODE_API;
        HttpMethod method = HttpMethod.POST;
        ByteArrayResource resource = new ByteArrayResource(file) {
            @Override
            public String getFilename() {
                return name;
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", resource);

        // Create the HTTP entity with headers and body
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Make the POST request to the service
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(serviceUrl, requestEntity, String.class);
        String url = response.getBody();
        logger.info("Save to GCS successfully ..."+url);
        return url;
    }

    public BarCode getBarCode(BarCode barCode) {
        logger.info("in Client::"+barCode.getBarCodeText());
        BarCode node = restTemplate.postForObject(claimRequestUrl + BAR_CODE_API, barCode, BarCode.class);
        logger.info("BarCode generated ...");
        return node;
    }

    public JsonNode getClaims(JsonNode jsonNode, Pageable pageable, String entityName) {
        final String QUERY_PARAMS = "?size=" + pageable.getPageSize() + "&page="+pageable.getPageNumber();
        ObjectNode requestBody = JsonNodeFactory.instance.objectNode();
        requestBody.set("attestorInfo", jsonNode);
        requestBody.put("entity", entityName);
        return restTemplate.postForObject(claimRequestUrl + FETCH_CLAIMS_PATH + QUERY_PARAMS, requestBody, JsonNode.class);
    }

    public JsonNode getClaim(JsonNode jsonNode, String entityName, String claimId) {
        ObjectNode requestBody = JsonNodeFactory.instance.objectNode();
        requestBody.set("attestorInfo", jsonNode);
        requestBody.put("entity", entityName);
        return restTemplate.postForObject(claimRequestUrl + FETCH_CLAIMS_PATH + "/" + claimId, requestBody, JsonNode.class);
    }

    public ResponseEntity<Object> attestClaim(JsonNode attestationRequest, String claimId) {
        return restTemplate.exchange(
                claimRequestUrl + CLAIMS_PATH + "/" + claimId,
                HttpMethod.POST,
                new HttpEntity<>(attestationRequest),
                Object.class
        );
    }
}
