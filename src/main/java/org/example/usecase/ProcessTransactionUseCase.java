package org.example.usecase;

import org.example.model.*;
import org.example.observer.Observer;
import org.example.repository.CardRepository;
import org.example.repository.TransactionRepository;
import org.example.service.*;
import org.example.state.*;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

public class ProcessTransactionUseCase {

    private final CardRepository cardRepo;
    private final TransactionRepository repo;
    private final FraudDetectionService fraudService;
    private final EmailNotificationService emailService;
    private final Observer logger;
    private final Observer alert;
    private final OtpGenerator otpGenerator;

    private final Map<String, Integer> failedAttempts = new HashMap<>();
    private final Map<String, LocalDateTime> flaggedCards = new HashMap<>();

    private static final int COOLDOWN_MINUTES = 2;

    public ProcessTransactionUseCase(CardRepository cardRepo,
                                     TransactionRepository repo,
                                     FraudDetectionService fraudService,
                                     EmailNotificationService emailService,
                                     Observer logger,
                                     Observer alert,
                                     OtpGenerator otpGenerator) {

        this.cardRepo = cardRepo;
        this.repo = repo;
        this.fraudService = fraudService;
        this.emailService = emailService;
        this.logger = logger;
        this.alert = alert;
        this.otpGenerator = otpGenerator;
    }

    public void execute(Scanner scanner) {

        System.out.print("Enter Card Number: ");
        String cardNumber = scanner.nextLine().replaceAll("\\s+", "");

        if (cardNumber.length() < 16) {
            System.out.println("Invalid card number");
            return;
        }

        Optional<Card> existingCard = cardRepo.findByNumber(cardNumber);

        if (existingCard.isEmpty()) {
            System.out.println("Card not found. Please register card first.");
            return;
        }

        Card cardObj = existingCard.get();

        // Cooldown check
        LocalDateTime lastFlagged = flaggedCards.get(cardNumber);
        if (lastFlagged != null) {
            long minutes = java.time.Duration.between(lastFlagged, LocalDateTime.now()).toMinutes();

            if (minutes < COOLDOWN_MINUTES) {
                System.out.println("Card temporarily blocked due to suspicious activity. Try after "
                        + (COOLDOWN_MINUTES - minutes) + " minute(s).");
                return;
            }
        }

        // Expiry check
        if (YearMonth.from(cardObj.getExpiryDate()).isBefore(YearMonth.now())) {
            System.out.println("Card has expired. Transaction denied.");
            return;
        }

        // CVV check
        System.out.print("Enter CVV: ");
        String inputCvv = scanner.nextLine();

        if (!cardObj.getCvv().equals(inputCvv)) {

            int attempts = failedAttempts.getOrDefault(cardNumber, 0) + 1;
            failedAttempts.put(cardNumber, attempts);

            System.out.println("Invalid CVV. Attempt: " + attempts);

            if (attempts >= 3) {
                System.out.println("Suspicious activity detected");
                emailService.sendFraudAlert(cardObj.getEmail(), cardNumber);
                failedAttempts.put(cardNumber, 0);
            }
            return;
        }

        // OTP generation
        int otp = otpGenerator.generate();

        System.out.println("OTP sent to: " + cardObj.getEmail());
        emailService.sendOtpEmail(cardObj.getEmail(), otp);

        System.out.print("Enter OTP: ");
        int userOtp;

        try {
            userOtp = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("Invalid OTP format");
            return;
        }

//        if (userOtp != otp) {
//
//            int attempts = failedAttempts.getOrDefault(cardNumber, 0) + 1;
//            failedAttempts.put(cardNumber, attempts);
//
//            System.out.println("Incorrect OTP. Attempt: " + attempts);
//
//            if (attempts >= 3) {
//                System.out.println("Suspicious activity detected");
//                emailService.sendFraudAlert(cardObj.getEmail(), cardNumber);
//                failedAttempts.put(cardNumber, 0);
//            }
//            return;
//        }

        // Reset attempts
        failedAttempts.put(cardNumber, 0);

        // Amount
        System.out.print("Enter Amount: ");
        double amount;

        try {
            amount = Double.parseDouble(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("Invalid amount");
            return;
        }

        // Merchant
        System.out.print("Enter Merchant Name: ");
        String merchantName = scanner.nextLine();

        System.out.print("Enter Merchant Category: ");
        String merchantCategory = scanner.nextLine();

        Merchant merchant = new Merchant(0, merchantName, merchantCategory);

        Location loc = LocationService.getLocation();

        Transaction txn = new Transaction(
                "TXN-" + UUID.randomUUID(),
                cardObj,
                amount,
                LocalDateTime.now()
        );

        txn.setMerchant(merchant);
        txn.setLocation(loc);

        TransactionContext context = new TransactionContext(txn);
        context.addObserver(logger);
        context.addObserver(alert);
        context.addObserver(emailService);
        context.setState(new PendingState());

        fraudService.evaluate(context);

        if ("FLAGGED".equalsIgnoreCase(txn.getStatus())) {
            flaggedCards.put(cardNumber, LocalDateTime.now());
        }
    }
}