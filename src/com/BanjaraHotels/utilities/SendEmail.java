
package com.BanjaraHotels.utilities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class SendEmail {
    public static void mail(String reciever_mail, String subject, String body) {
        // Recipient's email
        String to = reciever_mail;

        // Use a dedicated email for sending
        List<String> credentials = SendEmail.getCredentials();
        String username = credentials.get(0);
        String password = credentials.get(1);
        

        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("Email sent successfully");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    private static List<String> getCredentials() {
        Properties properties = new Properties();
        FileInputStream fs;
        try {
            fs = new FileInputStream("db.properties");
            properties.load(fs);
            String email= properties.getProperty("company.email");
            String password= properties.getProperty("company.password");
            return List.of(new String[]{email, password});
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return null;
    }
}