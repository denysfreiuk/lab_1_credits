package com.creditadvisor.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ClientTest {

    @Test
    void constructorStoresFullNameAndIncome() {
        Client client = new Client("Ivan Petrenko", new BigDecimal("30000"));

        assertEquals("Ivan Petrenko", client.getFullName());
        assertEquals(0, new BigDecimal("30000").compareTo(client.getMonthlyIncome()));
    }

    @Test
    void constructorRejectsBlankFullName() {
        assertThrows(IllegalArgumentException.class, () -> new Client(" ", new BigDecimal("30000")));
    }

    @Test
    void constructorRejectsNonPositiveIncome() {
        assertThrows(IllegalArgumentException.class, () -> new Client("Ivan Petrenko", BigDecimal.ZERO));
    }

    @Test
    void calculateMaxAffordableMonthlyPaymentAppliesGivenRatio() {
        Client client = new Client("Ivan Petrenko", new BigDecimal("30000"));

        BigDecimal maxPayment = client.calculateMaxAffordableMonthlyPayment(new BigDecimal("40"));

        assertEquals(0, new BigDecimal("12000.00").compareTo(maxPayment));
    }

    @Test
    void calculateMaxAffordableMonthlyPaymentRejectsNonPositiveRatio() {
        Client client = new Client("Ivan Petrenko", new BigDecimal("30000"));

        assertThrows(IllegalArgumentException.class,
                () -> client.calculateMaxAffordableMonthlyPayment(BigDecimal.ZERO));
    }
}
