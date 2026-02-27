package pl.pkobp.corpai.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pkobp.corpai.common.domain.AmlCheckResult;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmlCheckCompletedEvent {
    public static final String TOPIC = "corpai.aml.check.completed";

    private String eventId;
    private String correlationId;
    private LocalDateTime occurredAt;
    private String companyNip;
    private AmlCheckResult.GoNoGoDecision decision;
    private boolean hasRedFlags;
}
