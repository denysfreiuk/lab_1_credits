package com.creditadvisor.console;

import com.creditadvisor.model.AnnuityCreditOffer;
import com.creditadvisor.model.Bank;
import com.creditadvisor.model.CreditOffer;
import com.creditadvisor.model.CreditPurpose;
import com.creditadvisor.repository.CreditOfferRepository;
import com.creditadvisor.service.CreditAdvisorService;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreditAdvisorConsoleAppTest {

    private static final Bank PRIVAT_BANK = new Bank("PrivatBank", "PB-001", 5);

    @Test
    void runPrintsMatchingAffordableOffersRankedFromBest() {
        CreditOffer autoOffer = new AnnuityCreditOffer(PRIVAT_BANK, "Auto Standard", CreditPurpose.AUTO,
                new BigDecimal("60000"), new BigDecimal("10"), 24);
        CreditOfferRepository repository = () -> List.of(autoOffer);
        CreditAdvisorService service = new CreditAdvisorService(repository);

        String input = String.join(System.lineSeparator(),
                "2",                 // AUTO
                "50000",             // desired amount
                "24",                // desired term
                "Ivan Petrenko",     // full name
                "50000") + System.lineSeparator(); // monthly income
        ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();

        CreditAdvisorConsoleApp app = new CreditAdvisorConsoleApp(service,
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(outputBuffer, true, StandardCharsets.UTF_8));

        app.run();

        String output = outputBuffer.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("PrivatBank"));
        assertTrue(output.contains("Auto Standard"));
        assertTrue(output.contains("1."));
    }

    @Test
    void runPrintsNoOffersMessageWhenNothingMatches() {
        CreditOffer mortgageOffer = new AnnuityCreditOffer(PRIVAT_BANK, "Mortgage Standard", CreditPurpose.MORTGAGE,
                new BigDecimal("60000"), new BigDecimal("10"), 24);
        CreditOfferRepository repository = () -> List.of(mortgageOffer);
        CreditAdvisorService service = new CreditAdvisorService(repository);

        String input = String.join(System.lineSeparator(),
                "2", "50000", "24", "Ivan Petrenko", "50000") + System.lineSeparator();
        ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();

        CreditAdvisorConsoleApp app = new CreditAdvisorConsoleApp(service,
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(outputBuffer, true, StandardCharsets.UTF_8));

        app.run();

        assertTrue(outputBuffer.toString(StandardCharsets.UTF_8).contains("не знайдено"));
    }

    @Test
    void runRejectsAnOutOfRangePurposeChoice() {
        CreditOfferRepository repository = List::of;
        CreditAdvisorService service = new CreditAdvisorService(repository);

        String input = "9" + System.lineSeparator();
        CreditAdvisorConsoleApp app = new CreditAdvisorConsoleApp(service,
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8));

        assertThrows(IllegalArgumentException.class, app::run);
    }

    @Test
    void constructorRejectsNullService() {
        assertThrows(IllegalArgumentException.class, () -> new CreditAdvisorConsoleApp(
                null, new ByteArrayInputStream(new byte[0]), new PrintStream(new ByteArrayOutputStream())));
    }

    @Test
    void constructorRejectsNullStreams() {
        CreditAdvisorService service = new CreditAdvisorService(List::of);

        assertThrows(IllegalArgumentException.class, () -> new CreditAdvisorConsoleApp(
                service, null, new PrintStream(new ByteArrayOutputStream())));
        assertThrows(IllegalArgumentException.class, () -> new CreditAdvisorConsoleApp(
                service, new ByteArrayInputStream(new byte[0]), null));
    }
}
