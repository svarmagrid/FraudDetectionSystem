package org.example.state;

public class FlaggedState implements TransactionState {

    @Override
    public String getName() {
        return "FLAGGED";
    }

    @Override
    public void handle(TransactionContext context) {
        System.out.println("Transaction flagged!");
    }
}