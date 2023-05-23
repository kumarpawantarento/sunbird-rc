//package dev.sunbirdrc.registry.service.impl;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import dev.sunbirdrc.pojos.ComponentHealthInfo;
//import dev.sunbirdrc.pojos.Request;
//import dev.sunbirdrc.pojos.RequestContext;
//import dev.sunbirdrc.pojos.Response;
//import dev.sunbirdrc.registry.middleware.util.Constants;
//import dev.sunbirdrc.registry.service.ICertificateService;
//import dev.sunbirdrc.registry.service.IEmailService;
//import dev.sunbirdrc.registry.util.JsonKey;
//import dev.sunbirdrc.registry.util.mail.SendEmail;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.lang.StringUtils;
//import org.jetbrains.annotations.NotNull;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestClientException;
//import org.springframework.web.client.RestTemplate;
//
//import java.io.StringWriter;
//import java.net.URI;
//import java.util.*;
//
//import static dev.sunbirdrc.registry.middleware.util.Constants.CONNECTION_FAILURE;
//import static dev.sunbirdrc.registry.middleware.util.Constants.SUNBIRD_CERTIFICATE_SERVICE_NAME;
//
//@Component
//public class EmailServiceImpl implements IEmailService {
//    private final String templateBaseUrl;
//    private final String certificateUrl;
//    private final String certificateHealthCheckURL;
//    private final RestTemplate restTemplate;
//
//    private boolean signatureEnabled;
//    private static Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
//
//    public EmailServiceImpl(@Value("${certificate.templateBaseUrl}") String templateBaseUrl,
//                            @Value("${certificate.apiUrl}") String certificateUrl,
//                            @Value("${signature.enabled}") boolean signatureEnabled,
//                            @Value("${certificate.healthCheckURL}") String certificateHealthCheckURL,
//                            RestTemplate restTemplate) {
//        this.templateBaseUrl = templateBaseUrl;
//        this.certificateUrl = certificateUrl;
//        this.restTemplate = restTemplate;
//        this.certificateHealthCheckURL = certificateHealthCheckURL;
//        this.signatureEnabled = signatureEnabled;
//    }
//
//
//    @Override
//    public Object sendEmail(Request emailRequest) {
//        RequestContext requestContext = emailRequest.getRequestContext();
//        Map<String, Object> request =
//                (Map<String, Object>) emailRequest.getRequest().get(JsonKey.EMAIL_REQUEST);
//        List<String> userIds =
//                (CollectionUtils.isEmpty((List<String>) request.get(JsonKey.RECIPIENT_USERIDS)))
//                        ? new ArrayList<>()
//                        : (List<String>) request.get(JsonKey.RECIPIENT_USERIDS);
//        List<String> phones =
//                (CollectionUtils.isEmpty((List<String>) request.get(JsonKey.RECIPIENT_PHONES)))
//                        ? new ArrayList<>()
//                        : (List<String>) request.get(JsonKey.RECIPIENT_PHONES);
//        List<String> emails =
//                (CollectionUtils.isEmpty((List<String>) request.get(JsonKey.RECIPIENT_EMAILS)))
//                        ? new ArrayList<>()
//                        : (List<String>) request.get(JsonKey.RECIPIENT_EMAILS);
//        String mode;
//        if (request.get(JsonKey.MODE) != null
//                && JsonKey.SMS.equalsIgnoreCase((String) request.get(JsonKey.MODE))) {
//            mode = JsonKey.SMS;
//        } else {
//            mode = JsonKey.EMAIL;
//        }
//
//        if (JsonKey.EMAIL.equalsIgnoreCase(mode)) {
//            Map<String, Object> recipientSearchQuery =
//                    (Map<String, Object>) request.get(JsonKey.RECIPIENT_SEARCH_QUERY);
//            List<String> emailList = null;
//
//
//            if (CollectionUtils.isNotEmpty(emailList)) {
//                String template = notificationService.getEmailTemplateFile( (String) request.get(JsonKey.EMAIL_TEMPLATE_TYPE), requestContext);
//                sendMail(request, emailList, template, requestContext);
//            }
//        }
//
//        Response res = new Response();
//        res.put(JsonKey.RESPONSE, JsonKey.SUCCESS);
//        //sender().tell(res, self());
//    }
//
//    private void sendMail(
//            Map<String, Object> request,
//            List<String> emails,
//            String template,
//            RequestContext requestContext) {
//        try {
//            SendEmail sendEmail = new SendEmail();
//            Map context = null;
//            StringWriter writer = new StringWriter();
//            long interval = 60000L;
//
//            if (null == connection.getTransport()
//                    || ((System.currentTimeMillis()) - timer >= interval)
//                    || (!connection.getTransport().isConnected())) {
//                resetConnection(requestContext);
//            }
//            sendEmail.send(
//                    emails.toArray(new String[emails.size()]),
//                    (String) request.get(JsonKey.SUBJECT),
//                    context,
//                    writer,
//                    connection.getSession(),
//                    connection.getTransport());
//        } catch (Exception e) {
//
////        }
//    }
//
//
//    @Override
//    public String getServiceName() {
//        return null;
//    }
//
//    @Override
//    public ComponentHealthInfo getHealthInfo() {
//        return null;
//    }
//}
