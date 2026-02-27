package pl.pkobp.corpai.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pkobp.corpai.common.domain.AnalysisRequest;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisRequestedEvent {
    public static final String TOPIC = "corpai.analysis.requested";

    private String eventId;
    private LocalDateTime occurredAt;
    private AnalysisRequest analysisRequest;
}
