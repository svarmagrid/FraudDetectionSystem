package org.example.state;

public interface TransactionState {
    String getName();
    void handle(TransactionContext context);
}