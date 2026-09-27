package com.creditadvisor.model;

/**
 * Target purpose of a credit, used both to classify {@link CreditOffer}s
 * and to filter them by a client's request.
 */
public enum CreditPurpose {

    MORTGAGE("Іпотечний кредит"),
    AUTO("Автокредит"),
    CONSUMER("Споживчий кредит"),
    BUSINESS("Кредит для бізнесу");

    private final String displayName;

    CreditPurpose(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns a human-readable name of this purpose, suitable for printing
     * to the console.
     */
    public String getDisplayName() {
        return displayName;
    }
}
