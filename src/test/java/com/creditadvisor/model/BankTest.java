package com.creditadvisor.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BankTest {

    @Test
    void constructorStoresAllFields() {
        Bank bank = new Bank("PrivatBank", "PB-001", 5);

        assertEquals("PrivatBank", bank.getName());
        assertEquals("PB-001", bank.getBankCode());
        assertEquals(5, bank.getReliabilityRating());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void constructorRejectsBlankName(String blankName) {
        assertThrows(IllegalArgumentException.class,
                () -> new Bank(blankName, "PB-001", 3));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void constructorRejectsBlankBankCode(String blankCode) {
        assertThrows(IllegalArgumentException.class,
                () -> new Bank("PrivatBank", blankCode, 3));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 6, 100})
    void constructorRejectsRatingOutsideValidRange(int invalidRating) {
        assertThrows(IllegalArgumentException.class,
                () -> new Bank("PrivatBank", "PB-001", invalidRating));
    }

    @Test
    void banksWithSameCodeAreEqualRegardlessOfOtherFields() {
        Bank bank1 = new Bank("PrivatBank", "PB-001", 5);
        Bank bank2 = new Bank("PrivatBank Renamed", "PB-001", 3);

        assertEquals(bank1, bank2);
        assertEquals(bank1.hashCode(), bank2.hashCode());
    }

    @Test
    void banksWithDifferentCodesAreNotEqual() {
        Bank bank1 = new Bank("PrivatBank", "PB-001", 5);
        Bank bank2 = new Bank("Oschadbank", "OB-002", 5);

        assertNotEquals(bank1, bank2);
    }
}
