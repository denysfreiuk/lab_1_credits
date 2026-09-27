package com.creditadvisor.repository;

import com.creditadvisor.model.CreditOffer;

import java.util.List;

/**
 * Access to the catalog of available credit offers, independent of where
 * the data actually comes from.
 */
public interface CreditOfferRepository {

    /** All credit offers currently available for search. */
    List<CreditOffer> findAll();
}
