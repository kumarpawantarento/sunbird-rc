package dev.sunbirdrc.registry.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.naming.Context;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class DigiLockerService {

    private static final Logger log = LoggerFactory.getLogger(DigiLockerService.class);

    private DigiLockerService registryService = new DigiLockerService();

    public void init() {
        registryService.init();
    }

    public Object processURIRequest(Context context) {
        byte[] rawData = null;
        try {
            byte[] actualHMAC = generateHMAC(rawData);
            byte[] expectedHMAC = getHMACFromRequest(context);

            boolean validRequest = validateHMAC(actualHMAC, expectedHMAC);
            if (validRequest) {
               // PullURIRequest pullUriRequest = new PullURIRequest();
                // Implement XML unmarshalling here using a library like JAXB

                // Call registry service to get the certificate and other details
                // byte[] certificate = registryService.getCertificate(pullUriRequest);
                // String certificate_id = ...;

                // PullURIResponse pullURIResponse = generatePullURIResponse(pullUriRequest, certificate, certificate_id);
                // return pullURIResponse;
                return null; // Placeholder for return value
            } else {
                return null; // Return some error response here for invalid/unauthorized access
            }
        } catch (Exception e) {
            log.error("Error processing URI request: ", e);
            return null; // Return some error response here for any other errors
        }
    }

    private boolean validateHMAC(byte[] actualHMAC, byte[] expectedHMAC) {
        // Implement HMAC validation here
        // Return true if the HMACs match, false otherwise
        return true; // Placeholder for implementation
    }

    private byte[] generateHMAC(byte[] rawData) throws Exception {
        String key = "config.Config.Digilocker.AuthHMACKey";
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256Hmac.init(secretKey);
        byte[] hmacData = sha256Hmac.doFinal(rawData);
        return Base64.getEncoder().encode(hmacData);
    }

    private byte[] getHMACFromRequest(Context context) throws Exception {
        String hmacDigest = null;
                //context.header(config.Config.Digilocker.AuthKeyName);
        return Base64.getDecoder().decode(hmacDigest);
    }


}
