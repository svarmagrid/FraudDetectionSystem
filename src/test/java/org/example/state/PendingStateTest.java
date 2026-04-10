package org.example.state;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class PendingStateTest {

    @Test
    void testGetNameShouldReturnPending() {
        PendingState state = new PendingState();
        String result = state.getName();
        assertEquals("PENDING", result);
    }

    @Test
    void testHandleMethod() {
        PendingState state = new PendingState();
        TransactionContext context = new TransactionContext(null);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        System.setOut(new PrintStream(outputStream));

        state.handle(context);
        System.setOut(originalOutput);

        String output = outputStream.toString();

        assertTrue(output.contains("Transaction under review..."));
    }
}