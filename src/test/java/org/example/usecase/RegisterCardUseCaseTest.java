package org.example.usecase;

import org.example.repository.CardRepository;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Optional;
import java.util.Scanner;

import static org.mockito.Mockito.*;

class RegisterCardUseCaseTest {

    @Test
    void shouldRegisterNewCard() {

        CardRepository repo = mock(CardRepository.class);

        when(repo.findByNumber(any())).thenReturn(Optional.empty());

        Scanner scanner = new Scanner(new ByteArrayInputStream(
                ("1234567812345678\nSatish\n123\n2026-12-31\ntest@mail.com\n").getBytes()
        ));

        RegisterCardUseCase useCase = new RegisterCardUseCase(repo);

        useCase.execute(scanner);

        verify(repo).save(any());
    }

    @Test
    void shouldRejectDuplicateCard() {

        CardRepository repo = mock(CardRepository.class);

        when(repo.findByNumber(any())).thenReturn(Optional.of(mock(org.example.model.Card.class)));

        Scanner scanner = new Scanner(new ByteArrayInputStream(
                ("1234567812345678\n").getBytes()
        ));

        RegisterCardUseCase useCase = new RegisterCardUseCase(repo);

        useCase.execute(scanner);

        verify(repo, never()).save(any());
    }
}