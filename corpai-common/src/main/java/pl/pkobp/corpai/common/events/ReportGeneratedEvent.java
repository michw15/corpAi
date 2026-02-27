package pl.pkobp.corpai.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pkobp.corpai.common.domain.ReportType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportGeneratedEvent {
    public static final String TOPIC = "corpai.report.generated";

    private String eventId;
    private String correlationId;
    private LocalDateTime occurredAt;
    private String companyNip;
    private String reportId;
    private ReportType reportType;
    private String advisorId;
}
