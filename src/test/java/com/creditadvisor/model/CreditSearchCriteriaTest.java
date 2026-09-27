package com.creditadvisor.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreditSearchCriteriaTest {

    private static final Bank PRIVAT_BANK = new Bank("PrivatBank", "PB-001", 5);

    @Test
    void constructorStoresAllFields() {
        CreditSearchCriteria criteria = new CreditSearchCriteria(
                CreditPurpose.AUTO, new BigDecimal("50000"), 24);

        assertEquals(CreditPurpose.AUTO, criteria.getPurpose());
        assertEquals(0, new BigDecimal("50000").compareTo(criteria.getDesiredAmount()));
        assertEquals(24, criteria.getDesiredTermInMonths());
    }

    @Test
    void constructorRejectsNullPurpose() {
        assertThrows(IllegalArgumentException.class, () ->
                new CreditSearchCriteria(null, new BigDecimal("50000"), 24));
    }

    @Test
    void constructorRejectsNonPositiveAmount() {
        assertThrows(IllegalArgumentException.class, () ->
                new CreditSearchCriteria(CreditPurpose.AUTO, BigDecimal.ZERO, 24));
    }

    @Test
    void constructorRejectsNonPositiveTerm() {
        assertThrows(IllegalArgumentException.class, () ->
                new CreditSearchCriteria(CreditPurpose.AUTO, new BigDecimal("50000"), 0));
    }

    @Test
    void isSatisfiedByReturnsTrueWhenPurposeTermAndAmountMatch() {
        CreditSearchCriteria criteria = new CreditSearchCriteria(CreditPurpose.AUTO, new BigDecimal("50000"), 24);
        CreditOffer offer = new AnnuityCreditOffer(PRIVAT_BANK, "Auto Standard", CreditPurpose.AUTO,
                new BigDecimal("60000"), new BigDecimal("15"), 24);

        assertTrue(criteria.isSatisfiedBy(offer));
    }

    @Test
    void isSatisfiedByReturnsFalseWhenPurposeDiffers() {
        CreditSearchCriteria criteria = new CreditSearchCriteria(CreditPurpose.AUTO, new BigDecimal("50000"), 24);
        CreditOffer offer = new AnnuityCreditOffer(PRIVAT_BANK, "Mortgage Standard", CreditPurpose.MORTGAGE,
                new BigDecimal("60000"), new BigDecimal("15"), 24);

        assertFalse(criteria.isSatisfiedBy(offer));
    }

    @Test
    void isSatisfiedByReturnsFalseWhenTermDiffers() {
        CreditSearchCriteria criteria = new CreditSearchCriteria(CreditPurpose.AUTO, new BigDecimal("50000"), 24);
        CreditOffer offer = new AnnuityCreditOffer(PRIVAT_BANK, "Auto Standard", CreditPurpose.AUTO,
                new BigDecimal("60000"), new BigDecimal("15"), 12);

        assertFalse(criteria.isSatisfiedBy(offer));
    }

    @Test
    void isSatisfiedByReturnsFalseWhenOfferAmountIsTooSmall() {
        CreditSearchCriteria criteria = new CreditSearchCriteria(CreditPurpose.AUTO, new BigDecimal("50000"), 24);
        CreditOffer offer = new AnnuityCreditOffer(PRIVAT_BANK, "Auto Standard", CreditPurpose.AUTO,
                new BigDecimal("40000"), new BigDecimal("15"), 24);

        assertFalse(criteria.isSatisfiedBy(offer));
    }
}
