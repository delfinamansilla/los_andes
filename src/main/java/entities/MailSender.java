package entities;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class MailSender {
    public static void enviarCorreo(String destinatario, String asunto, String cuerpo) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("losandesclubrosario@gmail.com", "oqms hcip oykp aqal");
            }
        });

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("losandesclubrosario@gmail.com"));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
        msg.setSubject(asunto);
        msg.setContent(cuerpo, "text/html; charset=utf-8");

        Transport.send(msg);
    }
}