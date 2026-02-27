package pl.pkobp.corpai.llm.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * Sanitizes sensitive data before sending to external LLM providers.
 * SECURITY: This service is critical - it ensures no internal bank data leaks outside SIB zone.
 * 
 * Removes/replaces: internal account numbers, advisor IDs, CRM user IDs,
 * internal system references, and other bank-confidential identifiers.
 */
@Service
@Slf4j
public class DataSanitizer {

    private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile("\\b\\d{26}\\b");
    private static final Pattern INTERNAL_ID_PATTERN = Pattern.compile("\\b(ADV|CRM|INT)-\\w+\\b");
    private static final Pattern PESEL_PATTERN = Pattern.compile("\\b\\d{11}\\b");

    /**
     * Sanitizes input data before sending to external LLM.
     *
     * @param rawData raw data that may contain sensitive information
     * @return sanitized data safe to send outside SIB
     */
    public String sanitize(String rawData) {
        if (rawData == null) return null;
        log.debug("Sanitizing data for external LLM transmission");

        String sanitized = rawData;
        sanitized = ACCOUNT_NUMBER_PATTERN.matcher(sanitized).replaceAll("[ACCOUNT_REDACTED]");
        sanitized = INTERNAL_ID_PATTERN.matcher(sanitized).replaceAll("[INTERNAL_ID_REDACTED]");
        sanitized = PESEL_PATTERN.matcher(sanitized).replaceAll("[PESEL_REDACTED]");

        log.debug("Data sanitization complete");
        return sanitized;
    }

    /**
     * Validates that sanitized data contains no remaining sensitive patterns.
     *
     * @param sanitizedData data after sanitization
     * @return true if safe to transmit
     */
    public boolean isSafeToTransmit(String sanitizedData) {
        if (sanitizedData == null) return false;
        return !ACCOUNT_NUMBER_PATTERN.matcher(sanitizedData).find()
                && !INTERNAL_ID_PATTERN.matcher(sanitizedData).find()
                && !PESEL_PATTERN.matcher(sanitizedData).find();
    }
}
