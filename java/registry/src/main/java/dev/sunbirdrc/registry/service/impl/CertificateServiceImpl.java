package dev.sunbirdrc.registry.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import dev.sunbirdrc.pojos.ComponentHealthInfo;
import dev.sunbirdrc.registry.middleware.util.Constants;
import dev.sunbirdrc.registry.model.dto.BarCode;
import dev.sunbirdrc.registry.model.dto.MailDto;
import dev.sunbirdrc.registry.service.ICertificateService;
import dev.sunbirdrc.registry.util.ClaimRequestClient;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static dev.sunbirdrc.registry.middleware.util.Constants.CONNECTION_FAILURE;
import static dev.sunbirdrc.registry.middleware.util.Constants.SUNBIRD_CERTIFICATE_SERVICE_NAME;

@Component
public class CertificateServiceImpl implements ICertificateService {
    private final String templateBaseUrl;
    private final String certificateUrl;
    private final String certificateHealthCheckURL;
    private final RestTemplate restTemplate;

    private final ClaimRequestClient claimRequestClient;

    private boolean signatureEnabled;
    private static Logger logger = LoggerFactory.getLogger(CertificateServiceImpl.class);

    public CertificateServiceImpl(@Value("${certificate.templateBaseUrl}") String templateBaseUrl,
                                  @Value("${certificate.apiUrl}") String certificateUrl,
                                  @Value("${signature.enabled}") boolean signatureEnabled,
                                  @Value("${certificate.healthCheckURL}") String certificateHealthCheckURL,
                                  RestTemplate restTemplate,ClaimRequestClient claimRequestClient) {
        this.templateBaseUrl = templateBaseUrl;
        this.certificateUrl = certificateUrl;
        this.restTemplate = restTemplate;
        this.certificateHealthCheckURL = certificateHealthCheckURL;
        this.signatureEnabled = signatureEnabled;
        this.claimRequestClient = claimRequestClient;
    }

    @Override
    public Object getCertificate(JsonNode certificateData, String entityName, String entityId, String mediaType, String templateUrl, JsonNode entity) {
        try {
            String finalTemplateUrl = inferTemplateUrl(entityName, mediaType, templateUrl);
            logger.info("Org LOGO"+String.valueOf(entity.get("orgLogo")));
            ObjectNode objNode = (ObjectNode)certificateData.get("credentialSubject");


            if(objNode != null) {
                TextNode node = (TextNode)objNode.get("compositeData");
                ObjectMapper obj = new ObjectMapper();
                String value1 = node.toString();
                Document document = Jsoup.parse(value1);
                //objNode.put("compositeData",document.toString());
                //logger.info(document.toString());
                //JsonNode n1 = JsonNodeFactory.instance();
            }
            Map<String, Object> requestBody = new HashMap<String, Object>(){{
                put("templateUrl", finalTemplateUrl);
                String value = certificateData.toString();
                put("certificate", value);
                put("entityId", entityId);
                put("entityName", entityName);
                put("entity", entity);
            }};
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", mediaType);
            HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, headers);
            return restTemplate.postForObject(certificateUrl, httpEntity, byte[].class);
        } catch (Exception e) {
            logger.error("Get certificate failed", e);
        }
        return null;
    }


    public void shareCertificateMail(MailDto mail) {
        try {
            logger.info("Sharing Certificate start");
            claimRequestClient.sendMail(mail);
            logger.info("Sharing Certificate end");
        } catch (Exception e) {
            logger.error("Get certificate failed", e);
        }

    }

    public String saveToGCS(Object certificate, String entityId) {
        String url = null;
        logger.info("Uploading File GCP.");
        url = claimRequestClient.saveFileToGCS(certificate, entityId);
        logger.info("Uploading File GCP complete");
        return url;
    }

    public BarCode getBarCode(BarCode barCode) {
        BarCode node = null;
        try {
            logger.debug("BarCode start");
            node = claimRequestClient.getBarCode(barCode);
            logger.debug("BarCode end");
            return node;
        } catch (Exception e) {
            logger.error("Get BarCode failed", e);
        }
        return node;
    }

    @NotNull
    private String inferTemplateUrl(String entityName, String mediaType, String templateUrl) {
        if (templateUrl == null) {
            templateUrl = templateBaseUrl + entityName + getFileExtension(mediaType);
        }
        return templateUrl;
    }

    @NotNull
    private String getFileExtension(String mediaType) {
        if (Constants.SVG_MEDIA_TYPE.equals(mediaType)) {
            return ".svg";
        }
        return ".html";
    }

    @Override
    public String getServiceName() {
        return SUNBIRD_CERTIFICATE_SERVICE_NAME;
    }

    @Override
    public ComponentHealthInfo getHealthInfo() {
        if (signatureEnabled) {
            try {
                ResponseEntity<String> response = restTemplate.getForEntity(URI.create(certificateHealthCheckURL), String.class);
                if (!StringUtils.isEmpty(response.getBody()) && Arrays.asList("UP", "OK").contains(response.getBody().toUpperCase())) {
                    logger.debug("Certificate service running !");
                    return new ComponentHealthInfo(getServiceName(), true);
                } else {
                    return new ComponentHealthInfo(getServiceName(), false);
                }
            } catch (RestClientException ex) {
                logger.error("RestClientException when checking the health of the Sunbird certificate service: ", ex);
                return new ComponentHealthInfo(getServiceName(), false, CONNECTION_FAILURE, ex.getMessage());
            }
        } else {
            return new ComponentHealthInfo(getServiceName(), true, "SIGNATURE_ENABLED", "false");
        }

    }
}
