package com.creditadvisor.model;

import java.math.BigDecimal;

/**
 * What a client is looking for: a credit of a given purpose, for at least a
 * given amount, over a given term. Used to filter the catalog of available
 * {@link CreditOffer}s down to the ones actually relevant to the client.
 */
public final class CreditSearchCriteria {

    private final CreditPurpose purpose;
    private final BigDecimal desiredAmount;
    private final int desiredTermInMonths;

    public CreditSearchCriteria(CreditPurpose purpose, BigDecimal desiredAmount, int desiredTermInMonths) {
        if (purpose == null) {
            throw new IllegalArgumentException("Credit purpose must not be null");
        }
        if (desiredAmount == null || desiredAmount.signum() <= 0) {
            throw new IllegalArgumentException("Desired amount must be positive");
        }
        if (desiredTermInMonths <= 0) {
            throw new IllegalArgumentException("Desired term in months must be positive");
        }

        this.purpose = purpose;
        this.desiredAmount = desiredAmount;
        this.desiredTermInMonths = desiredTermInMonths;
    }

    public CreditPurpose getPurpose() {
        return purpose;
    }

    public BigDecimal getDesiredAmount() {
        return desiredAmount;
    }

    public int getDesiredTermInMonths() {
        return desiredTermInMonths;
    }

    /**
     * Whether the given offer matches this request: same purpose, the exact
     * requested term, and enough principal to cover the desired amount.
     */
    public boolean isSatisfiedBy(CreditOffer offer) {
        return offer.getPurpose() == purpose
                && offer.getTermInMonths() == desiredTermInMonths
                && offer.getPrincipalAmount().compareTo(desiredAmount) >= 0;
    }

    @Override
    public String toString() {
        return "CreditSearchCriteria{" +
                "purpose=" + purpose +
                ", desiredAmount=" + desiredAmount +
                ", desiredTermInMonths=" + desiredTermInMonths +
                '}';
    }
}
