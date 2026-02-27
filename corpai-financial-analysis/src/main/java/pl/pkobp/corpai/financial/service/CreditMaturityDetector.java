package pl.pkobp.corpai.financial.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.pkobp.corpai.common.domain.FinancialIndicators;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Specialized service for detecting credit maturities from financial statement notes.
 * Maturities within 180 days are flagged as urgent.
 */
@Service
@Slf4j
public class CreditMaturityDetector {

    private static final int URGENT_THRESHOLD_DAYS = 180;

    /**
     * Detects upcoming credit maturities for a company.
     *
     * @param nip  company NIP
     * @param year fiscal year to analyze
     * @return list of detected credit maturities
     */
    public List<FinancialIndicators.CreditMaturity> detectMaturities(String nip, int year) {
        log.debug("Detecting credit maturities for NIP: {}, year: {}", nip, year);
        // In real implementation, would parse PDF financial statements from KRS
        // and extract note data about credit facilities and their maturity dates
        return List.of();
    }

    /**
     * Checks if a maturity date is within the urgent threshold.
     */
    public boolean isUrgent(LocalDate maturityDate) {
        long daysToMaturity = ChronoUnit.DAYS.between(LocalDate.now(), maturityDate);
        return daysToMaturity > 0 && daysToMaturity <= URGENT_THRESHOLD_DAYS;
    }
}
