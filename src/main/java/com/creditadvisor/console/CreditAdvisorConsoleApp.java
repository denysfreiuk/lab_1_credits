package com.creditadvisor.console;

import com.creditadvisor.model.Client;
import com.creditadvisor.model.CreditOffer;
import com.creditadvisor.model.CreditPurpose;
import com.creditadvisor.model.CreditSearchCriteria;
import com.creditadvisor.service.CreditAdvisorService;

import java.io.InputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

/**
 * The console entry point of the advisor: reads the client's request in a
 * single linear pass (no nested menus), asks the {@link CreditAdvisorService}
 * for matching, affordable offers, and prints them ranked from the most to
 * the least advantageous. Input and output are injected so the whole flow
 * can be exercised in tests without touching the real console.
 */
public final class CreditAdvisorConsoleApp {

    private static final BigDecimal MAX_DEBT_TO_INCOME_RATIO_PERCENT = new BigDecimal("40");

    private final CreditAdvisorService service;
    private final Scanner input;
    private final PrintStream output;

    public CreditAdvisorConsoleApp(CreditAdvisorService service, InputStream input, PrintStream output) {
        if (service == null) {
            throw new IllegalArgumentException("Service must not be null");
        }
        if (input == null || output == null) {
            throw new IllegalArgumentException("Input and output streams must not be null");
        }

        this.service = service;
        this.input = new Scanner(input, StandardCharsets.UTF_8);
        this.output = output;
    }

    /** Runs one full request-response cycle: read the client's request, search, print the results. */
    public void run() {
        output.println("=== Кредитний радник ===");

        CreditPurpose purpose = readPurpose();
        BigDecimal desiredAmount = readPositiveBigDecimal("Бажана сума кредиту: ");
        int desiredTermInMonths = readPositiveInt("Бажаний строк, мінімум (у місяцях): ");
        String fullName = readNonBlankLine("Ваше ім'я: ");
        BigDecimal monthlyIncome = readPositiveBigDecimal("Ваш місячний дохід: ");

        CreditSearchCriteria criteria = new CreditSearchCriteria(purpose, desiredAmount, desiredTermInMonths);
        Client client = new Client(fullName, monthlyIncome);

        List<CreditOffer> offers = service.searchAffordableFor(criteria, client, MAX_DEBT_TO_INCOME_RATIO_PERCENT);

        printResults(offers);
    }

    private CreditPurpose readPurpose() {
        output.println("Оберіть мету кредиту:");
        CreditPurpose[] purposes = CreditPurpose.values();
        for (int i = 0; i < purposes.length; i++) {
            output.println((i + 1) + ". " + purposes[i].getDisplayName());
        }

        int choice = readPositiveInt("Ваш вибір: ");
        if (choice > purposes.length) {
            throw new IllegalArgumentException("No such menu option: " + choice);
        }

        return purposes[choice - 1];
    }

    private void printResults(List<CreditOffer> offers) {
        if (offers.isEmpty()) {
            output.println("На жаль, підходящих пропозицій не знайдено.");
            return;
        }

        output.println("Знайдено пропозицій: " + offers.size() + " (від найвигіднішої до найменш вигідної):");
        int rank = 1;
        for (CreditOffer offer : offers) {
            output.printf(
                    "%d. %s — %s: сума %.2f, ставка %.2f%%, строк %d міс., загальна вартість %.2f, переплата %.2f%n",
                    rank++, offer.getBank().getName(), offer.getOfferName(), offer.getPrincipalAmount(),
                    offer.getAnnualInterestRatePercent(), offer.getTermInMonths(),
                    offer.calculateTotalCost(), offer.calculateOverpaymentAmount());
        }
    }

    private BigDecimal readPositiveBigDecimal(String prompt) {
        String line = readLine(prompt);
        BigDecimal value = new BigDecimal(line);
        if (value.signum() <= 0) {
            throw new IllegalArgumentException("Expected a positive number, but got: " + line);
        }
        return value;
    }

    private int readPositiveInt(String prompt) {
        String line = readLine(prompt);
        int value = Integer.parseInt(line);
        if (value <= 0) {
            throw new IllegalArgumentException("Expected a positive number, but got: " + line);
        }
        return value;
    }

    private String readNonBlankLine(String prompt) {
        String line = readLine(prompt);
        if (line.isBlank()) {
            throw new IllegalArgumentException("Expected a non-blank value");
        }
        return line;
    }

    private String readLine(String prompt) {
        output.print(prompt);
        return input.nextLine().trim();
    }
}
