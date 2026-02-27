package pl.pkobp.corpai.report.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvisorPreferences {
    private String advisorId;
    private String advisorName;
    private String advisorEmail;
    private String communicationStyle; // FORMAL, SEMI_FORMAL, CASUAL
    private String language; // PL, EN
    private boolean includeFinancialCharts;
    private boolean includeEsgSection;
}
