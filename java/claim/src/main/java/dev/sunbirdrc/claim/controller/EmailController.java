package dev.sunbirdrc.claim.controller;

import com.fasterxml.jackson.databind.JsonNode;
import dev.sunbirdrc.claim.dto.MailDto;
import dev.sunbirdrc.claim.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Controller
public class EmailController {

    @Autowired
    private EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(EmailController.class);

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @RequestMapping(value = "/api/v1/sendMail", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> sendMail(@RequestHeader HttpHeaders headers,
                                                @RequestBody MailDto requestBody) {

        String email = requestBody.getEmailAddress();
        String idLink = requestBody.getCertificate();
        String name = requestBody.getName();
        String body = prepareBody(idLink, name);
        emailService.sendMail(email, "Identity card for Student", body);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private String prepareBody(String idLink, String name) {

        String body = "Hi "+ name + ","+
                "\n" +
                " \n" +
                "\n" +
                "We are pleased to inform you that a registration credential has been issued to you. You can view and download the credential by using the following link. \n" +
                "\n" +
                "\n" +
                idLink +
                " \n" +
                "\n" +
                "Thank you, \n" +
                "\n" +
                "<Registration Credential Issuing Authority> ";
        return body;
    }


}
