package com.creditadvisor.model;

import com.creditadvisor.model.feature.EarlyRepayable;

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
public final class DifferentiatedCreditOffer extends CreditOffer implements EarlyRepayable {

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

    /**
     * {@inheritDoc}
     *
     * <p>Since the principal is repaid in equal portions, the outstanding
     * balance after {@code monthsAlreadyPaid} months is simply the
     * principal minus that many portions. The remaining interest, with and
     * without the early repayment, is then read off a temporary
     * differentiated offer built for the remaining term — reusing this
     * class's own schedule calculation.
     */
    @Override
    public BigDecimal calculateEarlyRepaymentSavings(int monthsAlreadyPaid, BigDecimal earlyRepaymentAmount) {
        EarlyRepayable.validateEarlyRepaymentArguments(monthsAlreadyPaid, getTermInMonths(), earlyRepaymentAmount);

        int remainingMonths = getTermInMonths() - monthsAlreadyPaid;
        BigDecimal remainingBalance = calculateRemainingBalanceAfter(monthsAlreadyPaid);

        BigDecimal interestRemainingWithoutEarlyRepayment = remainingTermOffer(remainingBalance, remainingMonths)
                .calculateOverpaymentAmount();

        BigDecimal reducedBalance = remainingBalance.subtract(earlyRepaymentAmount).max(BigDecimal.ZERO);
        BigDecimal interestRemainingAfterEarlyRepayment = reducedBalance.signum() == 0
                ? BigDecimal.ZERO
                : remainingTermOffer(reducedBalance, remainingMonths).calculateOverpaymentAmount();

        return interestRemainingWithoutEarlyRepayment.subtract(interestRemainingAfterEarlyRepayment)
                .max(BigDecimal.ZERO);
    }

    private BigDecimal calculateRemainingBalanceAfter(int monthsAlreadyPaid) {
        BigDecimal principalPortion = getPrincipalAmount().divide(
                BigDecimal.valueOf(getTermInMonths()), MONEY_SCALE, RoundingMode.HALF_UP);
        return getPrincipalAmount().subtract(principalPortion.multiply(BigDecimal.valueOf(monthsAlreadyPaid)));
    }

    private DifferentiatedCreditOffer remainingTermOffer(BigDecimal principal, int remainingMonths) {
        return new DifferentiatedCreditOffer(getBank(), getOfferName(), getPurpose(),
                principal, getAnnualInterestRatePercent(), remainingMonths);
    }
}
