package com.creditadvisor.model.feature;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

class EarlyRepayableTest {

    @Test
    void validateEarlyRepaymentArgumentsRejectsNegativeMonthsAlreadyPaid() {
        assertThrows(IllegalArgumentException.class, () ->
                EarlyRepayable.validateEarlyRepaymentArguments(-1, 12, new BigDecimal("1000")));
    }

    @Test
    void validateEarlyRepaymentArgumentsRejectsMonthsAlreadyPaidEqualToTerm() {
        assertThrows(IllegalArgumentException.class, () ->
                EarlyRepayable.validateEarlyRepaymentArguments(12, 12, new BigDecimal("1000")));
    }

    @Test
    void validateEarlyRepaymentArgumentsRejectsNonPositiveAmount() {
        assertThrows(IllegalArgumentException.class, () ->
                EarlyRepayable.validateEarlyRepaymentArguments(0, 12, BigDecimal.ZERO));
    }

    @Test
    void validateEarlyRepaymentArgumentsRejectsNullAmount() {
        assertThrows(IllegalArgumentException.class, () ->
                EarlyRepayable.validateEarlyRepaymentArguments(0, 12, null));
    }
}
