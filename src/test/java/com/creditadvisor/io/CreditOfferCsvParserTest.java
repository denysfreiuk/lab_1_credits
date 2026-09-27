package com.creditadvisor.io;

import com.creditadvisor.model.AnnuityCreditOffer;
import com.creditadvisor.model.CreditLineOffer;
import com.creditadvisor.model.CreditOffer;
import com.creditadvisor.model.CreditPurpose;
import com.creditadvisor.model.DifferentiatedCreditOffer;
import com.creditadvisor.model.feature.CreditLineExtendable;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreditOfferCsvParserTest {

    private static final String HEADER = "type,bankName,bankCode,bankReliabilityRating,offerName,"
            + "purpose,principalAmount,annualInterestRatePercent,termInMonths";

    private final CreditOfferCsvParser parser = new CreditOfferCsvParser();

    @Test
    void parseLineBuildsAnnuityCreditOffer() {
        CreditOffer offer = parser.parseLine("ANNUITY,PrivatBank,PB-001,5,Auto Standard,AUTO,60000,15,24");

        AnnuityCreditOffer annuityOffer = assertInstanceOf(AnnuityCreditOffer.class, offer);
        assertEquals("PrivatBank", annuityOffer.getBank().getName());
        assertEquals("PB-001", annuityOffer.getBank().getBankCode());
        assertEquals(5, annuityOffer.getBank().getReliabilityRating());
        assertEquals("Auto Standard", annuityOffer.getOfferName());
        assertEquals(CreditPurpose.AUTO, annuityOffer.getPurpose());
        assertEquals(0, new BigDecimal("60000").compareTo(annuityOffer.getPrincipalAmount()));
        assertEquals(0, new BigDecimal("15").compareTo(annuityOffer.getAnnualInterestRatePercent()));
        assertEquals(24, annuityOffer.getTermInMonths());
    }

    @Test
    void parseLineBuildsDifferentiatedCreditOffer() {
        CreditOffer offer = parser.parseLine(
                "DIFFERENTIATED,Oschadbank,OB-002,5,Mortgage Classic,MORTGAGE,1200000,13.9,180");

        assertInstanceOf(DifferentiatedCreditOffer.class, offer);
    }

    @Test
    void parseLineBuildsCreditLineOfferImplementingCreditLineExtendable() {
        CreditOffer offer = parser.parseLine("CREDIT_LINE,UkrSibbank,US-006,4,Business Line,BUSINESS,2000000,19.5,12");

        assertInstanceOf(CreditLineOffer.class, offer);
        assertInstanceOf(CreditLineExtendable.class, offer);
    }

    @Test
    void parseLineRejectsUnknownOfferType() {
        assertThrows(IllegalArgumentException.class, () ->
                parser.parseLine("UNKNOWN,PrivatBank,PB-001,5,Auto Standard,AUTO,60000,15,24"));
    }

    @Test
    void parseLineRejectsWrongColumnCount() {
        assertThrows(IllegalArgumentException.class, () ->
                parser.parseLine("ANNUITY,PrivatBank,PB-001,5,Auto Standard,AUTO,60000,15"));
    }

    @Test
    void parseLineRejectsUnknownCreditPurpose() {
        assertThrows(IllegalArgumentException.class, () ->
                parser.parseLine("ANNUITY,PrivatBank,PB-001,5,Auto Standard,VACATION,60000,15,24"));
    }

    @Test
    void parseSkipsHeaderAndBlankLines() {
        List<CreditOffer> offers = parser.parse(List.of(
                HEADER,
                "ANNUITY,PrivatBank,PB-001,5,Auto Standard,AUTO,60000,15,24",
                "",
                "DIFFERENTIATED,Oschadbank,OB-002,5,Mortgage Classic,MORTGAGE,1200000,13.9,180"));

        assertEquals(2, offers.size());
    }

    @Test
    void bundledDataFileParsesWithoutErrors() throws IOException {
        List<String> lines;
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("credit-offers.csv")) {
            assertTrue(input != null, "credit-offers.csv must be present on the classpath");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
                lines = reader.lines().toList();
            }
        }

        List<CreditOffer> offers = parser.parse(lines);

        assertEquals(10, offers.size());
    }
}
