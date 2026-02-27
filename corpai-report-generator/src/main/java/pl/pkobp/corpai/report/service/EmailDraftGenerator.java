package pl.pkobp.corpai.report.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.pkobp.corpai.common.domain.SalesOpportunity;
import pl.pkobp.corpai.report.domain.AdvisorPreferences;
import pl.pkobp.corpai.report.domain.AggregatedAnalysis;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Generates personalized email drafts for client meetings.
 * Uses LLM (Spring AI) to create contextual email with 3 key discussion points.
 */
@Service
@Slf4j
public class EmailDraftGenerator {

    /**
     * Generates a personalized email draft for the advisor.
     *
     * @param analysis    aggregated company analysis
     * @param preferences advisor communication preferences
     * @return email draft text
     */
    public String generate(AggregatedAnalysis analysis, AdvisorPreferences preferences) {
        log.info("Generating email draft for NIP: {}", analysis.getCompanyNip());

        String companyName = analysis.getCompany() != null ? analysis.getCompany().getFullName() : analysis.getCompanyNip();
        List<String> topPoints = extractTopDiscussionPoints(analysis);

        return buildEmailTemplate(companyName, topPoints, preferences);
    }

    private List<String> extractTopDiscussionPoints(AggregatedAnalysis analysis) {
        if (analysis.getSalesOpportunities() == null || analysis.getSalesOpportunities().isEmpty()) {
            return List.of("Omówienie aktualnej sytuacji finansowej", "Możliwości współpracy z PKO BP", "Plany rozwojowe firmy");
        }
        return analysis.getSalesOpportunities().stream()
                .limit(3)
                .map(SalesOpportunity::getRecommendedAction)
                .collect(Collectors.toList());
    }

    private String buildEmailTemplate(String companyName, List<String> discussionPoints, AdvisorPreferences preferences) {
        String greeting = isPolish(preferences) ? "Szanowni Państwo," : "Dear Sir/Madam,";
        String opening = isPolish(preferences)
                ? "Dziękuję za dotychczasową współpracę z PKO BP. W związku z nadchodzącym spotkaniem przygotowałem/am dla Państwa kluczowe punkty do omówienia:"
                : "Thank you for your continued partnership with PKO BP. In preparation for our upcoming meeting, I have identified key discussion points:";

        StringBuilder email = new StringBuilder();
        email.append(greeting).append("\n\n");
        email.append(opening).append("\n\n");

        for (int i = 0; i < discussionPoints.size(); i++) {
            email.append(i + 1).append(". ").append(discussionPoints.get(i)).append("\n");
        }

        email.append("\n");
        if (isPolish(preferences)) {
            email.append("Będę wdzięczny/a za potwierdzenie spotkania.\n\nZ poważaniem,\n");
        } else {
            email.append("I look forward to discussing these topics with you.\n\nBest regards,\n");
        }

        if (preferences != null && preferences.getAdvisorName() != null) {
            email.append(preferences.getAdvisorName());
        }

        return email.toString();
    }

    private boolean isPolish(AdvisorPreferences preferences) {
        return preferences == null || !"EN".equalsIgnoreCase(preferences.getLanguage());
    }
}
