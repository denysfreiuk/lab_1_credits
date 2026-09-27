package com.creditadvisor.service;

import com.creditadvisor.model.AnnuityCreditOffer;
import com.creditadvisor.model.Bank;
import com.creditadvisor.model.Client;
import com.creditadvisor.model.CreditOffer;
import com.creditadvisor.model.CreditPurpose;
import com.creditadvisor.model.CreditSearchCriteria;
import com.creditadvisor.repository.CreditOfferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditAdvisorServiceTest {

    private static final Bank PRIVAT_BANK = new Bank("PrivatBank", "PB-001", 5);
    private static final Bank OSCHAD_BANK = new Bank("Oschadbank", "OB-002", 4);

    @Mock
    private CreditOfferRepository repository;

    private CreditAdvisorService service;

    @BeforeEach
    void setUp() {
        service = new CreditAdvisorService(repository);
    }

    @Test
    void constructorRejectsNullRepository() {
        assertThrows(IllegalArgumentException.class, () -> new CreditAdvisorService(null));
    }

    @Test
    void searchReturnsOnlyMatchingOffersSortedFromCheapestToMostExpensive() {
        CreditOffer cheapOffer = new AnnuityCreditOffer(PRIVAT_BANK, "Cheap", CreditPurpose.AUTO,
                new BigDecimal("60000"), new BigDecimal("10"), 12);
        CreditOffer pricierOffer = new AnnuityCreditOffer(OSCHAD_BANK, "Pricier", CreditPurpose.AUTO,
                new BigDecimal("60000"), new BigDecimal("25"), 12);
        CreditOffer wrongPurposeOffer = new AnnuityCreditOffer(PRIVAT_BANK, "Mortgage", CreditPurpose.MORTGAGE,
                new BigDecimal("60000"), new BigDecimal("5"), 12);
        CreditOffer tooSmallOffer = new AnnuityCreditOffer(PRIVAT_BANK, "Too Small", CreditPurpose.AUTO,
                new BigDecimal("10000"), new BigDecimal("5"), 12);
        when(repository.findAll()).thenReturn(List.of(pricierOffer, tooSmallOffer, cheapOffer, wrongPurposeOffer));

        CreditSearchCriteria criteria = new CreditSearchCriteria(CreditPurpose.AUTO, new BigDecimal("50000"), 12);

        List<CreditOffer> result = service.search(criteria);

        assertEquals(List.of(cheapOffer, pricierOffer), result);
    }

    @Test
    void searchRejectsNullCriteria() {
        assertThrows(IllegalArgumentException.class, () -> service.search(null));
    }

    @Test
    void findOptimalOfferReturnsTheCheapestMatchingOffer() {
        CreditOffer cheapOffer = new AnnuityCreditOffer(PRIVAT_BANK, "Cheap", CreditPurpose.AUTO,
                new BigDecimal("60000"), new BigDecimal("10"), 12);
        CreditOffer pricierOffer = new AnnuityCreditOffer(OSCHAD_BANK, "Pricier", CreditPurpose.AUTO,
                new BigDecimal("60000"), new BigDecimal("25"), 12);
        when(repository.findAll()).thenReturn(List.of(pricierOffer, cheapOffer));

        CreditSearchCriteria criteria = new CreditSearchCriteria(CreditPurpose.AUTO, new BigDecimal("50000"), 12);

        Optional<CreditOffer> result = service.findOptimalOffer(criteria);

        assertEquals(Optional.of(cheapOffer), result);
    }

    @Test
    void findOptimalOfferReturnsEmptyWhenNothingMatches() {
        when(repository.findAll()).thenReturn(List.of());

        CreditSearchCriteria criteria = new CreditSearchCriteria(CreditPurpose.AUTO, new BigDecimal("50000"), 12);

        assertTrue(service.findOptimalOffer(criteria).isEmpty());
    }

    @Test
    void searchAffordableForKeepsOnlyOffersWithAffordableFirstPayment() {
        CreditOffer affordableOffer = new AnnuityCreditOffer(PRIVAT_BANK, "Affordable", CreditPurpose.CONSUMER,
                new BigDecimal("40000"), new BigDecimal("10"), 12);
        CreditOffer tooExpensiveOffer = new AnnuityCreditOffer(OSCHAD_BANK, "Too Expensive", CreditPurpose.CONSUMER,
                new BigDecimal("200000"), new BigDecimal("10"), 12);
        when(repository.findAll()).thenReturn(List.of(affordableOffer, tooExpensiveOffer));

        CreditSearchCriteria criteria = new CreditSearchCriteria(CreditPurpose.CONSUMER, new BigDecimal("40000"), 12);
        Client client = new Client("Ivan Petrenko", new BigDecimal("10000"));

        List<CreditOffer> result = service.searchAffordableFor(criteria, client, new BigDecimal("40"));

        assertEquals(List.of(affordableOffer), result);
    }

    @Test
    void searchAffordableForRejectsNullClient() {
        CreditSearchCriteria criteria = new CreditSearchCriteria(CreditPurpose.CONSUMER, new BigDecimal("40000"), 12);

        assertThrows(IllegalArgumentException.class, () ->
                service.searchAffordableFor(criteria, null, new BigDecimal("40")));
    }
}
