package dev.sunbirdrc.claim.service;

import dev.sunbirdrc.claim.config.PropMapping;
import dev.sunbirdrc.claim.controller.EmailController;
import dev.sunbirdrc.claim.dto.MailDto;
import dev.sunbirdrc.claim.exception.ClaimMailException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service("emailService")
public class EmailService 
{
    private static final Logger logger = LoggerFactory.getLogger(EmailController.class);
    @Autowired
    private JavaMailSender mailSender;
     
    @Autowired
    private SimpleMailMessage preConfiguredMessage;

    @Autowired
    private PropMapping propMapping;

    @Autowired
    private Configuration freeMarkerConfiguration;

    private String generateCertificateMailContent(MailDto mailDto) {
        String processedTemplateString = null;

        Map<String, Object> mailMap = new HashMap<>();
        mailMap.put("name", mailDto.getName());
        mailMap.put("credType", mailDto.getCredentialsType());
        mailMap.put("idLink", mailDto.getCertificate());

        try {
            freeMarkerConfiguration.setClassForTemplateLoading(this.getClass(), "/templates/");
            Template template = freeMarkerConfiguration.getTemplate("credentials-mail.ftl");
            processedTemplateString = FreeMarkerTemplateUtils.processTemplateIntoString(template, mailMap);

        } catch (TemplateException e) {
            logger.error("TemplateException while creating mail template for certificate ", e);
            throw new ClaimMailException("Error while creating mail template for certificate");
        } catch (IOException e) {
            logger.error("IOException while creating mail template for certificate ", e);
            throw new ClaimMailException("Error while creating mail template for certificate");
        }
        return processedTemplateString;
    }
    /**
     * This method will send compose and send the message 
     * */
    @Async
    public void sendMail(MailDto mailDto) {
        String subject = mailDto.getCredentialsType() + " for Student";
//        String body = generateCertificateMailContent(mailDto);
//
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(mailDto.getEmailAddress());
//        message.setFrom(propMapping.getSimpleMailMessageFrom());
//        message.setSubject(subject);
//        message.setText(body);
//        mailSender.send(message);


        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setFrom(new InternetAddress(propMapping.getSimpleMailMessageFrom(),mailDto.getName()));
            mimeMessageHelper.setTo(mailDto.getEmailAddress());
            mimeMessageHelper.setText(generateCertificateMailContent(mailDto), true);

            mailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (Exception e) {
            logger.error("Exception while sending mail: ", e);
            throw new ClaimMailException("Exception while composing and sending mail with OTP");
        }

    }
 
    /**
     * This method will send a pre-configured message
     * */
    public void sendPreConfiguredMail(String message) 
    {
        SimpleMailMessage mailMessage = new SimpleMailMessage(preConfiguredMessage);
        mailMessage.setText(message);
        mailSender.send(mailMessage);
    }
    
    public void sendMailWithAttachment(String to, String subject, String body, String fileToAttach) 
    {
    	MimeMessagePreparator preparator = new MimeMessagePreparator() 
    	{
            public void prepare(MimeMessage mimeMessage) throws Exception 
            {
                mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
                mimeMessage.setFrom(new InternetAddress("admin@gmail.com"));
                mimeMessage.setSubject(subject);
                mimeMessage.setText(body);
                
                FileSystemResource file = new FileSystemResource(new File(fileToAttach));
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                helper.addAttachment("logo.jpg", file);
            }
        };
        
        try {
            mailSender.send(preparator);
        }
        catch (MailException ex) {
            // simply log it and go on...
            System.err.println(ex.getMessage());
        }
    }
    
    public void sendMailWithInlineResources(String to, String subject, String fileToAttach) 
    {
    	MimeMessagePreparator preparator = new MimeMessagePreparator() 
    	{
            public void prepare(MimeMessage mimeMessage) throws Exception 
            {
                mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
                mimeMessage.setFrom(new InternetAddress("admin@gmail.com"));
                mimeMessage.setSubject(subject);
                
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                
                helper.setText("<html><body><img src='cid:identifier1234'></body></html>", true);
                
                FileSystemResource res = new FileSystemResource(new File(fileToAttach));
                helper.addInline("identifier1234", res);
            }
        };
        
        try {
            mailSender.send(preparator);
        }
        catch (MailException ex) {
            // simply log it and go on...
            System.err.println(ex.getMessage());
        }
    }
}
