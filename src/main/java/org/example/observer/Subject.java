package org.example.observer;

import org.example.model.Transaction;

public interface Subject {
    void addObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObservers(String status, Transaction transaction);
}