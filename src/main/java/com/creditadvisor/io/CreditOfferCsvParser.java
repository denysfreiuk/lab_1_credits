package com.creditadvisor.io;

import com.creditadvisor.model.AnnuityCreditOffer;
import com.creditadvisor.model.Bank;
import com.creditadvisor.model.CreditLineOffer;
import com.creditadvisor.model.CreditOffer;
import com.creditadvisor.model.CreditPurpose;
import com.creditadvisor.model.DifferentiatedCreditOffer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses the credit offer initialization data file: a CSV with a header row
 * followed by one row per offer, in the form
 * {@code type,bankName,bankCode,bankReliabilityRating,offerName,}
 * {@code purpose,principalAmount,annualInterestRatePercent,termInMonths}.
 */
public final class CreditOfferCsvParser {

    private static final String COLUMN_SEPARATOR = ",";
    private static final int EXPECTED_COLUMN_COUNT = 9;

    /**
     * Parses every data row into a {@link CreditOffer}, skipping the header
     * (the first line) and any blank lines.
     */
    public List<CreditOffer> parse(List<String> csvLines) {
        List<CreditOffer> offers = new ArrayList<>();
        for (int i = 1; i < csvLines.size(); i++) {
            String line = csvLines.get(i);
            if (!line.isBlank()) {
                offers.add(parseLine(line));
            }
        }
        return offers;
    }

    /** Parses a single, non-header CSV row into the matching {@link CreditOffer} subtype. */
    public CreditOffer parseLine(String line) {
        String[] columns = line.split(COLUMN_SEPARATOR, -1);
        if (columns.length != EXPECTED_COLUMN_COUNT) {
            throw new IllegalArgumentException(
                    "Expected " + EXPECTED_COLUMN_COUNT + " columns but found " + columns.length
                            + " in line: " + line);
        }

        try {
            String offerType = columns[0].trim();
            Bank bank = new Bank(columns[1].trim(), columns[2].trim(), Integer.parseInt(columns[3].trim()));
            String offerName = columns[4].trim();
            CreditPurpose purpose = CreditPurpose.valueOf(columns[5].trim());
            BigDecimal principalAmount = new BigDecimal(columns[6].trim());
            BigDecimal annualInterestRatePercent = new BigDecimal(columns[7].trim());
            int termInMonths = Integer.parseInt(columns[8].trim());

            return switch (offerType) {
                case "ANNUITY" -> new AnnuityCreditOffer(
                        bank, offerName, purpose, principalAmount, annualInterestRatePercent, termInMonths);
                case "DIFFERENTIATED" -> new DifferentiatedCreditOffer(
                        bank, offerName, purpose, principalAmount, annualInterestRatePercent, termInMonths);
                case "CREDIT_LINE" -> new CreditLineOffer(
                        bank, offerName, purpose, principalAmount, annualInterestRatePercent, termInMonths);
                default -> throw new IllegalArgumentException("Unknown offer type: " + offerType);
            };
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to parse credit offer line: " + line, e);
        }
    }
}
