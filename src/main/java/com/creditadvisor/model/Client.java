package com.creditadvisor.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * A client requesting a credit. Besides identifying the client, this class
 * can tell how large a monthly payment they can realistically afford, based
 * on a debt-to-income ratio, so the advisor service can filter out offers
 * the client would not be approved for or should not take on.
 */
public final class Client {

    private static final int PERCENT_SCALE = 100;
    private static final int MONEY_SCALE = 2;

    private final String fullName;
    private final BigDecimal monthlyIncome;

    public Client(String fullName, BigDecimal monthlyIncome) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Client full name must not be blank");
        }
        if (monthlyIncome == null || monthlyIncome.signum() <= 0) {
            throw new IllegalArgumentException("Monthly income must be positive");
        }

        this.fullName = fullName;
        this.monthlyIncome = monthlyIncome;
    }

    public String getFullName() {
        return fullName;
    }

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }

    /**
     * The largest monthly credit payment this client can reasonably afford,
     * expressed as a percentage of their monthly income (a common lending
     * guideline for debt-to-income ratio).
     *
     * @param maxDebtToIncomeRatioPercent the ratio, e.g. {@code 40} for 40%; must be positive
     */
    public BigDecimal calculateMaxAffordableMonthlyPayment(BigDecimal maxDebtToIncomeRatioPercent) {
        if (maxDebtToIncomeRatioPercent == null || maxDebtToIncomeRatioPercent.signum() <= 0) {
            throw new IllegalArgumentException("Max debt-to-income ratio must be positive");
        }

        return monthlyIncome.multiply(maxDebtToIncomeRatioPercent)
                .divide(BigDecimal.valueOf(PERCENT_SCALE), MONEY_SCALE, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return "Client{" +
                "fullName='" + fullName + '\'' +
                ", monthlyIncome=" + monthlyIncome +
                '}';
    }
}
