package org.example.observer;

import org.example.model.Card;
import org.example.model.Transaction;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FraudAlertServiceTest {

    private Transaction createTransaction() {

        Card card = new Card(
                1,
                "1234567812345678",
                "Satish",
                LocalDate.now().plusYears(1),
                "VISA",
                "HDFC",
                "123",
                "test@mail.com"
        );

        return new Transaction(
                "TXN-1",
                card,
                9999,
                LocalDateTime.now()
        );
    }

    // Case 1: FLAGGED → should print alert
    @Test
    void shouldPrintAlert_whenTransactionIsFlagged() {

        FraudAlertService service = new FraudAlertService();
        Transaction txn = createTransaction();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(output));

        service.update("FLAGGED", txn);

        System.setOut(original);

        String log = output.toString();

        assertTrue(log.contains("[ALERT]"));
        assertTrue(log.contains("1234567812345678"));
        assertTrue(log.contains("9999"));
    }

    // Case 2: lowercase flagged → should still trigger
    @Test
    void shouldPrintAlert_caseInsensitive() {

        FraudAlertService service = new FraudAlertService();
        Transaction txn = createTransaction();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(output));

        service.update("flagged", txn);

        System.setOut(original);

        String log = output.toString();

        assertTrue(log.contains("[ALERT]"));
    }

    // Case 3: NOT flagged → no output
    @Test
    void shouldNotPrintAlert_whenStatusIsNotFlagged() {

        FraudAlertService service = new FraudAlertService();
        Transaction txn = createTransaction();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(output));

        service.update("SUCCESS", txn);

        System.setOut(original);

        String log = output.toString();

        assertTrue(log.isEmpty());
    }
}