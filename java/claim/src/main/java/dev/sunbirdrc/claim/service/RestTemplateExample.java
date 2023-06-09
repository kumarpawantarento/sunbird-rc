package dev.sunbirdrc.claim.service;

import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RestTemplateExample {
    public static void main(String[] args) {
        // Create RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Add String message converter
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        String requestBody = "{\n" +
                "    \"request\": {\n" +
                "        \"notifications\": [\n" +
                "            {\n" +
                "                \"ids\": [\n" +
                "                    \"alt.n2-7oawvyxu@yopmail.com\"\n" +
                "                ],\n" +
                "                \"priority\": 1,\n" +
                "                \"type\": \"email\",\n" +
                "                \"action\": {\n" +
                "                    \"type\": \"member-added\",\n" +
                "                    \"category\": \"member-added\",\n" +
                "                    \"template\": {\n" +
                "                        \"config\": {\n" +
                "                            \"sender\": \"shishir.suman@tarento.com\",\n" +
                "                            \"subject\": \"Hello to sunbird user\"\n" +
                "                        },\n" +
                "                        \"type\": \"PDF\",\n" +
                "                        \"params\": {\n" +
                "                            \"orgImageUrl\": \"UPSMF\",\n" +
                "                            \"name\": \"UPSMF\",\n" +
                "                            \"actionUrl\": \"http://localhost:9000/health\",\n" +
                "                            \"orgName\": \"UPSMF\",\n" +
                "                            \"ActionName\": \"UPSMF\",\n" +
                "                            \"param1\" : \"Credentials\",\n" +
                "                            \"param2\" : \"LINK\"\n" +
                "                        }\n" +
                "                    },\n" +
                "                    \"createdBy\": {\n" +
                "                        \"id\": \"12345\",\n" +
                "                        \"name\": \"John\",\n" +
                "                        \"type\": \"User\"\n" +
                "                    },\n" +
                "                     \"additionalInfo\": {\n" +
                "                        \"sender\": \"shishir.suman@tarento.com\",\n" +
                "                        \"subject\": \"Please find your Credentials\"\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";
        // Set request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("accept", "application/json");
        headers.set("notification-delivery-mode", "sync");
        headers.set("ts",getTime());
        headers.set("X-msgid", getTime());

        // Set request URL
        String url = "http://localhost:9000/v2/notification/send";

        // Set request method
        HttpMethod method = HttpMethod.POST;

        // Create HttpEntity with headers
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        // Send the HTTP request and get the response
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        // Get the response body
        String responseBody = responseEntity.getBody();

        // Process the response body as needed
        System.out.println(responseBody);
    }

    public static String getTime() {
        // Get the current timestamp
        LocalDateTime now = LocalDateTime.now();

        // Format the timestamp using a specific pattern
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTimestamp = now.format(formatter);

        return formattedTimestamp;
    }
}
