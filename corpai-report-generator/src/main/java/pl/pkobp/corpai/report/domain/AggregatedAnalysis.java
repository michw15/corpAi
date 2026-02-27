package pl.pkobp.corpai.report.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pkobp.corpai.common.domain.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AggregatedAnalysis {
    private String correlationId;
    private String companyNip;
    private Company company;
    private List<FinancialIndicators> financialIndicators;
    private AmlCheckResult amlCheckResult;
    private List<SalesOpportunity> salesOpportunities;
    private EsgReport esgReport;
    private Ecosystem ecosystem;
}
