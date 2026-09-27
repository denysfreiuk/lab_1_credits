package com.creditadvisor.model.feature;

import java.math.BigDecimal;

/**
 * A capability of a credit offer that supports increasing its available
 * limit, typical for revolving credit lines used by businesses that need
 * flexible access to funds.
 */
public interface CreditLineExtendable {

    /** Currently available credit limit. */
    BigDecimal getCreditLimit();

    /**
     * Returns a new offer representing this credit line with its limit
     * increased by {@code additionalLimitAmount}; the offer this method is
     * called on is left unchanged.
     *
     * @param additionalLimitAmount amount to add to the current limit, must be positive
     */
    CreditLineExtendable increaseLimit(BigDecimal additionalLimitAmount);
}
