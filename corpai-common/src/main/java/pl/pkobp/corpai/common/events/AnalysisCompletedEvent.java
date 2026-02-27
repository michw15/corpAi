package pl.pkobp.corpai.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisCompletedEvent {
    public static final String TOPIC = "corpai.analysis.completed";

    private String eventId;
    private String correlationId;
    private LocalDateTime occurredAt;
    private String companyNip;
    private String reportId;
    private boolean success;
    private String errorMessage;
}
