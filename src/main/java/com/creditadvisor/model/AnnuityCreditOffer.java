package com.creditadvisor.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

/**
 * A credit offer repaid in equal monthly installments (annuity payments),
 * the most common repayment scheme for consumer and mortgage loans.
 */
public final class AnnuityCreditOffer extends CreditOffer {

    private static final int MONTHS_IN_YEAR = 12;
    private static final int MONEY_SCALE = 2;

    public AnnuityCreditOffer(Bank bank,
                               String offerName,
                               CreditPurpose purpose,
                               BigDecimal principalAmount,
                               BigDecimal annualInterestRatePercent,
                               int termInMonths) {
        super(bank, offerName, purpose, principalAmount, annualInterestRatePercent, termInMonths);
    }

    @Override
    public List<BigDecimal> calculatePaymentSchedule() {
        return Collections.nCopies(getTermInMonths(), calculateMonthlyPayment());
    }

    /**
     * The constant monthly payment amount, calculated using the standard
     * annuity formula {@code P * r * (1 + r)^n / ((1 + r)^n - 1)}, where
     * {@code r} is the monthly interest rate and {@code n} is the term in
     * months. When the rate is zero, the principal is simply split evenly.
     */
    public BigDecimal calculateMonthlyPayment() {
        double principal = getPrincipalAmount().doubleValue();
        double monthlyRate = getAnnualInterestRatePercent().doubleValue() / 100 / MONTHS_IN_YEAR;
        int termInMonths = getTermInMonths();

        if (monthlyRate == 0) {
            return BigDecimal.valueOf(principal / termInMonths)
                    .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        }

        double growthFactor = Math.pow(1 + monthlyRate, termInMonths);
        double payment = principal * monthlyRate * growthFactor / (growthFactor - 1);

        return BigDecimal.valueOf(payment).setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }
}
