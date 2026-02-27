package pl.pkobp.corpai.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a detected sales opportunity for a company.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesOpportunity {
    private String id;
    private String companyNip;
    private SalesOpportunityType type;
    private Priority priority;
    private String description;
    private String evidenceSource;
    private String evidenceText;
    private LocalDate actionDate;
    private BigDecimal estimatedRevenuePotential;
    private String recommendedProduct;
    private String recommendedAction;
    private double confidenceScore;

    public enum Priority {
        CRITICAL, HIGH, MEDIUM, LOW
    }

    /**
     * Sales opportunity types ranked by survey score from corpAi survey.
     */
    public enum SalesOpportunityType {
        NEW_INVESTMENT_CAPEX(4.88),
        FX_TREASURY(4.79),
        LEASING(4.78),
        CREDIT_MATURITY_REFINANCING(4.72),
        SUPPLY_CHAIN_FINANCE(4.59),
        FOREIGN_EXPANSION(4.48),
        ESG_GREEN_FINANCING(4.38),
        FACTORING(4.36),
        MA_MERGER_ACQUISITION(4.25),
        NEW_SECTOR_DIVERSIFICATION(4.12),
        INTERNATIONAL_TRADE_FINANCE(4.04),
        NEW_CONTRACTS_TENDERS(3.88);

        private final double surveyScore;

        SalesOpportunityType(double surveyScore) {
            this.surveyScore = surveyScore;
        }

        public double getSurveyScore() {
            return surveyScore;
        }
    }
}
