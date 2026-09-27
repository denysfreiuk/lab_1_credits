package com.creditadvisor.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnnuityCreditOfferTest {

    private static final Bank PRIVAT_BANK = new Bank("PrivatBank", "PB-001", 5);

    @Test
    void calculateMonthlyPaymentMatchesStandardAnnuityFormula() {
        AnnuityCreditOffer offer = new AnnuityCreditOffer(PRIVAT_BANK, "Consumer Annuity", CreditPurpose.CONSUMER,
                new BigDecimal("100000"), new BigDecimal("12"), 12);

        assertEquals(0, new BigDecimal("8884.88").compareTo(offer.calculateMonthlyPayment()));
    }

    @Test
    void calculateMonthlyPaymentSplitsPrincipalEvenlyWhenRateIsZero() {
        AnnuityCreditOffer offer = new AnnuityCreditOffer(PRIVAT_BANK, "Zero Rate Promo", CreditPurpose.CONSUMER,
                new BigDecimal("12000"), BigDecimal.ZERO, 12);

        assertEquals(0, new BigDecimal("1000.00").compareTo(offer.calculateMonthlyPayment()));
    }

    @Test
    void paymentScheduleRepeatsTheSameMonthlyPaymentForEveryMonth() {
        AnnuityCreditOffer offer = new AnnuityCreditOffer(PRIVAT_BANK, "Auto Annuity", CreditPurpose.AUTO,
                new BigDecimal("50000"), new BigDecimal("18"), 6);

        List<BigDecimal> schedule = offer.calculatePaymentSchedule();

        assertEquals(6, schedule.size());
        for (BigDecimal payment : schedule) {
            assertEquals(0, offer.calculateMonthlyPayment().compareTo(payment));
        }
    }

    @Test
    void calculateTotalCostEqualsMonthlyPaymentTimesTermForAnnuity() {
        AnnuityCreditOffer offer = new AnnuityCreditOffer(PRIVAT_BANK, "Auto Annuity", CreditPurpose.AUTO,
                new BigDecimal("50000"), new BigDecimal("18"), 6);

        assertEquals(0, new BigDecimal("52657.56").compareTo(offer.calculateTotalCost()));
    }

    @Test
    void calculateOverpaymentAmountIsPositiveWhenRateIsPositive() {
        AnnuityCreditOffer offer = new AnnuityCreditOffer(PRIVAT_BANK, "Auto Annuity", CreditPurpose.AUTO,
                new BigDecimal("50000"), new BigDecimal("18"), 6);

        assertEquals(0, new BigDecimal("2657.56").compareTo(offer.calculateOverpaymentAmount()));
        assertTrue(offer.calculateOverpaymentAmount().signum() > 0);
    }
}
