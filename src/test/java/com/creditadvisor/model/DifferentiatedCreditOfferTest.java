package com.creditadvisor.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DifferentiatedCreditOfferTest {

    private static final Bank PRIVAT_BANK = new Bank("PrivatBank", "PB-001", 5);

    @Test
    void paymentScheduleDecreasesEveryMonthAsBalanceShrinks() {
        DifferentiatedCreditOffer offer = new DifferentiatedCreditOffer(PRIVAT_BANK, "Mortgage Differentiated",
                CreditPurpose.MORTGAGE, new BigDecimal("120000"), new BigDecimal("12"), 12);

        List<BigDecimal> schedule = offer.calculatePaymentSchedule();

        assertEquals(12, schedule.size());
        assertEquals(0, new BigDecimal("11200.00").compareTo(schedule.get(0)));
        assertEquals(0, new BigDecimal("11100.00").compareTo(schedule.get(1)));
        assertEquals(0, new BigDecimal("10100.00").compareTo(schedule.get(11)));

        for (int i = 1; i < schedule.size(); i++) {
            assertTrue(schedule.get(i).compareTo(schedule.get(i - 1)) < 0,
                    "payment " + i + " must be smaller than the previous one");
        }
    }

    @Test
    void calculateTotalCostAndOverpaymentMatchExpectedValues() {
        DifferentiatedCreditOffer offer = new DifferentiatedCreditOffer(PRIVAT_BANK, "Mortgage Differentiated",
                CreditPurpose.MORTGAGE, new BigDecimal("120000"), new BigDecimal("12"), 12);

        assertEquals(0, new BigDecimal("127800.00").compareTo(offer.calculateTotalCost()));
        assertEquals(0, new BigDecimal("7800.00").compareTo(offer.calculateOverpaymentAmount()));
    }

    @Test
    void lastInstallmentAbsorbsThePrincipalRoundingRemainder() {
        DifferentiatedCreditOffer offer = new DifferentiatedCreditOffer(PRIVAT_BANK, "Zero Rate Promo",
                CreditPurpose.CONSUMER, new BigDecimal("10000"), BigDecimal.ZERO, 3);

        List<BigDecimal> schedule = offer.calculatePaymentSchedule();

        assertEquals(0, new BigDecimal("3333.33").compareTo(schedule.get(0)));
        assertEquals(0, new BigDecimal("3333.33").compareTo(schedule.get(1)));
        assertEquals(0, new BigDecimal("3333.34").compareTo(schedule.get(2)));
        assertEquals(0, new BigDecimal("10000.00").compareTo(offer.calculateTotalCost()));
        assertEquals(0, BigDecimal.ZERO.compareTo(offer.calculateOverpaymentAmount()));
    }
}
