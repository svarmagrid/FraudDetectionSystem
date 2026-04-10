package org.example.state;

import org.example.model.Transaction;
import org.example.observer.Observer;
import org.example.observer.Subject;

import java.util.ArrayList;
import java.util.List;

public class TransactionContext implements Subject {

    private TransactionState state;
    private final Transaction transaction;
    private final List<Observer> observers = new ArrayList<>();

    public TransactionContext(Transaction transaction) {
        this.transaction = transaction;
        this.state = new PendingState();
    }

    public void setState(TransactionState state) {
        this.state = state;


        transaction.setStatus(state.getName());


        notifyObservers(state.getName(), transaction);


        state.handle(this);
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public String getStateName() {
        return state.getName();
    }

    // Observer methods
    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(String status, Transaction transaction) {
        for (Observer o : observers) {
            o.update(status, transaction);
        }
    }
}