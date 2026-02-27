package pl.pkobp.corpai.notification.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.pkobp.corpai.common.domain.SalesOpportunity;
import pl.pkobp.corpai.common.events.SalesOpportunityDetectedEvent;
import pl.pkobp.corpai.notification.domain.NotificationTrigger;
import pl.pkobp.corpai.notification.service.NotificationService;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Kafka consumer for SalesOpportunityDetectedEvent.
 * Triggers notifications to advisors for high/critical priority opportunities.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaNotificationConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = SalesOpportunityDetectedEvent.TOPIC, groupId = "corpai-notification")
    public void onSalesOpportunityDetected(SalesOpportunityDetectedEvent event) {
        log.info("Received SalesOpportunityDetectedEvent for NIP: {}, {} opportunities",
                event.getCompanyNip(), event.getOpportunities() != null ? event.getOpportunities().size() : 0);

        if (event.getOpportunities() == null) return;

        event.getOpportunities().stream()
                .filter(opp -> opp.getPriority() == SalesOpportunity.Priority.CRITICAL
                        || opp.getPriority() == SalesOpportunity.Priority.HIGH)
                .forEach(opp -> {
                    NotificationTrigger trigger = NotificationTrigger.builder()
                            .id(UUID.randomUUID().toString())
                            .advisorId(event.getAdvisorId())
                            .companyNip(event.getCompanyNip())
                            .opportunity(opp)
                            .channel(NotificationTrigger.NotificationChannel.CRM_PUSH)
                            .triggerDate(LocalDate.now())
                            .actionSuggestion(opp.getRecommendedAction())
                            .build();
                    notificationService.sendOpportunityAlert(trigger);
                });
    }
}
