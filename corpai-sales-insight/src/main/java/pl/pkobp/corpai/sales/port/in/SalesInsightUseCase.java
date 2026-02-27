package pl.pkobp.corpai.sales.port.in;

import pl.pkobp.corpai.common.domain.EsgReport;
import pl.pkobp.corpai.common.domain.FinancialIndicators;
import pl.pkobp.corpai.common.domain.SalesOpportunity;
import pl.pkobp.corpai.sales.domain.AnalysisContext;

import java.util.List;

/**
 * Use case interface for sales opportunity detection and scoring.
 */
public interface SalesInsightUseCase {
    /**
     * Detects all sales opportunities for a company.
     */
    List<SalesOpportunity> detectOpportunities(String nip, AnalysisContext context);

    /**
     * Scores and ranks a list of opportunities.
     */
    List<SalesOpportunity> scoreAndRank(List<SalesOpportunity> opportunities);

    /**
     * Detects credit maturity refinancing opportunity.
     */
    SalesOpportunity detectCreditMaturityOpportunity(FinancialIndicators indicators);

    /**
     * Detects FX treasury opportunity.
     */
    SalesOpportunity detectFxOpportunity(FinancialIndicators indicators);

    /**
     * Detects leasing opportunity.
     */
    SalesOpportunity detectLeasingOpportunity(FinancialIndicators indicators);

    /**
     * Detects ESG green financing opportunity.
     */
    SalesOpportunity detectEsgOpportunity(EsgReport esgReport);
}
