package com.creditadvisor.model;

import com.creditadvisor.model.feature.EarlyRepayable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

/**
 * A credit offer repaid in equal monthly installments (annuity payments),
 * the most common repayment scheme for consumer and mortgage loans.
 */
public final class AnnuityCreditOffer extends CreditOffer implements EarlyRepayable {

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

    /**
     * {@inheritDoc}
     *
     * <p>The outstanding balance after {@code monthsAlreadyPaid} months is
     * found by replaying the amortization month by month. The remaining
     * interest, with and without the early repayment, is then obtained by
     * building a temporary annuity offer for the remaining term and reading
     * its {@link #calculateOverpaymentAmount()} — reusing the very formula
     * this class already implements.
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
        double balance = getPrincipalAmount().doubleValue();
        double monthlyRate = getAnnualInterestRatePercent().doubleValue() / 100 / MONTHS_IN_YEAR;
        double monthlyPayment = calculateMonthlyPayment().doubleValue();

        for (int month = 0; month < monthsAlreadyPaid; month++) {
            double interest = balance * monthlyRate;
            double principalPortion = monthlyPayment - interest;
            balance -= principalPortion;
        }

        return BigDecimal.valueOf(balance).setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    private AnnuityCreditOffer remainingTermOffer(BigDecimal principal, int remainingMonths) {
        return new AnnuityCreditOffer(getBank(), getOfferName(), getPurpose(),
                principal, getAnnualInterestRatePercent(), remainingMonths);
    }
}
