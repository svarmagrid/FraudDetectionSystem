package org.example.usecase;

import org.example.model.Card;
import org.example.repository.CardRepository;

import java.time.LocalDate;
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

        System.out.print("Enter CVV: ");
        String cvv = scanner.nextLine();

        System.out.print("Enter Expiry Date (YYYY-MM-DD): ");
        LocalDate expiryDate = LocalDate.parse(scanner.nextLine());

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

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
}