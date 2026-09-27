package com.creditadvisor.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * A targeted credit offer from a {@link Bank}. Concrete subclasses differ in
 * how the monthly payment schedule is calculated (e.g. annuity vs.
 * differentiated payments); this class factors out everything that does not
 * depend on that algorithm, following the template method pattern:
 * {@link #calculateTotalCost()} and {@link #calculateOverpaymentAmount()}
 * are derived from {@link #calculatePaymentSchedule()}.
 *
 * <p>Two offers are equal only when they are of the same concrete subtype
 * and have the same field values, since the same field values processed by
 * a different payment algorithm represent a genuinely different offer.
 */
public abstract class CreditOffer implements Comparable<CreditOffer> {

    private final Bank bank;
    private final String offerName;
    private final CreditPurpose purpose;
    private final BigDecimal principalAmount;
    private final BigDecimal annualInterestRatePercent;
    private final int termInMonths;

    protected CreditOffer(Bank bank,
                           String offerName,
                           CreditPurpose purpose,
                           BigDecimal principalAmount,
                           BigDecimal annualInterestRatePercent,
                           int termInMonths) {
        if (bank == null) {
            throw new IllegalArgumentException("Bank must not be null");
        }
        if (offerName == null || offerName.isBlank()) {
            throw new IllegalArgumentException("Offer name must not be blank");
        }
        if (purpose == null) {
            throw new IllegalArgumentException("Credit purpose must not be null");
        }
        if (principalAmount == null || principalAmount.signum() <= 0) {
            throw new IllegalArgumentException("Principal amount must be positive");
        }
        if (annualInterestRatePercent == null || annualInterestRatePercent.signum() < 0) {
            throw new IllegalArgumentException("Annual interest rate must not be negative");
        }
        if (termInMonths <= 0) {
            throw new IllegalArgumentException("Term in months must be positive");
        }

        this.bank = bank;
        this.offerName = offerName;
        this.purpose = purpose;
        this.principalAmount = principalAmount;
        this.annualInterestRatePercent = annualInterestRatePercent;
        this.termInMonths = termInMonths;
    }

    /**
     * Calculates the amount due for every month of the credit term, in
     * chronological order. The list size must equal {@link #getTermInMonths()}.
     */
    public abstract List<BigDecimal> calculatePaymentSchedule();

    /**
     * Total amount the client will pay over the whole term (principal plus
     * interest), derived from {@link #calculatePaymentSchedule()}.
     */
    public BigDecimal calculateTotalCost() {
        return calculatePaymentSchedule().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * How much more than the principal the client pays in total (i.e. the
     * cost of borrowing).
     */
    public BigDecimal calculateOverpaymentAmount() {
        return calculateTotalCost().subtract(principalAmount);
    }

    public Bank getBank() {
        return bank;
    }

    public String getOfferName() {
        return offerName;
    }

    public CreditPurpose getPurpose() {
        return purpose;
    }

    public BigDecimal getPrincipalAmount() {
        return principalAmount;
    }

    public BigDecimal getAnnualInterestRatePercent() {
        return annualInterestRatePercent;
    }

    public int getTermInMonths() {
        return termInMonths;
    }

    /**
     * Orders offers by total cost (cheapest first); ties are broken in favor
     * of the more reliable bank.
     */
    @Override
    public int compareTo(CreditOffer other) {
        int costComparison = this.calculateTotalCost().compareTo(other.calculateTotalCost());
        if (costComparison != 0) {
            return costComparison;
        }
        return Integer.compare(other.bank.getReliabilityRating(), this.bank.getReliabilityRating());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        CreditOffer that = (CreditOffer) other;
        return termInMonths == that.termInMonths
                && bank.equals(that.bank)
                && offerName.equals(that.offerName)
                && purpose == that.purpose
                && principalAmount.compareTo(that.principalAmount) == 0
                && annualInterestRatePercent.compareTo(that.annualInterestRatePercent) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), bank, offerName, purpose, principalAmount,
                annualInterestRatePercent, termInMonths);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "bank=" + bank.getName() +
                ", offerName='" + offerName + '\'' +
                ", purpose=" + purpose +
                ", principalAmount=" + principalAmount +
                ", annualInterestRatePercent=" + annualInterestRatePercent +
                ", termInMonths=" + termInMonths +
                '}';
    }
}
