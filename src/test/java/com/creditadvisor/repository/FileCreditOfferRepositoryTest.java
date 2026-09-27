package com.creditadvisor.repository;

import com.creditadvisor.model.AnnuityCreditOffer;
import com.creditadvisor.model.CreditOffer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileCreditOfferRepositoryTest {

    private static final String HEADER = "type,bankName,bankCode,bankReliabilityRating,offerName,"
            + "purpose,principalAmount,annualInterestRatePercent,termInMonths";

    @TempDir
    Path tempDir;

    @Test
    void findAllReturnsOffersParsedFromAGivenFile() throws IOException {
        Path dataFile = tempDir.resolve("test-offers.csv");
        Files.writeString(dataFile, HEADER + System.lineSeparator()
                + "ANNUITY,PrivatBank,PB-001,5,Auto Standard,AUTO,60000,15,24" + System.lineSeparator(),
                StandardCharsets.UTF_8);

        CreditOfferRepository repository = new FileCreditOfferRepository(dataFile);
        List<CreditOffer> offers = repository.findAll();

        assertEquals(1, offers.size());
        assertEquals("Auto Standard", offers.get(0).getOfferName());
        assertTrue(offers.get(0) instanceof AnnuityCreditOffer);
    }

    @Test
    void findAllReturnsAllOffersFromMultipleRows() throws IOException {
        Path dataFile = tempDir.resolve("test-offers.csv");
        Files.writeString(dataFile, HEADER + System.lineSeparator()
                + "ANNUITY,PrivatBank,PB-001,5,Auto Standard,AUTO,60000,15,24" + System.lineSeparator()
                + "DIFFERENTIATED,Oschadbank,OB-002,5,Mortgage Classic,MORTGAGE,1200000,13.9,180"
                + System.lineSeparator(),
                StandardCharsets.UTF_8);

        CreditOfferRepository repository = new FileCreditOfferRepository(dataFile);

        assertEquals(2, repository.findAll().size());
    }

    @Test
    void constructorThrowsWhenFileDoesNotExist() {
        Path missingFile = tempDir.resolve("does-not-exist.csv");

        assertThrows(UncheckedIOException.class, () -> new FileCreditOfferRepository(missingFile));
    }

    @Test
    void constructorThrowsWhenClasspathResourceDoesNotExist() {
        assertThrows(IllegalArgumentException.class, () -> new FileCreditOfferRepository("does-not-exist.csv"));
    }

    @Test
    void bundledClasspathResourceLoadsExpectedNumberOfOffers() {
        CreditOfferRepository repository = new FileCreditOfferRepository("credit-offers.csv");

        assertEquals(10, repository.findAll().size());
    }
}
