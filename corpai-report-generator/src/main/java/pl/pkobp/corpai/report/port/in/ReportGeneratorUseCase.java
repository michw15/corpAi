package pl.pkobp.corpai.report.port.in;

import pl.pkobp.corpai.common.domain.GeneratedReport;
import pl.pkobp.corpai.report.domain.AdvisorPreferences;
import pl.pkobp.corpai.report.domain.AggregatedAnalysis;

/**
 * Use case interface for report generation.
 */
public interface ReportGeneratorUseCase {
    /**
     * Generates a One Pager brief for client meeting preparation.
     */
    GeneratedReport generateOnePager(AggregatedAnalysis analysis);

    /**
     * Generates a full detailed report.
     */
    GeneratedReport generateFullReport(AggregatedAnalysis analysis);

    /**
     * Generates a personalized email draft using LLM.
     */
    String generateEmailDraft(AggregatedAnalysis analysis, AdvisorPreferences preferences);
}
