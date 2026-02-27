package pl.pkobp.corpai.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Represents a request to analyze a company.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisRequest {
    @Builder.Default
    private String correlationId = UUID.randomUUID().toString();
    private String nip;
    private String krs;
    private String companyName;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private ReportType reportType;
    private AppVersion appVersion;
    private String requestedByAdvisorId;
    private String requestedByCrmUserId;
    private List<AnalysisModule> modules;
}
