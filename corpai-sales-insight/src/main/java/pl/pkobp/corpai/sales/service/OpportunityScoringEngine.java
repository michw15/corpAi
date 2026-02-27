package pl.pkobp.corpai.sales.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.pkobp.corpai.common.domain.SalesOpportunity;

import java.util.Comparator;
import java.util.List;

/**
 * Scores and ranks sales opportunities based on multiple factors:
 * survey weight, confidence score, revenue potential, and urgency.
 */
@Service
@Slf4j
public class OpportunityScoringEngine {

    /**
     * Scores and ranks opportunities using composite scoring.
     *
     * @param opportunities unranked list of opportunities
     * @return ranked list (highest score first)
     */
    public List<SalesOpportunity> scoreAndRank(List<SalesOpportunity> opportunities) {
        return opportunities.stream()
                .map(this::applyScore)
                .sorted(Comparator.comparingDouble(SalesOpportunity::getConfidenceScore).reversed())
                .toList();
    }

    private SalesOpportunity applyScore(SalesOpportunity opportunity) {
        double surveyWeight = opportunity.getType().getSurveyScore() / 5.0;
        double priorityWeight = getPriorityWeight(opportunity.getPriority());
        double compositeScore = (surveyWeight * 0.4) + (opportunity.getConfidenceScore() * 0.4) + (priorityWeight * 0.2);
        opportunity.setConfidenceScore(Math.min(1.0, compositeScore));
        return opportunity;
    }

    private double getPriorityWeight(SalesOpportunity.Priority priority) {
        if (priority == null) return 0.5;
        return switch (priority) {
            case CRITICAL -> 1.0;
            case HIGH -> 0.75;
            case MEDIUM -> 0.5;
            case LOW -> 0.25;
        };
    }
}
