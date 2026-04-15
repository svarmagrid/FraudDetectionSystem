package org.example.service;

import org.example.model.Transaction;
import org.example.model.Location;
import org.example.observer.Observer;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

import io.github.cdimascio.dotenv.Dotenv;

public class EmailNotificationService implements Observer {

    static Dotenv dotenv = Dotenv.load();

    private final String fromEmail = dotenv.get("EMAIL_USER");
    private final String password = dotenv.get("EMAIL_PASSWORD");

    @Override
    public void update(String status, Transaction t) {

        if (!"FLAGGED".equalsIgnoreCase(status)) return;

        String toEmail = t.getCard().getEmail();

        if (toEmail == null || toEmail.isEmpty()) {
            System.out.println("[EMAIL] No email found for this card.");
            return;
        }

        String host = "smtp.gmail.com";
        int port = 587;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", String.valueOf(port));

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(toEmail)
            );

            message.setSubject("⚠ Suspicious Transaction Alert");

            Location loc = t.getLocation();
            String locationInfo = (loc != null)
                    ? loc.getCity() + ", " + loc.getState() + ", " + loc.getCountry()
                    : "Unknown";

            String cardNumber = t.getCard().getCardNumber();
            String maskedCard;

            if (cardNumber == null || cardNumber.length() < 4) {
                maskedCard = "****";
            } else {
                maskedCard = "**** **** **** " +
                        cardNumber.substring(cardNumber.length() - 4);
            }

            String emailContent =
                    "Dear Customer,\n\n" +
                            "A suspicious transaction has been detected:\n\n" +
                            "Transaction ID: " + t.getTransactionId() + "\n" +
                            "Card Number: " + maskedCard + "\n" +
                            "Amount: ₹" + t.getAmount() + "\n" +
                            "Location: " + locationInfo + "\n" +
                            "Status: " + t.getStatus() + "\n\n" +
                            "If this was not you, please contact support immediately.\n\n" +
                            "Regards,\nFraud Detection System";

            message.setText(emailContent);

            Transport.send(message);

            System.out.println("[EMAIL] Notification sent to " + toEmail);

        } catch (MessagingException e) {
            System.out.println("[EMAIL ERROR] Failed to send email");
            e.printStackTrace();
        }
    }

    public void sendOtpEmail(String toEmail, int otp) {

        String host = "smtp.gmail.com";
        int port = 587;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", String.valueOf(port));

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(toEmail)
            );

            message.setSubject("🔐 OTP Verification");

            message.setText(
                    "Your OTP is: " + otp + "\n\n" +
                            "This OTP is valid for a short time.\n\n" +
                            "Do not share it with anyone."
            );

            Transport.send(message);

            System.out.println("[EMAIL] OTP sent to " + toEmail);

        } catch (MessagingException e) {
            System.out.println("[EMAIL ERROR] Failed to send OTP");
//            e.printStackTrace();
            System.exit(0);
        }
    }

    public void sendFraudAlert(String toEmail, String cardNumber) {

        String host = "smtp.gmail.com";
        int port = 587;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", String.valueOf(port));

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(toEmail)
            );

            message.setSubject("Suspicious Activity Detected");

            String maskedCard;

            if (cardNumber == null || cardNumber.length() < 4) {
                maskedCard = "****";
            } else {
                maskedCard = "**** **** **** " +
                        cardNumber.substring(cardNumber.length() - 4);
            }

            message.setText(
                    "Multiple failed authentication attempts detected.\n\n" +
                            "Card: " + maskedCard + "\n\n" +
                            "If this was not you, contact support immediately."
            );

            Transport.send(message);

            System.out.println("[ALERT EMAIL] Sent to " + toEmail);

        } catch (MessagingException e) {
            System.out.println("[EMAIL ERROR] Failed to send fraud alert");
//            e.printStackTrace();
        }
    }
}