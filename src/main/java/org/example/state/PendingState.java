package org.example.state;

public class PendingState implements TransactionState {

    @Override
    public String getName() {
        return "PENDING";
    }

    @Override
    public void handle(TransactionContext context) {
        System.out.println("Transaction under review...");
    }
}