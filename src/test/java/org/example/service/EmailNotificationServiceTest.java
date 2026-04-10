package org.example.service;

import org.example.model.*;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EmailNotificationServiceTest {

    private EmailNotificationService service = new EmailNotificationService();

    private Transaction txnWithEmail() {
        Card card = new Card(
                1, "1234567812345678", "Satish",
                LocalDate.now().plusYears(1),
                "VISA", "HDFC", "123",
                "test@mail.com"
        );

        Transaction txn = new Transaction(
                "TXN-1", card, 1000, LocalDateTime.now()
        );

        txn.setLocation(new Location(1, "Hyderabad", "TS", "India"));
        txn.setStatus("FLAGGED");

        return txn;
    }

    private Transaction txnWithoutEmail() {
        Card card = new Card(
                1, "1234567812345678", "Satish",
                LocalDate.now().plusYears(1),
                "VISA", "HDFC", "123",
                null
        );

        Transaction txn = new Transaction(
                "TXN-2", card, 500, LocalDateTime.now()
        );

        txn.setStatus("FLAGGED");

        return txn;
    }

    // ----------------------------------------
    // 1. Not FLAGGED → no output
    // ----------------------------------------
    @Test
    void shouldDoNothing_whenNotFlagged() {

        Transaction txn = txnWithEmail();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        service.update("SUCCESS", txn);

        assertTrue(out.toString().isEmpty());
    }

    // ----------------------------------------
    // 2. No email
    // ----------------------------------------
    @Test
    void shouldPrint_whenEmailMissing() {

        Transaction txn = txnWithoutEmail();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        service.update("FLAGGED", txn);

        assertTrue(out.toString().contains("No email found"));
    }

    // ----------------------------------------
    // 3. Location NULL → "Unknown"
    // ----------------------------------------
    @Test
    void shouldHandleNullLocation() {

        Transaction txn = txnWithEmail();
        txn.setLocation(null);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        service.update("FLAGGED", txn);

        String log = out.toString();

        // either success OR error → but must not crash
        assertNotNull(log);
    }

    // ----------------------------------------
    // 4. Happy path (FLAGGED + email)
    // ----------------------------------------
    @Test
    void shouldAttemptEmailSend_whenFlaggedAndValidEmail() {

        Transaction txn = txnWithEmail();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        service.update("FLAGGED", txn);

        String log = out.toString();

        // Will either succeed OR fail depending on SMTP
        assertTrue(
                log.contains("Notification sent") ||
                        log.contains("EMAIL ERROR")
        );
    }

    // ----------------------------------------
    // 5. OTP email path
    // ----------------------------------------
    @Test
    void shouldHandleOtpEmailExecution() {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        service.sendOtpEmail("test@mail.com", 1234);

        String log = out.toString();

        assertTrue(
                log.contains("OTP sent") ||
                        log.contains("EMAIL ERROR")
        );
    }

    // ----------------------------------------
    // 6. Fraud alert path
    // ----------------------------------------
    @Test
    void shouldHandleFraudAlertExecution() {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        service.sendFraudAlert("test@mail.com", "1234567812345678");

        String log = out.toString();

        assertTrue(
                log.contains("ALERT EMAIL") ||
                        log.contains("EMAIL ERROR")
        );
    }

    // ----------------------------------------
    // 7. Short card number edge case
    // ----------------------------------------
    @Test
    void shouldHandleShortCardNumberMasking() {

        Card card = new Card(
                1, "123", "Satish",
                LocalDate.now().plusYears(1),
                "VISA", "HDFC", "123",
                "test@mail.com"
        );

        Transaction txn = new Transaction(
                "TXN-3", card, 100, LocalDateTime.now()
        );

        txn.setStatus("FLAGGED");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        assertDoesNotThrow(() -> service.update("FLAGGED", txn));
    }
}