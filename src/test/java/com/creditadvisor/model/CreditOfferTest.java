package com.creditadvisor.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreditOfferTest {

    private static final Bank PRIVAT_BANK = new Bank("PrivatBank", "PB-001", 5);
    private static final Bank OSCHAD_BANK = new Bank("Oschadbank", "OB-002", 3);

    @Test
    void constructorStoresAllFields() {
        CreditOffer offer = fixedScheduleOffer(PRIVAT_BANK, "Mortgage Standard", CreditPurpose.MORTGAGE,
                new BigDecimal("100000"), new BigDecimal("12.5"), 24,
                List.of(new BigDecimal("5000"), new BigDecimal("5000")));

        assertEquals(PRIVAT_BANK, offer.getBank());
        assertEquals("Mortgage Standard", offer.getOfferName());
        assertEquals(CreditPurpose.MORTGAGE, offer.getPurpose());
        assertEquals(0, new BigDecimal("100000").compareTo(offer.getPrincipalAmount()));
        assertEquals(0, new BigDecimal("12.5").compareTo(offer.getAnnualInterestRatePercent()));
        assertEquals(24, offer.getTermInMonths());
    }

    @Test
    void constructorRejectsNullBank() {
        assertThrows(IllegalArgumentException.class, () ->
                fixedScheduleOffer(null, "Offer", CreditPurpose.AUTO,
                        new BigDecimal("1000"), new BigDecimal("10"), 12, List.of(new BigDecimal("100"))));
    }

    @Test
    void constructorRejectsBlankOfferName() {
        assertThrows(IllegalArgumentException.class, () ->
                fixedScheduleOffer(PRIVAT_BANK, " ", CreditPurpose.AUTO,
                        new BigDecimal("1000"), new BigDecimal("10"), 12, List.of(new BigDecimal("100"))));
    }

    @Test
    void constructorRejectsNonPositivePrincipal() {
        assertThrows(IllegalArgumentException.class, () ->
                fixedScheduleOffer(PRIVAT_BANK, "Offer", CreditPurpose.AUTO,
                        BigDecimal.ZERO, new BigDecimal("10"), 12, List.of(new BigDecimal("100"))));
    }

    @Test
    void constructorRejectsNegativeInterestRate() {
        assertThrows(IllegalArgumentException.class, () ->
                fixedScheduleOffer(PRIVAT_BANK, "Offer", CreditPurpose.AUTO,
                        new BigDecimal("1000"), new BigDecimal("-1"), 12, List.of(new BigDecimal("100"))));
    }

    @Test
    void constructorRejectsNonPositiveTerm() {
        assertThrows(IllegalArgumentException.class, () ->
                fixedScheduleOffer(PRIVAT_BANK, "Offer", CreditPurpose.AUTO,
                        new BigDecimal("1000"), new BigDecimal("10"), 0, List.of(new BigDecimal("100"))));
    }

    @Test
    void calculateTotalCostSumsPaymentSchedule() {
        CreditOffer offer = fixedScheduleOffer(PRIVAT_BANK, "Consumer Offer", CreditPurpose.CONSUMER,
                new BigDecimal("1000"), new BigDecimal("10"), 3,
                List.of(new BigDecimal("350"), new BigDecimal("350"), new BigDecimal("350")));

        assertEquals(0, new BigDecimal("1050").compareTo(offer.calculateTotalCost()));
    }

    @Test
    void calculateOverpaymentAmountIsTotalCostMinusPrincipal() {
        CreditOffer offer = fixedScheduleOffer(PRIVAT_BANK, "Consumer Offer", CreditPurpose.CONSUMER,
                new BigDecimal("1000"), new BigDecimal("10"), 3,
                List.of(new BigDecimal("350"), new BigDecimal("350"), new BigDecimal("350")));

        assertEquals(0, new BigDecimal("50").compareTo(offer.calculateOverpaymentAmount()));
    }

    @Test
    void offersOfSameTypeWithSameFieldsAreEqual() {
        CreditOffer offer1 = fixedScheduleOffer(PRIVAT_BANK, "Offer", CreditPurpose.AUTO,
                new BigDecimal("1000"), new BigDecimal("10"), 12, List.of(new BigDecimal("100")));
        CreditOffer offer2 = fixedScheduleOffer(PRIVAT_BANK, "Offer", CreditPurpose.AUTO,
                new BigDecimal("1000"), new BigDecimal("10"), 12, List.of(new BigDecimal("999")));

        assertEquals(offer1, offer2);
        assertEquals(offer1.hashCode(), offer2.hashCode());
    }

    @Test
    void offersOfDifferentConcreteTypesAreNotEqualEvenWithSameFields() {
        CreditOffer fixedOffer = fixedScheduleOffer(PRIVAT_BANK, "Offer", CreditPurpose.AUTO,
                new BigDecimal("1000"), new BigDecimal("10"), 12, List.of(new BigDecimal("100")));
        CreditOffer otherTypeOffer = new AnotherFixedScheduleCreditOffer(PRIVAT_BANK, "Offer", CreditPurpose.AUTO,
                new BigDecimal("1000"), new BigDecimal("10"), 12, List.of(new BigDecimal("100")));

        assertNotEquals(fixedOffer, otherTypeOffer);
    }

    @Test
    void compareToOrdersByTotalCostAscending() {
        CreditOffer cheaperOffer = fixedScheduleOffer(PRIVAT_BANK, "Cheap", CreditPurpose.AUTO,
                new BigDecimal("1000"), new BigDecimal("10"), 1, List.of(new BigDecimal("1050")));
        CreditOffer pricierOffer = fixedScheduleOffer(PRIVAT_BANK, "Pricey", CreditPurpose.AUTO,
                new BigDecimal("1000"), new BigDecimal("10"), 1, List.of(new BigDecimal("1200")));

        assertTrue(cheaperOffer.compareTo(pricierOffer) < 0);
        assertTrue(pricierOffer.compareTo(cheaperOffer) > 0);
    }

    @Test
    void compareToBreaksTieByPreferringMoreReliableBank() {
        CreditOffer fromReliableBank = fixedScheduleOffer(PRIVAT_BANK, "Offer A", CreditPurpose.AUTO,
                new BigDecimal("1000"), new BigDecimal("10"), 1, List.of(new BigDecimal("1050")));
        CreditOffer fromLessReliableBank = fixedScheduleOffer(OSCHAD_BANK, "Offer B", CreditPurpose.AUTO,
                new BigDecimal("1000"), new BigDecimal("10"), 1, List.of(new BigDecimal("1050")));

        assertTrue(fromReliableBank.compareTo(fromLessReliableBank) < 0);
    }

    @Test
    void toStringContainsKeyOfferInformation() {
        CreditOffer offer = fixedScheduleOffer(PRIVAT_BANK, "Consumer Offer", CreditPurpose.CONSUMER,
                new BigDecimal("1000"), new BigDecimal("10"), 3, List.of(new BigDecimal("350")));

        String text = offer.toString();

        assertTrue(text.contains("Consumer Offer"));
        assertTrue(text.contains("PrivatBank"));
    }

    private static CreditOffer fixedScheduleOffer(Bank bank, String offerName, CreditPurpose purpose,
                                                   BigDecimal principal, BigDecimal rate, int term,
                                                   List<BigDecimal> schedule) {
        return new FixedScheduleCreditOffer(bank, offerName, purpose, principal, rate, term, schedule);
    }

    /** Minimal concrete subclass used only to exercise the abstract {@link CreditOffer}. */
    private static class FixedScheduleCreditOffer extends CreditOffer {

        private final List<BigDecimal> schedule;

        FixedScheduleCreditOffer(Bank bank, String offerName, CreditPurpose purpose,
                                  BigDecimal principalAmount, BigDecimal annualInterestRatePercent,
                                  int termInMonths, List<BigDecimal> schedule) {
            super(bank, offerName, purpose, principalAmount, annualInterestRatePercent, termInMonths);
            this.schedule = schedule;
        }

        @Override
        public List<BigDecimal> calculatePaymentSchedule() {
            return schedule;
        }
    }

    /** A second, distinct concrete subclass used to test cross-subtype inequality. */
    private static final class AnotherFixedScheduleCreditOffer extends FixedScheduleCreditOffer {

        AnotherFixedScheduleCreditOffer(Bank bank, String offerName, CreditPurpose purpose,
                                         BigDecimal principalAmount, BigDecimal annualInterestRatePercent,
                                         int termInMonths, List<BigDecimal> schedule) {
            super(bank, offerName, purpose, principalAmount, annualInterestRatePercent, termInMonths, schedule);
        }
    }
}
