package org.example.observer;

import org.example.model.Transaction;

public interface Observer {
    void update(String status, Transaction transaction);
}