package org.example.model;

import org.junit.jupiter.api.Test;

import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

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

    // Getters
    @Test
    void shouldReturnCorrectValues_fromGetters() {
        Card card = createCard();

        assertEquals(1, card.getCardId());
        assertEquals("1234567812345678", card.getCardNumber());
        assertEquals("Satish", card.getCardholderName());
        assertNotNull(card.getExpiryDate());
        assertEquals("VISA", card.getCardType());
        assertEquals("HDFC", card.getIssuingBank());
        assertEquals("123", card.getCvv());
        assertEquals("test@mail.com", card.getEmail());
    }

    // Setters
    @Test
    void shouldUpdateCvv_andEmail() {
        Card card = createCard();

        card.setCvv("999");
        card.setEmail("new@mail.com");

        assertEquals("999", card.getCvv());
        assertEquals("new@mail.com", card.getEmail());
    }

    // equals: same reference
    @Test
    void shouldReturnTrue_whenSameReference() {
        Card card = createCard();

        assertEquals(card, card);
    }

    // equals: equal objects
    @Test
    void shouldBeEqual_whenCardNumbersAreSame() {
        Card card1 = createCard();

        Card card2 = new Card(
                2,
                "1234567812345678",
                "Other",
                YearMonth.now().plusYears(2),
                "MASTER",
                "ICICI",
                "999",
                "other@mail.com"
        );

        assertEquals(card1, card2);
        assertEquals(card1.hashCode(), card2.hashCode());
    }

    //  equals: null
    @Test
    void shouldReturnFalse_whenComparedWithNull() {
        Card card = createCard();

        assertNotEquals(card, null);
    }

    // equals: different type
    @Test
    void shouldReturnFalse_whenDifferentType() {
        Card card = createCard();

        assertNotEquals(card, "Some String");
    }

    // equals: different card number
    @Test
    void shouldNotBeEqual_whenCardNumbersDiffer() {
        Card card1 = createCard();

        Card card2 = new Card(
                2,
                "9999999999999999",
                "Satish",
                YearMonth.now().plusYears(1),
                "VISA",
                "HDFC",
                "123",
                "test@mail.com"
        );

        assertNotEquals(card1, card2);
    }

    // toString masking normal case
    @Test
    void shouldMaskCardNumber_inToString() {
        Card card = createCard();

        String result = card.toString();

        assertTrue(result.contains("**** **** **** 5678"));
        assertFalse(result.contains("1234567812345678"));
    }

    // maskCardNumber: null
    @Test
    void shouldHandleNullCardNumber() {
        Card card = new Card(
                1,
                null,
                "Satish",
                YearMonth.now().plusYears(1),
                "VISA",
                "HDFC",
                "123",
                "test@mail.com"
        );

        assertTrue(card.toString().contains("****"));
    }

    // maskCardNumber: length < 4
    @Test
    void shouldHandleShortCardNumber() {
        Card card = new Card(
                1,
                "123",
                "Satish",
                YearMonth.now().plusYears(1),
                "VISA",
                "HDFC",
                "123",
                "test@mail.com"
        );

        assertTrue(card.toString().contains("****"));
    }
}
