package pl.pkobp.corpai.sales.domain;

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
public class AnalysisContext {
    private Company company;
    private List<FinancialIndicators> financialIndicators;
    private EsgReport esgReport;
    private AmlCheckResult amlCheckResult;
    private Ecosystem ecosystem;
}
