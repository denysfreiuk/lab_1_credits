package com.creditadvisor.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreditLineOfferTest {

    private static final Bank PRIVAT_BANK = new Bank("PrivatBank", "PB-001", 5);

    @Test
    void paymentScheduleIsInterestOnlyExceptForTheBalloonLastPayment() {
        CreditLineOffer offer = new CreditLineOffer(PRIVAT_BANK, "Business Credit Line", CreditPurpose.BUSINESS,
                new BigDecimal("200000"), new BigDecimal("15"), 4);

        List<BigDecimal> schedule = offer.calculatePaymentSchedule();

        assertEquals(4, schedule.size());
        assertEquals(0, new BigDecimal("2500.00").compareTo(schedule.get(0)));
        assertEquals(0, new BigDecimal("2500.00").compareTo(schedule.get(1)));
        assertEquals(0, new BigDecimal("2500.00").compareTo(schedule.get(2)));
        assertEquals(0, new BigDecimal("202500.00").compareTo(schedule.get(3)));
    }

    @Test
    void calculateTotalCostAndOverpaymentMatchExpectedValues() {
        CreditLineOffer offer = new CreditLineOffer(PRIVAT_BANK, "Business Credit Line", CreditPurpose.BUSINESS,
                new BigDecimal("200000"), new BigDecimal("15"), 4);

        assertEquals(0, new BigDecimal("210000.00").compareTo(offer.calculateTotalCost()));
        assertEquals(0, new BigDecimal("10000.00").compareTo(offer.calculateOverpaymentAmount()));
    }

    @Test
    void getCreditLimitIsAnAliasForPrincipalAmount() {
        CreditLineOffer offer = new CreditLineOffer(PRIVAT_BANK, "Business Credit Line", CreditPurpose.BUSINESS,
                new BigDecimal("200000"), new BigDecimal("15"), 4);

        assertEquals(0, offer.getPrincipalAmount().compareTo(offer.getCreditLimit()));
    }

    @Test
    void increaseLimitReturnsNewOfferWithIncreasedLimitAndSameOtherFields() {
        CreditLineOffer original = new CreditLineOffer(PRIVAT_BANK, "Business Credit Line", CreditPurpose.BUSINESS,
                new BigDecimal("200000"), new BigDecimal("15"), 4);

        CreditLineOffer increased = original.increaseLimit(new BigDecimal("50000"));

        assertEquals(0, new BigDecimal("250000").compareTo(increased.getCreditLimit()));
        assertEquals(original.getBank(), increased.getBank());
        assertEquals(original.getOfferName(), increased.getOfferName());
        assertEquals(original.getPurpose(), increased.getPurpose());
        assertEquals(0, original.getAnnualInterestRatePercent().compareTo(increased.getAnnualInterestRatePercent()));
        assertEquals(original.getTermInMonths(), increased.getTermInMonths());
    }

    @Test
    void increaseLimitDoesNotModifyTheOriginalOffer() {
        CreditLineOffer original = new CreditLineOffer(PRIVAT_BANK, "Business Credit Line", CreditPurpose.BUSINESS,
                new BigDecimal("200000"), new BigDecimal("15"), 4);

        original.increaseLimit(new BigDecimal("50000"));

        assertEquals(0, new BigDecimal("200000").compareTo(original.getCreditLimit()));
    }

    @Test
    void increaseLimitRejectsNonPositiveAmount() {
        CreditLineOffer offer = new CreditLineOffer(PRIVAT_BANK, "Business Credit Line", CreditPurpose.BUSINESS,
                new BigDecimal("200000"), new BigDecimal("15"), 4);

        assertThrows(IllegalArgumentException.class, () -> offer.increaseLimit(BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> offer.increaseLimit(new BigDecimal("-100")));
        assertThrows(IllegalArgumentException.class, () -> offer.increaseLimit(null));
    }
}
