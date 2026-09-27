package com.creditadvisor.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * A credit offer repaid with differentiated payments: the principal is
 * repaid in equal portions each month, while interest is charged on the
 * outstanding balance, which shrinks every month. Unlike an annuity, the
 * monthly payment therefore decreases over the term.
 */
public final class DifferentiatedCreditOffer extends CreditOffer {

    private static final int MONTHS_IN_YEAR = 12;
    private static final int MONEY_SCALE = 2;

    public DifferentiatedCreditOffer(Bank bank,
                                      String offerName,
                                      CreditPurpose purpose,
                                      BigDecimal principalAmount,
                                      BigDecimal annualInterestRatePercent,
                                      int termInMonths) {
        super(bank, offerName, purpose, principalAmount, annualInterestRatePercent, termInMonths);
    }

    @Override
    public List<BigDecimal> calculatePaymentSchedule() {
        int termInMonths = getTermInMonths();
        BigDecimal principal = getPrincipalAmount();
        BigDecimal monthlyRate = getAnnualInterestRatePercent()
                .divide(BigDecimal.valueOf(100L * MONTHS_IN_YEAR), MathContext.DECIMAL64);
        BigDecimal principalPortion = principal.divide(
                BigDecimal.valueOf(termInMonths), MONEY_SCALE, RoundingMode.HALF_UP);

        List<BigDecimal> schedule = new ArrayList<>(termInMonths);
        BigDecimal remainingBalance = principal;
        BigDecimal principalRepaidSoFar = BigDecimal.ZERO;

        for (int month = 1; month <= termInMonths; month++) {
            // The last installment absorbs the rounding remainder so the
            // principal portions always sum up to exactly the loan amount.
            BigDecimal currentPrincipalPortion = month == termInMonths
                    ? principal.subtract(principalRepaidSoFar)
                    : principalPortion;

            BigDecimal interestForMonth = remainingBalance.multiply(monthlyRate)
                    .setScale(MONEY_SCALE, RoundingMode.HALF_UP);

            schedule.add(currentPrincipalPortion.add(interestForMonth));

            remainingBalance = remainingBalance.subtract(currentPrincipalPortion);
            principalRepaidSoFar = principalRepaidSoFar.add(currentPrincipalPortion);
        }

        return List.copyOf(schedule);
    }
}
