package dev.sunbirdrc.registry.service.impl;


import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DigiLockerRegistryService {

    private static final Logger log = LoggerFactory.getLogger(DigiLockerRegistryService.class);

    private final Cache<String, String> cacheService = Caffeine.newBuilder()
            .expireAfterWrite(12, TimeUnit.HOURS)
            .expireAfterAccess(24, TimeUnit.HOURS)
            .build();

    public void init() {
        // Do any initialization if required
    }

    private static class TokenResponse {
        private String access_token;
        private int expires_in;

        // Add getters and setters as needed
    }

    private static class SearchResult {
        private String osid;

        // Add getters and setters as needed
    }

    private String getEntityOsid(String schema, String templateStr, Map<String, String> docDetails)
            throws IOException {
        // Implement the template rendering as needed


        return null;
    }

    public byte[] getCertificate(Object pullUriRequest) throws IOException {
        Map<String, Object> docTypeMapper = null;
        //(Map<String, Object>) config.SchemaDocTypeMapper.get(pullUriRequest.getDocDetails().get("DocType"));
        String schema = (String) docTypeMapper.get("schema");
        String schemaTemplate = (String) docTypeMapper.get("template");

        String token = getServiceAccountToken();
        log.debug("Token {}", token);

        // Implement the searchFilter serialization as needed
        String searchFilter = new Gson().toJson(docTypeMapper.get("searchFilter"));
        log.debug("SearchFilter : {}", searchFilter);

        String osid = null;
                //getEntityOsid(schema, searchFilter, pullUriRequest.getDocDetails());
        log.debug("Searched Entity OSID : {}", osid);

//        if (osid != null) {
//            String url = "config.Config.Registry.URL" + "api/v1/" + schema + "/" + osid;
//            HttpRequest request = HttpRequest.get(url)
//                    .header("Accept", "application/pdf")
//                    .header("template-key", schemaTemplate)
//                    .bearer(token);
//
//            if (request.ok()) {
//                return request.bytes();
//            }
//        }

        return null;
    }

    private String getServiceAccountToken() throws IOException {
        String cachedToken = cacheService.getIfPresent("clientSecretServiceToken");
        if (cachedToken != null) {
            log.debug("In Cache");
            return cachedToken;
        }

        log.info("Get service account token");

        String url = "config.Config.Keycloak.TokenURL";
        Map<String, String> formData = new HashMap<>();
        formData.put("grant_type", "client_credentials");
        formData.put("client_id", "config.Config.Keycloak.ClientId");
        formData.put("client_secret", "config.Config.Keycloak.ClientSecret");

//        HttpRequest request = HttpRequest.post(url)
//                .contentType("application/x-www-form-urlencoded")
//                .form(formData);
//
//        if (request.ok()) {
//            JsonObject jsonObject = JsonParser.parseString(request.body()).getAsJsonObject();
//            String accessToken = jsonObject.get("access_token").getAsString();
//            int expiresIn = jsonObject.get("expires_in").getAsInt();
//            //cacheService.put("clientSecretServiceToken", accessToken, 30, TimeUnit.MILLISECONDS);
//            return accessToken;
//        }

        return null;
    }
}


