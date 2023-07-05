package dev.sunbirdrc.registry.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.sunbirdrc.pojos.dto.ClaimDTO;
import dev.sunbirdrc.registry.controller.RegistryController;
import dev.sunbirdrc.registry.model.dto.BarCode;
import dev.sunbirdrc.registry.model.dto.MailDto;
import org.jetbrains.annotations.Nullable;
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


import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import static org.springframework.http.HttpStatus.*;

@Component
public class ClaimRequestClient {
    private static Logger logger = LoggerFactory.getLogger(RegistryController.class);
    private final String claimRequestUrl;
    private final RestTemplate restTemplate;
    private static final String CLAIMS_PATH = "/api/v1/claims";
    private static final String FETCH_CLAIMS_PATH = "/api/v1/getClaims";

    private static final String MAIL_SEND_URL = "/api/v1/sendMail";
    private static final String BAR_CODE_API = "/api/v1/barcode";
    private static final String PDF = ".PDF";
    private static final String GCS_CODE_API = "/api/v1/files/upload";

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

    public String saveFileToGCS(Object certificate, String entityId) {
        String fileName = entityId + PDF;
        logger.info("Credentials File Name."+fileName);
        String url = null;
        byte[] bytes = convertObtToByte(certificate);
        HttpHeaders headers = new HttpHeaders();
        if(bytes!=null){
            ByteArrayResource resource = new ByteArrayResource(bytes) {
                @Override
                public String getFilename() {
                    return fileName;
                }
            };
            ResponseEntity<String> response = uploadFileToGCS(headers, resource);
            switch (response.getStatusCode()){
                case OK:
                    url=response.getBody(); // TODO - handle http status
                    break;
                default:
                    break;

            }
        }
        logger.debug("Save to GCS successfully ..."+url);
        return url;
    }

    @Nullable
    private ResponseEntity<String> uploadFileToGCS(HttpHeaders headers, ByteArrayResource resource) {
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("accept", MediaType.MULTIPART_FORM_DATA_VALUE);
        String serviceUrl = claimRequestUrl + GCS_CODE_API;
        HttpMethod method = HttpMethod.POST;

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", resource);

        // Create the HTTP entity with headers and body
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Make the POST request to the service
        RestTemplate restTemplate = new RestTemplate();
        logger.debug("Claim Service url for GCS upload:"+ serviceUrl);
        ResponseEntity<String> response = restTemplate.postForEntity(serviceUrl, requestEntity, String.class);

        return response;
    }

    private byte[] convertObtToByte(Object certificate) {
        byte[] bytes = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            try (ObjectOutputStream objOutStream = new ObjectOutputStream(bos)) {
                objOutStream.writeObject(certificate);
                objOutStream.flush();
                bytes = bos.toByteArray();
            }
        } catch (Exception e){
            logger.error("Converting certificate file to stream failed.",e);
        }

        return bytes;
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
