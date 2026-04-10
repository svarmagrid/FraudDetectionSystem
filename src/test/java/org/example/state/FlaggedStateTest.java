package org.example.state;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class FlaggedStateTest {

    @Test
    void testGetName_ShouldReturnFlagged(){
        FlaggedState flaggedState = new FlaggedState();
        String result = flaggedState.getName();
        assertEquals("FLAGGED",result);
    }

    @Test
    void testHandleShouldPrintFlaggedMessage(){
        FlaggedState state = new FlaggedState();
        TransactionContext context = new TransactionContext(null);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        System.setOut(new PrintStream(outputStream));

        state.handle(context);
        System.setOut(originalOutput);

        String output = outputStream.toString();

        assertTrue(output.contains("Transaction flagged!"));
    }
}