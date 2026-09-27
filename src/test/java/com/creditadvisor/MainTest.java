package com.creditadvisor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;

    @AfterEach
    void restoreStandardStreams() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    void mainWiresRealRepositoryAndServiceAndPrintsAMatchingOffer() {
        String input = String.join(System.lineSeparator(),
                "3",                 // CONSUMER
                "50000",             // desired amount (Universal Bank offer is 100000, Oschadbank is 80000)
                "12",                // desired term (both Universal Bank 24 and Oschadbank 18 qualify)
                "Ivan Petrenko",
                "100000") + System.lineSeparator();
        ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();

        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(outputBuffer, true, StandardCharsets.UTF_8));

        Main.main(new String[0]);

        String output = outputBuffer.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Кредитний радник"));
        assertTrue(output.contains("Знайдено пропозицій"));
    }
}
