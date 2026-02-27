package pl.pkobp.corpai.orchestrator.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pkobp.corpai.common.domain.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AggregatedAnalysis {
    private String correlationId;
    private String companyNip;
    private LocalDateTime analysisStartedAt;
    private LocalDateTime analysisCompletedAt;
    private Company company;
    private List<FinancialIndicators> financialIndicators;
    private AmlCheckResult amlCheckResult;
    private List<SalesOpportunity> salesOpportunities;
    private EsgReport esgReport;
    private Ecosystem ecosystem;
    private AnalysisStatus status;

    public enum AnalysisStatus {
        IN_PROGRESS, COMPLETED, FAILED, PARTIAL
    }
}
