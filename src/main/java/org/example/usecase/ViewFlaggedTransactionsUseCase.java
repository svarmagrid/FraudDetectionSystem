package org.example.usecase;

import org.example.model.Transaction;
import org.example.repository.TransactionRepository;

import java.util.List;

public class ViewFlaggedTransactionsUseCase {

    private final TransactionRepository repo;

    public ViewFlaggedTransactionsUseCase(TransactionRepository repo) {
        this.repo = repo;
    }

    public void execute() {

        List<Transaction> flagged = repo.findAll().stream()
                .filter(t -> "FLAGGED".equalsIgnoreCase(t.getStatus()))
                .toList();

        System.out.println("\n--- Flagged Transactions ---");

        if (flagged.isEmpty()) {
            System.out.println("No flagged transactions.");
        }

        for (Transaction t : flagged) {
            System.out.println(
                    "TXN ID: " + t.getTransactionId() +
                            ", Card: **** **** **** " +
                            t.getCard().getCardNumber().substring(12) +
                            ", Amount: ₹" + t.getAmount() +
                            ", Location: " + t.getLocation().getCity()
            );
        }
    }
}