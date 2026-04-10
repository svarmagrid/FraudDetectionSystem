package org.example.observer;

import org.example.model.Transaction;

public class FraudAlertService implements Observer {

    @Override
    public void update(String status, Transaction t) {
        if ("FLAGGED".equalsIgnoreCase(status)) {
            System.out.println("[ALERT] Suspicious transaction for card "
                    + t.getCard().maskCardNumber(t.getCard().getCardNumber()) +
                    " | Amount: " + t.getAmount());
        }
    }
}