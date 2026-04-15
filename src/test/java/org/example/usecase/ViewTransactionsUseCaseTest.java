package org.example.usecase;

import org.example.model.*;
import org.example.repository.TransactionRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static org.mockito.Mockito.*;

class ViewTransactionsUseCaseTest {

    @Test
    void shouldPrintTransactions() {

        TransactionRepository repo = mock(TransactionRepository.class);

        Card card = new Card(1, "1234567812345678", "Satish",
                YearMonth.now(), "VISA", "HDFC", "123", "mail");

        Transaction txn = new Transaction("TXN1", card, 1000, LocalDateTime.now());

        txn.setLocation(new Location(1, "Hyderabad", "Telangana", "India"));

        when(repo.findAll()).thenReturn(List.of(txn));

        ViewTransactionsUseCase useCase = new ViewTransactionsUseCase(repo);

        useCase.execute();

        verify(repo).findAll();
    }
}
