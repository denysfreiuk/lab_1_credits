package com.creditadvisor.model.feature;

import java.math.BigDecimal;

/**
 * A capability of a credit offer that supports early repayment: an extra
 * payment applied directly to the outstanding principal ahead of schedule,
 * which reduces the total interest the client pays for the rest of the term.
 */
public interface EarlyRepayable {

    /**
     * Calculates how much less total interest the client pays if, after
     * already having made {@code monthsAlreadyPaid} regular installments,
     * they apply one extra payment of {@code earlyRepaymentAmount} directly
     * to the outstanding principal, compared to continuing the original
     * schedule unchanged for the rest of the term.
     *
     * @param monthsAlreadyPaid    number of regular monthly payments already made;
     *                             must be between {@code 0} and {@code termInMonths - 1}
     * @param earlyRepaymentAmount extra amount applied to the outstanding principal;
     *                             must be positive
     * @return the amount of interest saved, never negative
     */
    BigDecimal calculateEarlyRepaymentSavings(int monthsAlreadyPaid, BigDecimal earlyRepaymentAmount);

    /**
     * Validates the arguments common to any {@link EarlyRepayable}
     * implementation, so every implementing class rejects invalid input the
     * same way.
     */
    static void validateEarlyRepaymentArguments(int monthsAlreadyPaid, int termInMonths,
                                                 BigDecimal earlyRepaymentAmount) {
        if (monthsAlreadyPaid < 0 || monthsAlreadyPaid >= termInMonths) {
            throw new IllegalArgumentException(
                    "Months already paid must be between 0 and " + (termInMonths - 1)
                            + ", but was " + monthsAlreadyPaid);
        }
        if (earlyRepaymentAmount == null || earlyRepaymentAmount.signum() <= 0) {
            throw new IllegalArgumentException("Early repayment amount must be positive");
        }
    }
}
