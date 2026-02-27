package pl.pkobp.corpai.financial.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.pkobp.corpai.common.domain.FinancialIndicators;
import pl.pkobp.corpai.financial.domain.CreditRating;
import pl.pkobp.corpai.financial.domain.SectorBenchmark;
import pl.pkobp.corpai.financial.port.in.FinancialAnalysisUseCase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service implementing financial analysis use cases.
 * Parses financial statements, calculates ratios, and detects signals.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FinancialAnalysisService implements FinancialAnalysisUseCase {

    private final CreditMaturityDetector creditMaturityDetector;
    private final RatingEngine ratingEngine;
    private final SectorBenchmarkService sectorBenchmarkService;

    @Override
    public List<FinancialIndicators> analyzeLastThreeYears(String nip) {
        log.info("Analyzing last 3 years of financial data for NIP: {}", nip);
        int currentYear = LocalDate.now().getYear();
        List<FinancialIndicators> results = new ArrayList<>();
        for (int year = currentYear - 3; year < currentYear; year++) {
            results.add(calculateIndicators(nip, year));
        }
        detectLiquidityDeterioration(results);
        detectLtDebtDecreasingStRising(results);
        return results;
    }

    @Override
    public FinancialIndicators calculateIndicators(String nip, int year) {
        log.debug("Calculating financial indicators for NIP: {}, year: {}", nip, year);
        // In a real implementation, this would parse financial statements from KRS/PDFs
        // and calculate all the ratios. Here we return a stub.
        return FinancialIndicators.builder()
                .year(year)
                .upcomingCreditMaturities(creditMaturityDetector.detectMaturities(nip, year))
                .build();
    }

    @Override
    public List<FinancialIndicators.CreditMaturity> detectUpcomingMaturities(String nip) {
        log.info("Detecting upcoming credit maturities for NIP: {}", nip);
        return creditMaturityDetector.detectMaturities(nip, LocalDate.now().getYear());
    }

    @Override
    public SectorBenchmark compareToBenchmark(String nip, String sectorPkd) {
        log.info("Comparing NIP: {} to sector benchmark: {}", nip, sectorPkd);
        return sectorBenchmarkService.compare(nip, sectorPkd);
    }

    @Override
    public CreditRating calculateRating(String nip) {
        log.info("Calculating credit rating for NIP: {}", nip);
        List<FinancialIndicators> indicators = analyzeLastThreeYears(nip);
        return ratingEngine.calculate(nip, indicators);
    }

    @Override
    public BigDecimal calculatePreLimit(String nip) {
        log.info("Calculating pre-limit for NIP: {}", nip);
        List<FinancialIndicators> indicators = analyzeLastThreeYears(nip);
        if (indicators.isEmpty() || indicators.get(0).getRevenuePln() == null) {
            return BigDecimal.ZERO;
        }
        // Simple pre-limit: 25% of annual revenue
        return indicators.get(0).getRevenuePln()
                .multiply(BigDecimal.valueOf(0.25))
                .setScale(0, RoundingMode.HALF_UP);
    }

    private void detectLiquidityDeterioration(List<FinancialIndicators> indicators) {
        if (indicators.size() < 2) return;
        for (int i = 1; i < indicators.size(); i++) {
            FinancialIndicators prev = indicators.get(i - 1);
            FinancialIndicators curr = indicators.get(i);
            if (prev.getCurrentRatio() != null && curr.getCurrentRatio() != null) {
                if (curr.getCurrentRatio().compareTo(prev.getCurrentRatio()) < 0) {
                    curr.setLiquidityDeteriorating(true);
                }
            }
        }
    }

    private void detectLtDebtDecreasingStRising(List<FinancialIndicators> indicators) {
        if (indicators.size() < 2) return;
        for (int i = 1; i < indicators.size(); i++) {
            FinancialIndicators prev = indicators.get(i - 1);
            FinancialIndicators curr = indicators.get(i);
            if (prev.getLongTermDebt() != null && curr.getLongTermDebt() != null
                    && prev.getShortTermDebt() != null && curr.getShortTermDebt() != null) {
                boolean ltDecreasing = curr.getLongTermDebt().compareTo(prev.getLongTermDebt()) < 0;
                boolean stRising = curr.getShortTermDebt().compareTo(prev.getShortTermDebt()) > 0;
                curr.setLtDebtDecreasingStDebtRising(ltDecreasing && stRising);
            }
        }
    }
}
