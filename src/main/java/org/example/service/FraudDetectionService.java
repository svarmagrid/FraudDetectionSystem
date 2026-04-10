package org.example.service;

import org.example.model.Transaction;
import org.example.repository.TransactionRepository;
import org.example.state.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class FraudDetectionService {

    private final TransactionRepository repo;

    public FraudDetectionService(TransactionRepository repo) {
        this.repo = repo;
    }

    public void evaluate(TransactionContext context) {
        Transaction t = context.getTransaction();
        String cardNumber = t.getCard().getCardNumber();

        boolean velocity = checkVelocity(cardNumber);
        boolean location = checkLocation(cardNumber, t);

        if (velocity || location) {
            t.setStatus("FLAGGED");
            context.setState(new FlaggedState());
        } else {
            t.setStatus("SUCCESS");
            context.setState(new ClearedState());
        }

        repo.save(t);
    }

    // Velocity Check
    private boolean checkVelocity(String cardNumber) {
        List<Transaction> txns = repo.findAll().stream()
                .filter(t -> t.getCard().getCardNumber().equals(cardNumber))
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .limit(3)
                .collect(Collectors.toList());

        if (txns.size() < 3) return false;

        LocalDateTime first = txns.get(2).getTimestamp();
        LocalDateTime last = txns.get(0).getTimestamp();

        long seconds = java.time.Duration.between(first, last).getSeconds();

        return seconds < 60;
    }

    // Location Check
    private boolean checkLocation(String cardNumber, Transaction currentTxn) {

        List<Transaction> lastTxn = repo.findAll().stream()
                .filter(t -> t.getCard().getCardNumber().equals(cardNumber))
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .limit(1)
                .collect(Collectors.toList());

        if (lastTxn.isEmpty()) return false;

        String prevCity = lastTxn.get(0).getLocation().getCity();
        String newCity = currentTxn.getLocation().getCity();

        return !prevCity.equalsIgnoreCase(newCity);
    }
}