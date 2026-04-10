package org.example.usecase;

import org.example.model.*;
import org.example.observer.Observer;
import org.example.repository.CardRepository;
import org.example.repository.TransactionRepository;
import org.example.service.*;

import org.example.state.TransactionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProcessTransactionUseCaseTest {

    private CardRepository cardRepo;
    private TransactionRepository txnRepo;
    private FraudDetectionService fraudService;
    private EmailNotificationService emailService;
    private Observer logger;
    private Observer alert;
    private OtpGenerator otpGenerator;
    private ProcessTransactionUseCase useCase;

    @BeforeEach
    void setup() {
        cardRepo = mock(CardRepository.class);
        txnRepo = mock(TransactionRepository.class);
        fraudService = mock(FraudDetectionService.class);
        emailService = mock(EmailNotificationService.class);
        logger = mock(Observer.class);
        alert = mock(Observer.class);
        otpGenerator = mock(OtpGenerator.class);

        useCase = new ProcessTransactionUseCase(
                cardRepo, txnRepo, fraudService, emailService, logger, alert, otpGenerator
        );
    }

    private Card validCard() {
        return new Card(
                1,
                "1234567812345678",
                "Satish",
                LocalDate.now().plusYears(1),
                "VISA",
                "HDFC",
                "123",
                "test@mail.com"
        );
    }

    private Card expiredCard() {
        return new Card(
                1,
                "1234567812345678",
                "Satish",
                LocalDate.now().minusYears(1),
                "VISA",
                "HDFC",
                "123",
                "test@mail.com"
        );
    }

    // -----------------------------
    // BASIC FLOWS
    // -----------------------------

    @Test
    void shouldRejectInvalidCardNumber() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("123\n".getBytes()));

        useCase.execute(scanner);

        verifyNoInteractions(cardRepo);
    }

    @Test
    void shouldRejectCardNotFound() {
        when(cardRepo.findByNumber(any())).thenReturn(Optional.empty());

        Scanner scanner = new Scanner("1234567812345678\n");

        useCase.execute(scanner);

        verify(cardRepo).findByNumber(any());
    }

    // -----------------------------
    // EXPIRY + COOLDOWN
    // -----------------------------

    @Test
    void shouldRejectExpiredCard() {
        when(cardRepo.findByNumber(any())).thenReturn(Optional.of(expiredCard()));

        Scanner scanner = new Scanner("1234567812345678\n");

        useCase.execute(scanner);

        verifyNoInteractions(emailService);
    }

    @Test
    void shouldRejectDueToCooldown() throws Exception {
        Card card = validCard();
        when(cardRepo.findByNumber(any())).thenReturn(Optional.of(card));

        // inject cooldown map
        Field field = useCase.getClass().getDeclaredField("flaggedCards");
        field.setAccessible(true);
        Map<String, LocalDateTime> map = (Map<String, LocalDateTime>) field.get(useCase);
        map.put(card.getCardNumber(), LocalDateTime.now());

        Scanner scanner = new Scanner("1234567812345678\n");

        useCase.execute(scanner);

        verifyNoInteractions(emailService);
    }

    // -----------------------------
    // CVV BRANCHES
    // -----------------------------

    @Test
    void shouldHandleInvalidCvvAttemptsAndTriggerFraudAlert() {
        Card card = validCard();
        when(cardRepo.findByNumber(any())).thenReturn(Optional.of(card));

        for (int i = 0; i < 3; i++) {
            Scanner scanner = new Scanner(new ByteArrayInputStream(
                    ("1234567812345678\n999\n").getBytes()
            ));
            useCase.execute(scanner);
        }

        verify(emailService).sendFraudAlert(eq(card.getEmail()), any());
    }

    // -----------------------------
    // OTP BRANCHES
    // -----------------------------

    @Test
    void shouldHandleInvalidOtpFormat() {
        Card card = validCard();
        when(cardRepo.findByNumber(any())).thenReturn(Optional.of(card));
        when(otpGenerator.generate()).thenReturn(1234);

        Scanner scanner = new Scanner(new ByteArrayInputStream(
                ("1234567812345678\n123\nabcd\n").getBytes()
        ));

        useCase.execute(scanner);

        verify(emailService).sendOtpEmail(any(), anyInt());
    }

    @Test
    void shouldHandleOtpFailureAndTriggerFraudAlert() {
        Card card = validCard();
        when(cardRepo.findByNumber(any())).thenReturn(Optional.of(card));
        when(otpGenerator.generate()).thenReturn(1234);

        for (int i = 0; i < 3; i++) {
            Scanner scanner = new Scanner(new ByteArrayInputStream(
                    ("1234567812345678\n123\n0000\n").getBytes()
            ));
            useCase.execute(scanner);
        }

        verify(emailService).sendFraudAlert(eq(card.getEmail()), any());
    }

    // -----------------------------
    // AMOUNT BRANCH
    // -----------------------------

    @Test
    void shouldRejectInvalidAmount() {
        Card card = validCard();

        when(cardRepo.findByNumber(any())).thenReturn(Optional.of(card));
        when(otpGenerator.generate()).thenReturn(1234);

        Scanner scanner = new Scanner(new ByteArrayInputStream(
                ("1234567812345678\n123\n1234\nabc\n").getBytes()
        ));

        useCase.execute(scanner);

        verify(fraudService, never()).evaluate(any());
    }

    // -----------------------------
    // SUCCESS FLOW
    // -----------------------------

    @Test
    void shouldProcessSuccessfulTransaction() {

        Card card = validCard();

        when(cardRepo.findByNumber(any())).thenReturn(Optional.of(card));
        when(otpGenerator.generate()).thenReturn(1234);

        Scanner scanner = new Scanner(new ByteArrayInputStream(
                ("1234567812345678\n123\n1234\n1000\nAmazon\nShopping\n").getBytes()
        ));

        useCase.execute(scanner);

        verify(fraudService).evaluate(any());
    }

    // -----------------------------
    // FLAGGED FLOW
    // -----------------------------

    @Test
    void shouldStoreCooldownWhenFlagged() throws Exception {

        Card card = validCard();

        when(cardRepo.findByNumber(any())).thenReturn(Optional.of(card));
        when(otpGenerator.generate()).thenReturn(1234);

        doAnswer(invocation -> {
            TransactionContext ctx = invocation.getArgument(0);
            ctx.getTransaction().setStatus("FLAGGED");
            return null;
        }).when(fraudService).evaluate(any());

        Scanner scanner = new Scanner(new ByteArrayInputStream(
                ("1234567812345678\n123\n1234\n1000\nAmazon\nShopping\n").getBytes()
        ));

        useCase.execute(scanner);

        Field field = useCase.getClass().getDeclaredField("flaggedCards");
        field.setAccessible(true);
        Map<String, ?> map = (Map<String, ?>) field.get(useCase);

        assertTrue(map.containsKey(card.getCardNumber()));
    }
}