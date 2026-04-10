package org.example.model;

import java.time.LocalDateTime;

public class Transaction {
    private String transactionId;
    private Card card;
    private double amount;
    private String currency;
    private LocalDateTime timestamp;
    private String status = "PENDING";
    private String paymentChannel = "CARD";
    private Merchant merchant;
    private Location location;  // SINGLE LOCATION

    public Transaction(String transactionId, Card card, double amount,
                       String currency, LocalDateTime timestamp, String status,
                       String paymentChannel, Merchant merchant, Location location) {
        this.transactionId = transactionId;
        this.card = card;
        this.amount = amount;
        this.currency = currency;
        this.timestamp = timestamp;
        this.status = status;
        this.paymentChannel = paymentChannel;
        this.merchant = merchant;
        this.location = location;
    }

    public Transaction(String transactionId, Card card, double amount, LocalDateTime timestamp) {
        this(transactionId, card, amount, "INR", timestamp, "PENDING", "ONLINE", null, null);
    }

    public String getTransactionId() { return transactionId; }
    public Card getCard() { return card; }
    public double getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPaymentChannel() { return paymentChannel; }
    public Merchant getMerchant() { return merchant; }
    public void setMerchant(Merchant merchant) { this.merchant = merchant; }
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
}