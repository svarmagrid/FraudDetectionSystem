package org.example.service;

import org.example.model.*;
import org.example.repository.TransactionRepository;
import org.example.state.TransactionContext;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class FraudDetectionServiceTest {

    private TransactionRepository repo = mock(TransactionRepository.class);
    private FraudDetectionService service = new FraudDetectionService(repo);

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

    private Transaction createTxn(String id, String city, LocalDateTime time) {
        Transaction txn = new Transaction(id, createCard(), 1000, time);
        txn.setLocation(new Location(1, city, "State", "India"));
        return txn;
    }

    // Case 1: No fraud → SUCCESS
    @Test
    void shouldMarkSuccess_whenNoFraudDetected() {

        Transaction current = createTxn("TXN-1", "Hyderabad", LocalDateTime.now());

        when(repo.findAll()).thenReturn(List.of()); // no history

        TransactionContext context = new TransactionContext(current);

        service.evaluate(context);

        assertEquals("SUCCESS", current.getStatus());
        verify(repo).save(current);
    }

    // Case 2: Velocity fraud → FLAGGED
    @Test
    void shouldFlag_whenVelocityIsHigh() {

        String city = "Hyderabad";
        LocalDateTime now = LocalDateTime.now();

        Transaction t1 = createTxn("1", city, now.minusSeconds(10));
        Transaction t2 = createTxn("2", city, now.minusSeconds(20));
        Transaction t3 = createTxn("3", city, now.minusSeconds(30));

        Transaction current = createTxn("TXN-4", city, now);

        when(repo.findAll()).thenReturn(List.of(t1, t2, t3));

        TransactionContext context = new TransactionContext(current);

        service.evaluate(context);

        assertEquals("FLAGGED", current.getStatus());
        verify(repo).save(current);
    }

    // Case 3: Location fraud → FLAGGED
    @Test
    void shouldFlag_whenLocationChanges() {

        Transaction previous = createTxn("OLD", "Delhi", LocalDateTime.now().minusMinutes(5));

        Transaction current = createTxn("NEW", "Hyderabad", LocalDateTime.now());

        when(repo.findAll()).thenReturn(List.of(previous));

        TransactionContext context = new TransactionContext(current);

        service.evaluate(context);

        assertEquals("FLAGGED", current.getStatus());
    }

    // Case 4: Same location → not flagged
    @Test
    void shouldNotFlag_whenLocationSame() {

        String city = "Hyderabad";

        Transaction previous = createTxn("OLD", city, LocalDateTime.now().minusMinutes(5));
        Transaction current = createTxn("NEW", city, LocalDateTime.now());

        when(repo.findAll()).thenReturn(List.of(previous));

        TransactionContext context = new TransactionContext(current);

        service.evaluate(context);

        assertEquals("SUCCESS", current.getStatus());
    }

    // Case 5: Less than 3 transactions → no velocity fraud
    @Test
    void shouldNotTriggerVelocity_whenLessThan3Transactions() {

        Transaction t1 = createTxn("1", "Hyderabad", LocalDateTime.now().minusSeconds(10));
        Transaction t2 = createTxn("2", "Hyderabad", LocalDateTime.now().minusSeconds(20));

        Transaction current = createTxn("NEW", "Hyderabad", LocalDateTime.now());

        when(repo.findAll()).thenReturn(List.of(t1, t2));

        TransactionContext context = new TransactionContext(current);

        service.evaluate(context);

        assertEquals("SUCCESS", current.getStatus());
    }
}
