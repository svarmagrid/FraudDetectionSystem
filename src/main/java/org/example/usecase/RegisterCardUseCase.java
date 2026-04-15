package org.example.usecase;

import org.example.model.Card;
import org.example.repository.CardRepository;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class RegisterCardUseCase {

    private final CardRepository repo;

    public RegisterCardUseCase(CardRepository repo) {
        this.repo = repo;
    }

    public void execute(java.util.Scanner scanner) {

        System.out.print("Enter Card Number: ");
        String cardNumber = scanner.nextLine().replaceAll("\\s+", "");

        if (cardNumber.length() < 16) {
            System.out.println("Invalid card number");
            return;
        }

        if (repo.findByNumber(cardNumber).isPresent()) {
            System.out.println("Card already registered");
            return;
        }

        System.out.print("Enter Cardholder Name: ");
        String name = scanner.nextLine();

        while (!ValidationUtil.isValidName(name)){
            System.out.println("Invalid name. Name must only contain characters");
            System.out.print("Enter Cardholder Name: ");
            name = scanner.nextLine();
        }

        System.out.print("Enter CVV: ");
        String cvv = scanner.nextLine();
        while (!ValidationUtil.isValidCvv(cvv)) {
            System.out.println("Invalid CVV. CVV must contain exactly 3 digits.");
            System.out.print("Enter CVV: ");
            cvv = scanner.nextLine();
        }

        System.out.print("Enter Expiry Date (MM/yy or MM/yyyy): ");
        YearMonth expiryDate;
        while (true) {
            try {
                expiryDate = ValidationUtil.parseExpiryDate(scanner.nextLine());
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid expiry date. Please use MM/yy or MM/yyyy.");
                System.out.print("Enter Expiry Date (MM/yy or MM/yyyy): ");
            }
        }

        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        while (!ValidationUtil.isValidEmail(email)) {
            System.out.println("Invalid email format. Please enter a valid email (example: abc@gmail.com)");
            System.out.print("Enter Email: ");
            email = scanner.nextLine();
        }

        repo.save(new Card(0, cardNumber, name, expiryDate, null, null, cvv, email));

        System.out.println("Card registered successfully");
    }
}

class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );
    private static final Pattern CVV_PATTERN = Pattern.compile("^\\d{3}$");

    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z]+$");

    public static boolean isValidCvv(String cvv) {
        return cvv != null && CVV_PATTERN.matcher(cvv).matches();
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidName(String name){
        return name != null && NAME_PATTERN.matcher(name).matches();
    }

    public static YearMonth parseExpiryDate(String input) {
        if (input == null) {
            throw new DateTimeParseException("Expiry date is required", "", 0);
        }

        String normalized = input.trim();
        String[] parts = normalized.split("/");
        if (parts.length != 2) {
            throw new DateTimeParseException("Invalid expiry format", normalized, 0);
        }

        int month;
        int year;

        try {
            month = Integer.parseInt(parts[0]);
            year = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new DateTimeParseException("Invalid expiry format", normalized, 0, e);
        }

        if (month < 1 || month > 12) {
            throw new DateTimeParseException("Invalid expiry month", normalized, 0);
        }

        if (parts[1].length() == 2) {
            year += 2000;
        } else if (parts[1].length() != 4) {
            throw new DateTimeParseException("Invalid expiry year", normalized, 3);
        }

        return YearMonth.of(year, month);
    }

}
