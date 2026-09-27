package com.creditadvisor.model;

import com.creditadvisor.model.feature.CreditLineExtendable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * A revolving credit line: the client can draw up to the {@link #getCreditLimit()},
 * pays interest-only installments on the drawn amount every month, and repays
 * the full drawn amount as a single balloon payment together with the last
 * month's interest. The limit itself can be increased via
 * {@link #increaseLimit(BigDecimal)}, which is typical for business credit lines.
 */
public final class CreditLineOffer extends CreditOffer implements CreditLineExtendable {

    private static final int MONTHS_IN_YEAR = 12;
    private static final int MONEY_SCALE = 2;

    public CreditLineOffer(Bank bank,
                            String offerName,
                            CreditPurpose purpose,
                            BigDecimal creditLimit,
                            BigDecimal annualInterestRatePercent,
                            int termInMonths) {
        super(bank, offerName, purpose, creditLimit, annualInterestRatePercent, termInMonths);
    }

    /** Alias for {@link #getPrincipalAmount()} using credit-line terminology. */
    @Override
    public BigDecimal getCreditLimit() {
        return getPrincipalAmount();
    }

    @Override
    public List<BigDecimal> calculatePaymentSchedule() {
        int termInMonths = getTermInMonths();
        BigDecimal monthlyInterest = calculateMonthlyInterest();

        List<BigDecimal> schedule = new ArrayList<>(termInMonths);
        for (int month = 1; month < termInMonths; month++) {
            schedule.add(monthlyInterest);
        }
        schedule.add(getCreditLimit().add(monthlyInterest));

        return List.copyOf(schedule);
    }

    private BigDecimal calculateMonthlyInterest() {
        return getCreditLimit()
                .multiply(getAnnualInterestRatePercent())
                .divide(BigDecimal.valueOf(100L * MONTHS_IN_YEAR), MONEY_SCALE, RoundingMode.HALF_UP);
    }

    @Override
    public CreditLineOffer increaseLimit(BigDecimal additionalLimitAmount) {
        if (additionalLimitAmount == null || additionalLimitAmount.signum() <= 0) {
            throw new IllegalArgumentException("Additional limit amount must be positive");
        }

        return new CreditLineOffer(getBank(), getOfferName(), getPurpose(),
                getCreditLimit().add(additionalLimitAmount), getAnnualInterestRatePercent(), getTermInMonths());
    }
}
