package entities;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.util.ByteArrayDataSource;
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
                return new PasswordAuthentication("losandesclubrosario@gmail.com", "oqmshcipoykpaqal");
            }
        });

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("losandesclubrosario@gmail.com"));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
        msg.setSubject(asunto);
        msg.setContent(cuerpo, "text/html; charset=utf-8");

        Transport.send(msg);
    }

    public static void enviarCorreoConAdjunto(String destinatario, String asunto, String cuerpoHtml, byte[] archivoPdf, String nombreArchivo) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("losandesclubrosario@gmail.com", "oqmshcipoykpaqal");
            }
        });

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("losandesclubrosario@gmail.com"));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
        msg.setSubject(asunto);


        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(cuerpoHtml, "text/html; charset=utf-8");

        MimeBodyPart attachmentBodyPart = new MimeBodyPart();
        DataSource source = new ByteArrayDataSource(archivoPdf, "application/pdf");
        attachmentBodyPart.setDataHandler(new DataHandler(source));
        attachmentBodyPart.setFileName(nombreArchivo);

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        multipart.addBodyPart(attachmentBodyPart);

        msg.setContent(multipart);

        Transport.send(msg);
    }
    
}