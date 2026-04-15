package org.example.repository;

import org.example.jdbc.JdbcUtil;
import org.example.model.*;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.YearMonth;
import java.util.List;

public class TransactionRepository {

    private final JdbcUtil jdbc;
    private final CardRepository cardRepo;

    public TransactionRepository(JdbcUtil jdbc, CardRepository cardRepo) {
        this.jdbc = jdbc;
        this.cardRepo = cardRepo;
    }

    //  SAVE TRANSACTION
    public void save(Transaction t) {

        //  Ensure card exists
        Card card = cardRepo.findByNumber(t.getCard().getCardNumber())
                .orElseGet(() -> {
                    int id = cardRepo.save(t.getCard());
                    return new Card(id,
                            t.getCard().getCardNumber(),
                            t.getCard().getCardholderName(),
                            t.getCard().getExpiryDate(),
                            t.getCard().getCardType(),
                            t.getCard().getIssuingBank(),
                            t.getCard().getCvv(),
                            t.getCard().getEmail());
                });

        //  Ensure location exists
        jdbc.execute(
                "INSERT INTO locations(city, state, country) VALUES (?, ?, ?) " +
                        "ON CONFLICT (city, state, country) DO NOTHING",
                t.getLocation().getCity(),
                t.getLocation().getState(),
                t.getLocation().getCountry()
        );

        int locationId = jdbc.findOne(
                "SELECT location_id FROM locations WHERE city=? AND state=? AND country=?",
                rs -> {
                    try { return rs.getInt("location_id"); }
                    catch (SQLException e) { throw new RuntimeException(e); }
                },
                t.getLocation().getCity(),
                t.getLocation().getState(),
                t.getLocation().getCountry()
        ).orElseThrow();

        //  Ensure merchant exists
        jdbc.execute(
                "INSERT INTO merchants(merchant_name, merchant_category) VALUES (?, ?) " +
                        "ON CONFLICT (merchant_name) DO NOTHING",
                t.getMerchant().getMerchantName(),
                t.getMerchant().getMerchantCategory()
        );

        int merchantId = jdbc.findOne(
                "SELECT merchant_id FROM merchants WHERE merchant_name=?",
                rs -> {
                    try { return rs.getInt("merchant_id"); }
                    catch (SQLException e) { throw new RuntimeException(e); }
                },
                t.getMerchant().getMerchantName()
        ).orElseThrow();

        // Insert Transaction
        jdbc.execute(
                "INSERT INTO transactions(transaction_id, card_id, amount, currency, transaction_time, status, payment_channel, merchant_id, location_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                t.getTransactionId(),
                card.getCardId(),
                t.getAmount(),
                t.getCurrency(),
                Timestamp.valueOf(t.getTimestamp()),
                t.getStatus(),
                t.getPaymentChannel(),
                merchantId,
                locationId
        );
    }

    // FETCH ALL TRANSACTIONS
    public List<Transaction> findAll() {
        return jdbc.findMany(
                "SELECT t.*, c.*, m.*, l.* " +
                        "FROM transactions t " +
                        "JOIN cards c ON t.card_id = c.card_id " +
                        "JOIN merchants m ON t.merchant_id = m.merchant_id " +
                        "JOIN locations l ON t.location_id = l.location_id",
                rs -> {
                    try {
                        Card card = new Card(
                                rs.getInt("card_id"),
                                rs.getString("card_number"),
                                rs.getString("cardholder_name"),
                                YearMonth.of(rs.getInt("expiry_year"), rs.getInt("expiry_month")),
                                rs.getString("card_type"),
                                rs.getString("issuing_bank"),
                                null ,
                                rs.getString("email")
                        );

                        Merchant merchant = new Merchant(
                                rs.getInt("merchant_id"),
                                rs.getString("merchant_name"),
                                rs.getString("merchant_category")
                        );

                        Location location = new Location(
                                rs.getInt("location_id"),
                                rs.getString("city"),
                                rs.getString("state"),
                                rs.getString("country")
                        );

                        return new Transaction(
                                rs.getString("transaction_id"),
                                card,
                                rs.getDouble("amount"),
                                rs.getString("currency"),
                                rs.getTimestamp("transaction_time").toLocalDateTime(),
                                rs.getString("status"),
                                rs.getString("payment_channel"),
                                merchant,
                                location
                        );
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    //  FETCH FLAGGED TRANSACTIONS
    public List<Transaction> findFlagged() {
        return jdbc.findMany(
                "SELECT t.*, c.*, m.*, l.* " +
                        "FROM transactions t " +
                        "JOIN cards c ON t.card_id = c.card_id " +
                        "JOIN merchants m ON t.merchant_id = m.merchant_id " +
                        "JOIN locations l ON t.location_id = l.location_id " +
                        "WHERE t.status='FLAGGED'",
                rs -> {
                    try {
                        Card card = new Card(
                                rs.getInt("card_id"),
                                rs.getString("card_number"),
                                rs.getString("cardholder_name"),
                                YearMonth.of(rs.getInt("expiry_year"), rs.getInt("expiry_month")),
                                rs.getString("card_type"),
                                rs.getString("issuing_bank"),
                                null,
                                rs.getString("email")
                        );

                        Merchant merchant = new Merchant(
                                rs.getInt("merchant_id"),
                                rs.getString("merchant_name"),
                                rs.getString("merchant_category")
                        );

                        Location location = new Location(
                                rs.getInt("location_id"),
                                rs.getString("city"),
                                rs.getString("state"),
                                rs.getString("country")
                        );

                        return new Transaction(
                                rs.getString("transaction_id"),
                                card,
                                rs.getDouble("amount"),
                                rs.getString("currency"),
                                rs.getTimestamp("transaction_time").toLocalDateTime(),
                                rs.getString("status"),
                                rs.getString("payment_channel"),
                                merchant,
                                location
                        );
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }
}
