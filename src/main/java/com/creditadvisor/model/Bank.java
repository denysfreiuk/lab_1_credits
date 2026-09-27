package com.creditadvisor.model;

import java.util.Objects;

/**
 * A bank that issues credit offers. Two banks are considered equal when
 * their {@link #getBankCode() bank codes} match, since the code is the
 * bank's unique business identifier.
 */
public final class Bank {

    private static final int MIN_RELIABILITY_RATING = 1;
    private static final int MAX_RELIABILITY_RATING = 5;

    private final String name;
    private final String bankCode;
    private final int reliabilityRating;

    /**
     * Creates a bank.
     *
     * @param name              full name of the bank, must not be blank
     * @param bankCode          unique bank identifier (e.g. MFO code), must not be blank
     * @param reliabilityRating reliability rating from {@value #MIN_RELIABILITY_RATING}
     *                          (low) to {@value #MAX_RELIABILITY_RATING} (high), used as
     *                          a tie-breaker when comparing offers with equal cost
     */
    public Bank(String name, String bankCode, int reliabilityRating) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Bank name must not be blank");
        }
        if (bankCode == null || bankCode.isBlank()) {
            throw new IllegalArgumentException("Bank code must not be blank");
        }
        if (reliabilityRating < MIN_RELIABILITY_RATING || reliabilityRating > MAX_RELIABILITY_RATING) {
            throw new IllegalArgumentException(
                    "Reliability rating must be between " + MIN_RELIABILITY_RATING
                            + " and " + MAX_RELIABILITY_RATING + ", but was " + reliabilityRating);
        }

        this.name = name;
        this.bankCode = bankCode;
        this.reliabilityRating = reliabilityRating;
    }

    public String getName() {
        return name;
    }

    public String getBankCode() {
        return bankCode;
    }

    public int getReliabilityRating() {
        return reliabilityRating;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Bank)) {
            return false;
        }
        Bank bank = (Bank) other;
        return bankCode.equals(bank.bankCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bankCode);
    }

    @Override
    public String toString() {
        return "Bank{" +
                "name='" + name + '\'' +
                ", bankCode='" + bankCode + '\'' +
                ", reliabilityRating=" + reliabilityRating +
                '}';
    }
}
