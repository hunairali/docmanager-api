package dev.umairalishah.docmanager.service;

import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Generates human-readable reference numbers such as "CTS-2026-000482",
 * matching the format used by the correspondence tracking systems this
 * project draws its domain model from. Backed by an in-memory counter for
 * simplicity; a production system would source the sequence from the
 * database (e.g. a dedicated sequence table) to survive restarts safely.
 */
@Component
public class ReferenceNumberGenerator {

    private final AtomicLong counter = new AtomicLong(1);

    public String next() {
        int year = Year.now().getValue();
        long value = counter.getAndIncrement();
        return "CTS-%d-%06d".formatted(year, value);
    }
}

