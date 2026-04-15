package org.example.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    private Card createCard() {
        return new Card(
                1,
                "1234567812345678",
                "Satish",
                YearMonth.now().plusYears(1),
                "VISA",
                "HDFC",
                "123",
                "test@mail.com"
        );
    }

    private Merchant createMerchant() {
        return new Merchant(1, "Amazon", "Shopping");
    }

    private Location createLocation() {
        return new Location(1, "Hyderabad", "Telangana", "India");
    }

    // Full constructor test
    @Test
    void shouldInitializeAllFields_usingFullConstructor() {

        Card card = createCard();
        Merchant merchant = createMerchant();
        Location location = createLocation();
        LocalDateTime now = LocalDateTime.now();

        Transaction txn = new Transaction(
                "TXN-1",
                card,
                1000,
                "USD",
                now,
                "SUCCESS",
                "CARD",
                merchant,
                location
        );

        assertEquals("TXN-1", txn.getTransactionId());
        assertEquals(card, txn.getCard());
        assertEquals(1000, txn.getAmount());
        assertEquals("USD", txn.getCurrency());
        assertEquals(now, txn.getTimestamp());
        assertEquals("SUCCESS", txn.getStatus());
        assertEquals("CARD", txn.getPaymentChannel());
        assertEquals(merchant, txn.getMerchant());
        assertEquals(location, txn.getLocation());
    }

    // Short constructor (default values path)
    @Test
    void shouldUseDefaultValues_inShortConstructor() {

        Card card = createCard();
        LocalDateTime now = LocalDateTime.now();

        Transaction txn = new Transaction(
                "TXN-2",
                card,
                500,
                now
        );

        assertEquals("TXN-2", txn.getTransactionId());
        assertEquals(card, txn.getCard());
        assertEquals(500, txn.getAmount());
        assertEquals("INR", txn.getCurrency()); // default
        assertEquals(now, txn.getTimestamp());
        assertEquals("PENDING", txn.getStatus()); // default
        assertEquals("ONLINE", txn.getPaymentChannel()); // from constructor
        assertNull(txn.getMerchant());
        assertNull(txn.getLocation());
    }

    // Test status update
    @Test
    void shouldUpdateStatus() {

        Transaction txn = new Transaction(
                "TXN-3",
                createCard(),
                200,
                LocalDateTime.now()
        );

        txn.setStatus("SUCCESS");

        assertEquals("SUCCESS", txn.getStatus());
    }

    // Test merchant update
    @Test
    void shouldSetMerchant() {

        Transaction txn = new Transaction(
                "TXN-4",
                createCard(),
                300,
                LocalDateTime.now()
        );

        Merchant merchant = createMerchant();
        txn.setMerchant(merchant);

        assertEquals(merchant, txn.getMerchant());
    }

    // Test location update
    @Test
    void shouldSetLocation() {

        Transaction txn = new Transaction(
                "TXN-5",
                createCard(),
                400,
                LocalDateTime.now()
        );

        Location location = createLocation();
        txn.setLocation(location);

        assertEquals(location, txn.getLocation());
    }

    // Edge case: null merchant & location
    @Test
    void shouldAllowNullMerchantAndLocation() {

        Transaction txn = new Transaction(
                "TXN-6",
                createCard(),
                600,
                LocalDateTime.now()
        );

        txn.setMerchant(null);
        txn.setLocation(null);

        assertNull(txn.getMerchant());
        assertNull(txn.getLocation());
    }
}
