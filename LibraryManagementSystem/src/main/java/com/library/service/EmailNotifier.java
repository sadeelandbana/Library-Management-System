package com.library.service;

import com.library.model.UserAccount;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailNotifier implements Observer {

    private final String senderEmail;
    private final String senderPassword;

    public EmailNotifier(String senderEmail, String senderPassword) {
        this.senderEmail = senderEmail;
        this.senderPassword = senderPassword;
    }

    @Override
    public void notify(UserAccount user, String message){
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });

            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(senderEmail));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
            msg.setSubject("Library Notification");
            msg.setText(message);
            Transport.send(msg);

            System.out.println("Email sent → " + user.getEmail());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
