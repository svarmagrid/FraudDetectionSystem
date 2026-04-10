package org.example.state;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class ClearedStateTest {

    @Test
    void testGetName_shouldReturnSuccess() {
        ClearedState state = new ClearedState();

        String result = state.getName();

        assertEquals("SUCCESS", result);
    }

    @Test
    void testHandle_shouldPrintSuccessMessages() {

        // Arrange
        ClearedState state = new ClearedState();
        TransactionContext context = new TransactionContext(null);

        // Capture console output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        // Act
        state.handle(context);

        // Restore System.out
        System.setOut(originalOut);

        String output = outputStream.toString();

        // Assert
        assertTrue(output.contains("Transaction approved."));
        assertTrue(output.contains("Transaction processed successfully!"));
    }
}