package org.example.model;

import java.time.LocalDate;
import java.util.Objects;

public class Card {
    private int cardId;
    private String cardNumber;
    private String cardholderName;
    private LocalDate expiryDate;
    private String cardType;
    private String issuingBank;
    private String cvv;
    private String email;

    public Card(int cardId, String cardNumber, String cardholderName, LocalDate expiryDate,
                String cardType, String issuingBank, String cvv, String email) {
        this.cardId = cardId;
        this.cardNumber = cardNumber;
        this.cardholderName = cardholderName;
        this.expiryDate = expiryDate;
        this.cardType = cardType;
        this.issuingBank = issuingBank;
        this.cvv = cvv;
        this.email = email;
    }

    public int getCardId() { return cardId; }
    public String getCardNumber() { return cardNumber; }
    public String getCardholderName() { return cardholderName; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public String getCardType() { return cardType; }
    public String getIssuingBank() { return issuingBank; }
    public String getCvv() { return cvv; }

    public void setCvv(String cvv) { this.cvv = cvv; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "Card{" +
                "cardNumber='" + maskCardNumber(cardNumber) + '\'' +
                ", cardholderName='" + cardholderName + '\'' +
                ", expiryDate=" + expiryDate +
                ", cardType='" + cardType + '\'' +
                ", issuingBank='" + issuingBank + '\'' +
                '}';
    }

    public String maskCardNumber(String number) {
        if (number == null || number.length() < 4) return "****";
        return "**** **** **** " + number.substring(number.length() - 4);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card)) return false;
        Card card = (Card) o;
        return Objects.equals(cardNumber, card.cardNumber);
    }
}