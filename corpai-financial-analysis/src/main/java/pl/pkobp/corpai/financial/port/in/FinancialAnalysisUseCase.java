package pl.pkobp.corpai.financial.port.in;

import pl.pkobp.corpai.common.domain.FinancialIndicators;
import pl.pkobp.corpai.financial.domain.CreditRating;
import pl.pkobp.corpai.financial.domain.SectorBenchmark;

import java.math.BigDecimal;
import java.util.List;

/**
 * Use case interface for financial analysis operations.
 */
public interface FinancialAnalysisUseCase {
    /**
     * Analyzes the last three years of financial data for the given NIP.
     */
    List<FinancialIndicators> analyzeLastThreeYears(String nip);

    /**
     * Calculates financial indicators for a specific year.
     */
    FinancialIndicators calculateIndicators(String nip, int year);

    /**
     * Detects upcoming credit maturities (within 180 days = urgent).
     */
    List<FinancialIndicators.CreditMaturity> detectUpcomingMaturities(String nip);

    /**
     * Compares company indicators to sector benchmark.
     */
    SectorBenchmark compareToBenchmark(String nip, String sectorPkd);

    /**
     * Calculates internal credit rating (V2 feature).
     */
    CreditRating calculateRating(String nip);

    /**
     * Calculates pre-approval credit limit (V2 feature).
     */
    BigDecimal calculatePreLimit(String nip);
}
