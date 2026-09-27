package com.creditadvisor;

import com.creditadvisor.console.CreditAdvisorConsoleApp;
import com.creditadvisor.repository.CreditOfferRepository;
import com.creditadvisor.repository.FileCreditOfferRepository;
import com.creditadvisor.service.CreditAdvisorService;

/** Application entry point: wires the repository, service and console app together. */
public final class Main {

    private static final String CREDIT_OFFERS_RESOURCE = "credit-offers.csv";

    private Main() {
    }

    public static void main(String[] args) {
        CreditOfferRepository repository = new FileCreditOfferRepository(CREDIT_OFFERS_RESOURCE);
        CreditAdvisorService service = new CreditAdvisorService(repository);

        new CreditAdvisorConsoleApp(service, System.in, System.out).run();
    }
}
