package com.creditadvisor.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CreditPurposeTest {

    @Test
    void everyPurposeHasNonBlankDisplayName() {
        for (CreditPurpose purpose : CreditPurpose.values()) {
            assertFalse(purpose.getDisplayName().isBlank(),
                    purpose + " must have a non-blank display name");
        }
    }

    @Test
    void mortgageHasExpectedDisplayName() {
        assertEquals("Іпотечний кредит", CreditPurpose.MORTGAGE.getDisplayName());
    }

    @Test
    void valueOfResolvesByEnumConstantName() {
        assertEquals(CreditPurpose.AUTO, CreditPurpose.valueOf("AUTO"));
    }
}
