package org.example.usecase;

import org.example.model.*;
import org.example.repository.TransactionRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static org.mockito.Mockito.*;

class ViewFlaggedTransactionsUseCaseTest {

    @Test
    void shouldReturnOnlyFlaggedTransactions() {

        TransactionRepository repo = mock(TransactionRepository.class);

        Card card = new Card(1, "1234567812345678", "Satish",
                YearMonth.now(), "VISA", "HDFC", "123", "mail");

        Transaction flagged = new Transaction("TXN1", card, 1000, LocalDateTime.now());
        flagged.setStatus("FLAGGED");
        flagged.setLocation(new Location(1, "Delhi", "Delhi", "India"));

        Transaction normal = new Transaction("TXN2", card, 500, LocalDateTime.now());
        normal.setStatus("SUCCESS");
        normal.setLocation(new Location(2, "Mumbai", "MH", "India"));

        when(repo.findAll()).thenReturn(List.of(flagged, normal));

        ViewFlaggedTransactionsUseCase useCase = new ViewFlaggedTransactionsUseCase(repo);

        useCase.execute();

        verify(repo).findAll();
    }
}
