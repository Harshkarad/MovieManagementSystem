package com.example.Service;

import java.util.Properties;
import java.util.Random;
import jakarta.mail.*;
import jakarta.mail.internet.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OTPSender {
    // Configure these with your email credentials
    @Value("${email.username}")
    private String SENDER_EMAIL;

    @Value("${email.password}")
    private String SENDER_PASSWORD;
    
    @Value("${email.host}")  // Note: Fix the typo in your properties (host vs hóst)
    private String smtpHost;
    
    @Value("${email.port}")
    private int smtpPort;

    public String sendOTP(String recipientEmail) {
        // Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);

        try {
            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Your OTP Code");
            message.setText("Your OTP is: " + otp);

            Transport.send(message);
            return otp; // Return the OTP that was sent

        } catch (MessagingException e) {
            System.err.println("Failed to send OTP: " + e.getMessage());
            return null;
        }
    }
}