package pl.pkobp.corpai.notification.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pkobp.corpai.common.domain.SalesOpportunity;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTrigger {
    private String id;
    private String advisorId;
    private String companyNip;
    private String companyName;
    private SalesOpportunity opportunity;
    private NotificationChannel channel;
    private LocalDate triggerDate;
    private String actionSuggestion;
    private boolean isRead;
    private LocalDateTime sentAt;

    public enum NotificationChannel {
        CRM_PUSH,
        EMAIL,
        MS_TEAMS
    }
}
