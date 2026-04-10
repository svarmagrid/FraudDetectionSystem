package org.example.repository;

import org.example.jdbc.JdbcUtil;
import org.example.model.Card;

import java.sql.Date;
import java.util.Optional;

public class CardRepository {

    private final JdbcUtil jdbc;

    public CardRepository(JdbcUtil jdbc) {
        this.jdbc = jdbc;
    }

    //  Save Card
//    public int save(Card card) {
//        return jdbc.findOne(
//                "INSERT INTO cards(card_number, cardholder_name, expiry_date, card_type, issuing_bank, cvv, email) " +
//                        "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING card_id",
//                rs -> {
//                    try {
//                        return rs.getInt("card_id");
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                },
//                card.getCardNumber(),
//                card.getCardholderName(),
//                Date.valueOf(card.getExpiryDate()), //  no fallback, must be valid
//                card.getCardType(),
//                card.getIssuingBank(),
//                card.getCvv(),
//                card.getEmail()
//        ).orElseThrow();
//    }
    public int save(Card card) {
        jdbc.execute(
                "INSERT INTO cards(card_number, cardholder_name, expiry_date, card_type, issuing_bank, cvv, email) VALUES (?, ?, ?, ?, ?, ?, ?)",
                card.getCardNumber(),
                card.getCardholderName(),
                Date.valueOf(card.getExpiryDate()),
                card.getCardType(),
                card.getIssuingBank(),
                card.getCvv(),
                card.getEmail()
        );

        return jdbc.findOne(
                "SELECT card_id FROM cards WHERE card_number = ?",
                rs -> {
                    try {
                        return rs.getInt("card_id");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                card.getCardNumber()
        ).orElseThrow();
    }

    // Find Card by Number
    public Optional<Card> findByNumber(String cardNumber) {
        return jdbc.findOne(
                "SELECT * FROM cards WHERE card_number = ?",
                rs -> {
                    try {
                        return new Card(
                                rs.getInt("card_id"),
                                rs.getString("card_number"),
                                rs.getString("cardholder_name"),
                                rs.getDate("expiry_date").toLocalDate(), // safe (NOT NULL)
                                rs.getString("card_type"),
                                rs.getString("issuing_bank"),
                                rs.getString("cvv"),
                                rs.getString("email")
                        );
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                cardNumber
        );
    }
}