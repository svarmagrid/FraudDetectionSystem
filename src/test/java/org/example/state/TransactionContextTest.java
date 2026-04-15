package org.example.state;

import org.example.model.Card;
import org.example.model.Transaction;
import org.example.observer.Observer;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class TransactionContextTest {

    private Transaction createTransaction() {
        Card card = new Card(
                1, "1234567812345678", "Satish",
                java.time.YearMonth.now().plusYears(1),
                null, null, "123", "test@mail.com"
        );

        return new Transaction(
                "TXN-1",
                card,
                1000,
                java.time.LocalDateTime.now()
        );
    }

    @Test
    void shouldInitializeWithPendingState() {
        Transaction txn = createTransaction();
        TransactionContext context = new TransactionContext(txn);

        assertEquals("PENDING", context.getStateName());
    }

    @Test
    void shouldUpdateTransactionStatus_whenStateChanges() {
        Transaction txn = createTransaction();
        TransactionContext context = new TransactionContext(txn);

        TransactionState state = mock(TransactionState.class);
        when(state.getName()).thenReturn("SUCCESS");

        context.setState(state);

        assertEquals("SUCCESS", txn.getStatus());
    }

    @Test
    void shouldNotifyObservers_whenStateChanges() {
        Transaction txn = createTransaction();
        TransactionContext context = new TransactionContext(txn);

        Observer observer = mock(Observer.class);
        context.addObserver(observer);

        TransactionState state = mock(TransactionState.class);
        when(state.getName()).thenReturn("FLAGGED");

        context.setState(state);

        verify(observer).update("FLAGGED", txn);
    }

    @Test
    void shouldCallHandle_onStateChange() {
        Transaction txn = createTransaction();
        TransactionContext context = new TransactionContext(txn);

        TransactionState state = mock(TransactionState.class);
        when(state.getName()).thenReturn("SUCCESS");

        context.setState(state);

        verify(state).handle(context);
    }

    @Test
    void shouldNotNotifyRemovedObserver() {
        Transaction txn = createTransaction();
        TransactionContext context = new TransactionContext(txn);

        Observer observer = mock(Observer.class);
        context.addObserver(observer);
        context.removeObserver(observer);

        TransactionState state = mock(TransactionState.class);
        when(state.getName()).thenReturn("SUCCESS");

        context.setState(state);

        verify(observer, never()).update(anyString(), any());
    }
}
