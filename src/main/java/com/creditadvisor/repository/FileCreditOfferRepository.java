package com.creditadvisor.repository;

import com.creditadvisor.io.CreditOfferCsvParser;
import com.creditadvisor.model.CreditOffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * A {@link CreditOfferRepository} that reads and parses its offers once,
 * either from a file bundled as a classpath resource (the way the console
 * application loads its data) or from an arbitrary file on disk.
 */
public final class FileCreditOfferRepository implements CreditOfferRepository {

    private final List<CreditOffer> offers;

    /**
     * Reads the data file from the classpath, e.g. {@code "credit-offers.csv"}
     * bundled under {@code src/main/resources}.
     */
    public FileCreditOfferRepository(String classpathResourceName) {
        this(readLinesFromClasspath(classpathResourceName));
    }

    /** Reads the data file from an arbitrary location on disk. */
    public FileCreditOfferRepository(Path filePath) {
        this(readLinesFromFile(filePath));
    }

    private FileCreditOfferRepository(List<String> csvLines) {
        this.offers = List.copyOf(new CreditOfferCsvParser().parse(csvLines));
    }

    @Override
    public List<CreditOffer> findAll() {
        return offers;
    }

    private static List<String> readLinesFromClasspath(String resourceName) {
        try (InputStream input = FileCreditOfferRepository.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (input == null) {
                throw new IllegalArgumentException("Data file not found on classpath: " + resourceName);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
                return reader.lines().toList();
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read data file: " + resourceName, e);
        }
    }

    private static List<String> readLinesFromFile(Path filePath) {
        try {
            return Files.readAllLines(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read data file: " + filePath, e);
        }
    }
}
