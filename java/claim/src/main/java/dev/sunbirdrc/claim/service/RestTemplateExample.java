package dev.sunbirdrc.claim.service;

import dev.sunbirdrc.claim.dto.FileDto;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RestTemplateExample {
    public static void main(String[] args) {
        // Create RestTemplate instance

            String serviceUrl = "http://localhost:8082/api/v1/files/upload";
            File fileToSend = new File("D:\\1-09152724-dd8e-4ec1-a5fe-36f4227d2451.PDF");

            try {
                // Read the file content as byte array
                byte[] fileBytes = Files.readAllBytes(Path.of(fileToSend.getAbsolutePath()));

                // Create HttpHeaders with Content-Type as multipart/form-data
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                headers.set("accept", MediaType.MULTIPART_FORM_DATA_VALUE);
                // Create the file part with the byte array
                ByteArrayResource resource = new ByteArrayResource(fileBytes) {
                    @Override
                    public String getFilename() {
                        return fileToSend.getName();
                    }
                };

                // Create the request body with the file part
                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                body.add("file", resource);

                // Create the HTTP entity with headers and body
                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

                // Make the POST request to the service
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<String> response = restTemplate.postForEntity(serviceUrl, requestEntity, String.class);

                System.out.println("Response received: " + response.getBody());
            } catch (IOException e) {
                e.printStackTrace();
            }

    }


}
