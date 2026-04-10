package org.example.observer;

import org.example.model.Transaction;
import org.example.model.Location;

public class LoggingService implements Observer {

    @Override
    public void update(String status, Transaction t) {
        Location loc = t.getLocation();

        String locationInfo = (loc != null)
                ? loc.getCity() + ", " + loc.getState() + ", " + loc.getCountry()
                : "Unknown";

        System.out.println("[LOG] Transaction " + t.getTransactionId() +
                " → " + status +
                " | Location: " + locationInfo +
                " | Amount: " + t.getAmount());
    }
}