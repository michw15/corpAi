package pl.pkobp.corpai.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pkobp.corpai.common.domain.SalesOpportunity;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesOpportunityDetectedEvent {
    public static final String TOPIC = "corpai.sales.opportunity.detected";

    private String eventId;
    private String correlationId;
    private LocalDateTime occurredAt;
    private String companyNip;
    private String advisorId;
    private List<SalesOpportunity> opportunities;
}
