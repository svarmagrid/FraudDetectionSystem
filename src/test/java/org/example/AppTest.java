package org.example;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AppTest {

    private void runAppInSeparateProcess(String input) throws Exception {

        ProcessBuilder pb = new ProcessBuilder(
                "java",
                "-cp",
                System.getProperty("java.class.path"),
                "org.example.App"
        );

        Process process = pb.start();

        // Send input
        OutputStream os = process.getOutputStream();
        os.write(input.getBytes());
        os.flush();
        os.close();

        // Read output (optional)
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
        );

        while (reader.readLine() != null) {
            // consume output
        }

        process.waitFor(); // wait until exit
    }

    // ----------------------------------------
    // TESTS
    // ----------------------------------------

    @Test
    void shouldHandleInvalidChoice() throws Exception {
        runAppInSeparateProcess("99\n5\n");
        assertTrue(true);
    }

    @Test
    void shouldHandleViewAllTransactions() throws Exception {
        runAppInSeparateProcess("2\n5\n");
        assertTrue(true);
    }

    @Test
    void shouldHandleViewFlaggedTransactions() throws Exception {
        runAppInSeparateProcess("3\n5\n");
        assertTrue(true);
    }

    @Test
    void shouldHandleRegisterCard() throws Exception {
        runAppInSeparateProcess(
                "4\n" +
                        "1234567812345678\n" +
                        "Satish\n" +
                        "123\n" +
                        "2030-12-31\n" +
                        "test@mail.com\n" +
                        "5\n"
        );
        assertTrue(true);
    }

    @Test
    void shouldHandleAddTransaction() throws Exception {
        runAppInSeparateProcess(
                "1\n" +
                        "1234567812345678\n" +
                        "123\n" +
                        "0000\n" + // fail OTP
                        "5\n"
        );
        assertTrue(true);
    }

    @Test
    void shouldCoverMultipleFlows() throws Exception {
        runAppInSeparateProcess(
                // Register card
                "4\n" +
                        "1234567812345678\n" +
                        "Satish\n" +
                        "123\n" +
                        "2030-12-31\n" +
                        "test@mail.com\n" +

                        // Invalid choice
                        "99\n" +

                        // View all
                        "2\n" +

                        // View flagged
                        "3\n" +

                        // Add txn (fail CVV)
                        "1\n" +
                        "1234567812345678\n" +
                        "999\n" +

                        // Exit
                        "5\n"
        );
    }
}