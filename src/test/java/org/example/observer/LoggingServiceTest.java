package org.example.observer;

import org.example.model.*;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

class LoggingServiceTest {

    private Transaction createTransactionWithLocation() {

        Card card = new Card(
                1,
                "1234567812345678",
                "Satish",
                YearMonth.now().plusYears(1),
                "VISA",
                "HDFC",
                "123",
                "test@mail.com"
        );

        Transaction txn = new Transaction(
                "TXN-1",
                card,
                1000,
                LocalDateTime.now()
        );

        txn.setLocation(new Location(1, "Hyderabad", "Telangana", "India"));

        return txn;
    }

    private Transaction createTransactionWithoutLocation() {

        Card card = new Card(
                1,
                "1234567812345678",
                "Satish",
                YearMonth.now().plusYears(1),
                "VISA",
                "HDFC",
                "123",
                "test@mail.com"
        );

        return new Transaction(
                "TXN-2",
                card,
                500,
                LocalDateTime.now()
        );
    }

    // Case 1: Location present
    @Test
    void shouldLogTransaction_withLocation() {

        LoggingService service = new LoggingService();
        Transaction txn = createTransactionWithLocation();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(output));

        service.update("SUCCESS", txn);

        System.setOut(original);

        String log = output.toString();

        assertTrue(log.contains("TXN-1"));
        assertTrue(log.contains("SUCCESS"));
        assertTrue(log.contains("Hyderabad, Telangana, India"));
        assertTrue(log.contains("1000"));
    }

    // Case 2: Location is null → "Unknown"
    @Test
    void shouldLogTransaction_withUnknownLocation() {

        LoggingService service = new LoggingService();
        Transaction txn = createTransactionWithoutLocation();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(output));

        service.update("FLAGGED", txn);

        System.setOut(original);

        String log = output.toString();

        assertTrue(log.contains("TXN-2"));
        assertTrue(log.contains("FLAGGED"));
        assertTrue(log.contains("Unknown"));
        assertTrue(log.contains("500"));
    }
}
