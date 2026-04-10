package org.example.repository;

import org.example.jdbc.JdbcUtil;
import org.example.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TransactionRepositoryTest {

    private JdbcUtil jdbc;
    private CardRepository cardRepo;
    private TransactionRepository repo;

    @BeforeEach
    void setup() {
        jdbc = mock(JdbcUtil.class);
        cardRepo = mock(CardRepository.class);

        repo = new TransactionRepository(jdbc, cardRepo);
    }

    private Transaction mockTransaction() {
        Card card = new Card(
                1,
                "1234567812345678",
                "Satish",
                LocalDate.now().plusYears(1),
                "VISA",
                "HDFC",
                "123",
                "mail@test.com"
        );

        Merchant merchant = new Merchant(1, "Amazon", "Shopping");
        Location location = new Location(1, "Hyderabad", "TS", "India");

        Transaction t = new Transaction("TXN1", card, 1000, LocalDateTime.now());
        t.setMerchant(merchant);
        t.setLocation(location);

        return t;
    }

    //  CARD EXISTS
    @Test
    void shouldUseExistingCard() {

        Transaction t = mockTransaction();

        when(cardRepo.findByNumber(any())).thenReturn(Optional.of(t.getCard()));

        //  FIX: mock BOTH signatures
        when(jdbc.findOne(anyString(), any(), any(), any(), any()))
                .thenReturn(Optional.of(1)); // location

        when(jdbc.findOne(anyString(), any(), any()))
                .thenReturn(Optional.of(2)); // merchant

        repo.save(t);

        verify(cardRepo, never()).save(any());
        verify(jdbc, atLeastOnce()).execute(any(), any(), any(), any());
    }

    //  CARD NOT EXISTS
    @Test
    void shouldCreateCardIfNotExists() {

        Transaction t = mockTransaction();

        when(cardRepo.findByNumber(any())).thenReturn(Optional.empty());
        when(cardRepo.save(any())).thenReturn(99);

        when(jdbc.findOne(anyString(), any(), any(), any(), any()))
                .thenReturn(Optional.of(1));

        when(jdbc.findOne(anyString(), any(), any()))
                .thenReturn(Optional.of(2));

        repo.save(t);

        verify(cardRepo).save(any());
    }

    //  LOCATION FAILURE
    @Test
    void shouldThrowIfLocationNotFound() {

        Transaction t = mockTransaction();

        when(cardRepo.findByNumber(any())).thenReturn(Optional.of(t.getCard()));

        when(jdbc.findOne(anyString(), any(), any(), any(), any()))
                .thenReturn(Optional.empty()); // location missing

        assertThrows(RuntimeException.class, () -> repo.save(t));
    }

    //  MERCHANT FAILURE
    @Test
    void shouldThrowIfMerchantNotFound() {

        Transaction t = mockTransaction();

        when(cardRepo.findByNumber(any())).thenReturn(Optional.of(t.getCard()));

        when(jdbc.findOne(anyString(), any(), any(), any(), any()))
                .thenReturn(Optional.of(1)); // location ok

        when(jdbc.findOne(anyString(), any(), any()))
                .thenReturn(Optional.empty()); // merchant missing

        assertThrows(RuntimeException.class, () -> repo.save(t));
    }

    //  FIND ALL (MAPPING)
    @Test
    void shouldMapFindAllCorrectly() throws Exception {

        when(jdbc.findMany(any(), any()))
                .thenAnswer(invocation -> {

                    Function<ResultSet, Transaction> mapper =
                            (Function<ResultSet, Transaction>) invocation.getArgument(1);

                    ResultSet rs = mock(ResultSet.class);

                    when(rs.getInt("card_id")).thenReturn(1);
                    when(rs.getString("card_number")).thenReturn("1234567812345678");
                    when(rs.getString("cardholder_name")).thenReturn("Satish");
                    when(rs.getDate("expiry_date"))
                            .thenReturn(java.sql.Date.valueOf(LocalDate.now()));
                    when(rs.getString("card_type")).thenReturn("VISA");
                    when(rs.getString("issuing_bank")).thenReturn("HDFC");
                    when(rs.getString("email")).thenReturn("mail@test.com");

                    when(rs.getInt("merchant_id")).thenReturn(1);
                    when(rs.getString("merchant_name")).thenReturn("Amazon");
                    when(rs.getString("merchant_category")).thenReturn("Shopping");

                    when(rs.getInt("location_id")).thenReturn(1);
                    when(rs.getString("city")).thenReturn("Hyderabad");
                    when(rs.getString("state")).thenReturn("TS");
                    when(rs.getString("country")).thenReturn("India");

                    when(rs.getString("transaction_id")).thenReturn("TXN1");
                    when(rs.getDouble("amount")).thenReturn(1000.0);
                    when(rs.getString("currency")).thenReturn("INR");
                    when(rs.getTimestamp("transaction_time"))
                            .thenReturn(java.sql.Timestamp.valueOf(LocalDateTime.now()));
                    when(rs.getString("status")).thenReturn("SUCCESS");
                    when(rs.getString("payment_channel")).thenReturn("ONLINE");

                    return List.of(mapper.apply(rs));
                });

        List<Transaction> result = repo.findAll();

        assertEquals(1, result.size());
    }

    // FIND FLAGGED
    @Test
    void shouldReturnFlaggedTransactions() {

        when(jdbc.findMany(any(), any()))
                .thenReturn(List.of(mock(Transaction.class)));

        List<Transaction> result = repo.findFlagged();

        assertEquals(1, result.size());
    }
}