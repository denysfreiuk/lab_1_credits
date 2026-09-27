package com.creditadvisor.service;

import com.creditadvisor.model.Client;
import com.creditadvisor.model.CreditOffer;
import com.creditadvisor.model.CreditSearchCriteria;
import com.creditadvisor.repository.CreditOfferRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Forms the set of targeted credit proposals for a client: searches the
 * catalog for offers matching a request, ranks them from most to least
 * advantageous, and can narrow the results down to what the client can
 * actually afford.
 */
public final class CreditAdvisorService {

    private final CreditOfferRepository repository;

    public CreditAdvisorService(CreditOfferRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("Repository must not be null");
        }
        this.repository = repository;
    }

    /**
     * Every offer matching the criteria, ordered from the most to the least
     * advantageous (see {@link CreditOffer#compareTo(CreditOffer)}).
     */
    public List<CreditOffer> search(CreditSearchCriteria criteria) {
        if (criteria == null) {
            throw new IllegalArgumentException("Search criteria must not be null");
        }

        return repository.findAll().stream()
                .filter(criteria::isSatisfiedBy)
                .sorted()
                .toList();
    }

    /** The single most advantageous offer matching the criteria, if any exists. */
    public Optional<CreditOffer> findOptimalOffer(CreditSearchCriteria criteria) {
        return search(criteria).stream().findFirst();
    }

    /**
     * Offers matching the criteria whose first scheduled payment the client
     * can afford under the given maximum debt-to-income ratio. The first
     * payment is used as the affordability check because it is the client's
     * worst-case monthly burden for every offer type: constant for an
     * annuity, the highest installment for a differentiated schedule, and
     * the (low) interest-only installment for a credit line.
     */
    public List<CreditOffer> searchAffordableFor(CreditSearchCriteria criteria, Client client,
                                                  BigDecimal maxDebtToIncomeRatioPercent) {
        if (client == null) {
            throw new IllegalArgumentException("Client must not be null");
        }

        BigDecimal maxAffordablePayment = client.calculateMaxAffordableMonthlyPayment(maxDebtToIncomeRatioPercent);

        return search(criteria).stream()
                .filter(offer -> firstScheduledPayment(offer).compareTo(maxAffordablePayment) <= 0)
                .toList();
    }

    private static BigDecimal firstScheduledPayment(CreditOffer offer) {
        return offer.calculatePaymentSchedule().get(0);
    }
}
