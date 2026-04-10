package org.example.usecase;

import org.example.model.Transaction;
import org.example.repository.TransactionRepository;

import java.util.List;

public class ViewTransactionsUseCase {

    private final TransactionRepository repo;

    public ViewTransactionsUseCase(TransactionRepository repo) {
        this.repo = repo;
    }

    public void execute() {

        List<Transaction> allTxns = repo.findAll();

        System.out.println("\n--- All Transactions ---");

        if (allTxns.isEmpty()) {
            System.out.println("No transactions found.");
        }

        for (Transaction t : allTxns) {
            System.out.println(
                    "TXN ID: " + t.getTransactionId() +
                            ", Card: **** **** **** " +
                            t.getCard().getCardNumber().substring(12) +
                            ", Amount: ₹" + t.getAmount() +
                            ", Status: " + t.getStatus() +
                            ", Location: " + t.getLocation().getCity()
            );
        }
    }
}