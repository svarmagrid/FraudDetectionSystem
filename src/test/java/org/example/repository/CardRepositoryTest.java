package org.example.repository;

import org.example.jdbc.JdbcUtil;
import org.example.model.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CardRepositoryTest {

    private JdbcUtil jdbc;
    private CardRepository repo;

    @BeforeEach
    void setup() {
        jdbc = new JdbcUtil(
                "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
                "sa",
                ""
        );

        repo = new CardRepository(jdbc);

        jdbc.execute("DROP TABLE IF EXISTS cards");

        jdbc.execute("""
                CREATE TABLE cards (
                    card_id INT AUTO_INCREMENT PRIMARY KEY,
                    card_number VARCHAR(20),
                    cardholder_name VARCHAR(50),
                    expiry_date DATE,
                    card_type VARCHAR(20),
                    issuing_bank VARCHAR(50),
                    cvv VARCHAR(3),
                    email VARCHAR(100)
                )
        """);
    }

    private Card createCard() {
        return new Card(
                0,
                "1234567812345678",
                "Satish",
                LocalDate.now().plusYears(1),
                "VISA",
                "HDFC",
                "123",
                "test@mail.com"
        );
    }

    // Save test
    @Test
    void shouldSaveCard_andReturnId() {

        Card card = createCard();

        int id = repo.save(card);

        assertTrue(id > 0);
    }

    // Find by number
    @Test
    void shouldFindCard_byCardNumber() {

        Card card = createCard();
        repo.save(card);

        Optional<Card> result = repo.findByNumber("1234567812345678");

        assertTrue(result.isPresent());

        Card found = result.get();

        assertEquals("1234567812345678", found.getCardNumber());
        assertEquals("Satish", found.getCardholderName());
        assertEquals("HDFC", found.getIssuingBank());
    }

    // Not found case
    @Test
    void shouldReturnEmpty_whenCardNotFound() {

        Optional<Card> result = repo.findByNumber("9999999999999999");

        assertTrue(result.isEmpty());
    }
}