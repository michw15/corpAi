package pl.pkobp.corpai.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneratedReport {
    private String reportId;
    private String companyNip;
    private String companyName;
    private ReportType reportType;
    private LocalDateTime generatedAt;
    private String onePagerHtml;
    private String fullReportHtml;
    private String emailDraftText;
    private String advisorId;
    private AppVersion version;
}
