package org.example;

import io.github.cdimascio.dotenv.Dotenv;
import org.example.jdbc.JdbcUtil;
import org.example.observer.*;
import org.example.repository.*;
import org.example.service.*;
import org.example.usecase.*;

import java.util.Scanner;

public class App {

    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.load();
        Scanner scanner = new Scanner(System.in);

        JdbcUtil jdbc = new JdbcUtil(
                dotenv.get("DB_URL"),
                dotenv.get("DB_USER"),
                dotenv.get("DB_PASSWORD")
        );

        CardRepository cardRepo = new CardRepository(jdbc);
        TransactionRepository repo = new TransactionRepository(jdbc, cardRepo);
        FraudDetectionService fraudService = new FraudDetectionService(repo);

        Observer logger = new LoggingService();
        Observer alert = new FraudAlertService();
        EmailNotificationService emailService = new EmailNotificationService();
        OtpGenerator otpGenerator = new RandomOtpGenerator();

        // UseCases
        ProcessTransactionUseCase processUC =
                new ProcessTransactionUseCase(cardRepo, repo, fraudService, emailService, logger, alert, otpGenerator);

        ViewTransactionsUseCase viewUC = new ViewTransactionsUseCase(repo);
        ViewFlaggedTransactionsUseCase flaggedUC = new ViewFlaggedTransactionsUseCase(repo);
        RegisterCardUseCase registerUC = new RegisterCardUseCase(cardRepo);

        while (true) {
            System.out.println("\n===== FRAUD MONITOR MENU =====");
            System.out.println("1. Add Transaction");
            System.out.println("2. View All Transactions");
            System.out.println("3. View Flagged Transactions");
            System.out.println("4. Register Card");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> processUC.execute(scanner);
                case "2" -> viewUC.execute();
                case "3" -> flaggedUC.execute();
                case "4" -> registerUC.execute(scanner);
                case "5" -> System.exit(0);
                default -> System.out.println("Invalid choice");
            }
        }
    }
}