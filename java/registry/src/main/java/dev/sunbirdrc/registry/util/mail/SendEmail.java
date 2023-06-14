package dev.sunbirdrc.registry.util.mail;

import dev.sunbirdrc.registry.util.JsonKey;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.StringWriter;
import java.util.Map;

public class SendEmail {


  private static final String fromEmail = System.getenv(JsonKey.EMAIL_SERVER_FROM);

  public boolean send(
      String[] emailList,
      String subject,
      Map context,
      StringWriter writer,
      Session session,
      Transport transport) {
    boolean sentStatus = true;
    try {
      if (context != null) {
        context.put(JsonKey.FROM_EMAIL, fromEmail);
      }
      MimeMessage message = new MimeMessage(session);
      message.setFrom(new InternetAddress(fromEmail));
      Message.RecipientType recipientType = null;
      if (emailList.length > 1) {
        recipientType = Message.RecipientType.BCC;
      } else {
        recipientType = Message.RecipientType.TO;
      }
      for (String email : emailList) {
        message.addRecipient(recipientType, new InternetAddress(email));
      }
      if (recipientType == Message.RecipientType.BCC)
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(fromEmail));
      message.setSubject(subject);
      message.setContent(writer.toString(), "text/html; charset=utf-8");
      transport.sendMessage(message, message.getAllRecipients());
    } catch (Exception e) {
      sentStatus = false;

    }
    return sentStatus;
  }
}
