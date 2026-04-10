package org.example.state;

public class ClearedState implements TransactionState {

    @Override
    public String getName() {
        return "SUCCESS"; // aligned with Transaction status
    }

    @Override
    public void handle(TransactionContext context) {
        System.out.println("Transaction approved.");
        System.out.println("Transaction processed successfully!");
    }
}