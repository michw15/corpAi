package pl.pkobp.corpai.financial.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.pkobp.corpai.common.domain.FinancialIndicators;
import pl.pkobp.corpai.financial.domain.CreditRating;

import java.math.BigDecimal;
import java.util.List;

/**
 * Calculates internal credit rating based on financial indicators.
 */
@Service
@Slf4j
public class RatingEngine {

    /**
     * Calculates the credit rating for a company.
     *
     * @param nip        company NIP
     * @param indicators list of financial indicators (last 3 years)
     * @return computed credit rating
     */
    public CreditRating calculate(String nip, List<FinancialIndicators> indicators) {
        log.info("Calculating credit rating for NIP: {}", nip);
        if (indicators.isEmpty()) {
            return CreditRating.builder()
                    .companyNip(nip)
                    .rating("N/A")
                    .score(0)
                    .rationale("Insufficient financial data")
                    .eligibleForAutomaticApproval(false)
                    .build();
        }

        int score = calculateScore(indicators);
        String rating = scoreToRating(score);

        return CreditRating.builder()
                .companyNip(nip)
                .rating(rating)
                .score(score)
                .rationale("Based on " + indicators.size() + " years of financial data")
                .eligibleForAutomaticApproval(score >= 70)
                .build();
    }

    private int calculateScore(List<FinancialIndicators> indicators) {
        int score = 50; // baseline
        FinancialIndicators latest = indicators.get(indicators.size() - 1);

        if (latest.getCurrentRatio() != null) {
            if (latest.getCurrentRatio().compareTo(BigDecimal.valueOf(1.5)) >= 0) score += 10;
            else if (latest.getCurrentRatio().compareTo(BigDecimal.ONE) < 0) score -= 15;
        }
        if (latest.getDebtToEquity() != null) {
            if (latest.getDebtToEquity().compareTo(BigDecimal.valueOf(2.0)) <= 0) score += 10;
            else if (latest.getDebtToEquity().compareTo(BigDecimal.valueOf(4.0)) > 0) score -= 15;
        }
        if (latest.isLiquidityDeteriorating()) score -= 10;
        if (latest.isLtDebtDecreasingStDebtRising()) score -= 5;

        return Math.max(0, Math.min(100, score));
    }

    private String scoreToRating(int score) {
        if (score >= 90) return "AAA";
        if (score >= 80) return "AA";
        if (score >= 70) return "A";
        if (score >= 60) return "BBB";
        if (score >= 50) return "BB";
        if (score >= 40) return "B";
        if (score >= 30) return "CCC";
        if (score >= 20) return "CC";
        if (score >= 10) return "C";
        return "D";
    }
}
