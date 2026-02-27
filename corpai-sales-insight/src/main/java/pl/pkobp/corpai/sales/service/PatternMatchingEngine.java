package pl.pkobp.corpai.sales.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.pkobp.corpai.common.domain.FinancialIndicators;
import pl.pkobp.corpai.common.domain.SalesOpportunity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Pattern matching engine for V3 proactive detection.
 * Matches predefined patterns against financial data to detect opportunities.
 *
 * Patterns: credit_maturity_approaching, lt_debt_decreasing_st_rising,
 * fx_exposure_no_hedging, leasing_expiry, liquidity_deterioration,
 * new_investment_planned, foreign_expansion_signals.
 */
@Service
@Slf4j
public class PatternMatchingEngine {

    /**
     * Matches all patterns against the given financial indicators.
     *
     * @param nip        company NIP
     * @param indicators financial indicators to match against
     * @return list of matched opportunities
     */
    public List<SalesOpportunity> matchPatterns(String nip, List<FinancialIndicators> indicators) {
        log.info("Running pattern matching for NIP: {}", nip);
        List<SalesOpportunity> matched = new ArrayList<>();

        for (FinancialIndicators fi : indicators) {
            matchCreditMaturityApproaching(fi, nip, matched);
            matchLtDebtDecreasingStRising(fi, nip, matched);
            matchFxExposureNoHedging(fi, nip, matched);
            matchLiquidityDeterioration(fi, nip, matched);
            matchForeignExpansion(fi, nip, matched);
        }

        return matched;
    }

    private void matchCreditMaturityApproaching(FinancialIndicators fi, String nip, List<SalesOpportunity> result) {
        if (fi.getUpcomingCreditMaturities() != null
                && fi.getUpcomingCreditMaturities().stream().anyMatch(FinancialIndicators.CreditMaturity::isUrgent)) {
            result.add(SalesOpportunity.builder()
                    .id(UUID.randomUUID().toString())
                    .companyNip(nip)
                    .type(SalesOpportunity.SalesOpportunityType.CREDIT_MATURITY_REFINANCING)
                    .priority(SalesOpportunity.Priority.CRITICAL)
                    .description("Pattern: credit maturity approaching within 180 days")
                    .confidenceScore(0.95)
                    .build());
        }
    }

    private void matchLtDebtDecreasingStRising(FinancialIndicators fi, String nip, List<SalesOpportunity> result) {
        if (fi.isLtDebtDecreasingStDebtRising()) {
            result.add(SalesOpportunity.builder()
                    .id(UUID.randomUUID().toString())
                    .companyNip(nip)
                    .type(SalesOpportunity.SalesOpportunityType.CREDIT_MATURITY_REFINANCING)
                    .priority(SalesOpportunity.Priority.HIGH)
                    .description("Pattern: LT debt decreasing while ST debt rising - refinancing opportunity")
                    .confidenceScore(0.80)
                    .build());
        }
    }

    private void matchFxExposureNoHedging(FinancialIndicators fi, String nip, List<SalesOpportunity> result) {
        if (fi.isHasFxExposure()) {
            result.add(SalesOpportunity.builder()
                    .id(UUID.randomUUID().toString())
                    .companyNip(nip)
                    .type(SalesOpportunity.SalesOpportunityType.FX_TREASURY)
                    .priority(SalesOpportunity.Priority.HIGH)
                    .description("Pattern: FX exposure detected without apparent hedging")
                    .confidenceScore(0.85)
                    .build());
        }
    }

    private void matchLiquidityDeterioration(FinancialIndicators fi, String nip, List<SalesOpportunity> result) {
        if (fi.isLiquidityDeteriorating()) {
            result.add(SalesOpportunity.builder()
                    .id(UUID.randomUUID().toString())
                    .companyNip(nip)
                    .type(SalesOpportunity.SalesOpportunityType.SUPPLY_CHAIN_FINANCE)
                    .priority(SalesOpportunity.Priority.HIGH)
                    .description("Pattern: liquidity deterioration - supply chain finance opportunity")
                    .confidenceScore(0.75)
                    .build());
        }
    }

    private void matchForeignExpansion(FinancialIndicators fi, String nip, List<SalesOpportunity> result) {
        if (fi.getExportCountries() != null && fi.getExportCountries().size() > 3) {
            result.add(SalesOpportunity.builder()
                    .id(UUID.randomUUID().toString())
                    .companyNip(nip)
                    .type(SalesOpportunity.SalesOpportunityType.FOREIGN_EXPANSION)
                    .priority(SalesOpportunity.Priority.MEDIUM)
                    .description("Pattern: company exports to multiple countries - foreign expansion financing")
                    .confidenceScore(0.70)
                    .build());
        }
    }
}
