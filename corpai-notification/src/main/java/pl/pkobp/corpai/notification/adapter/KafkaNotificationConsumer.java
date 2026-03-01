package pl.pkobp.corpai.notification.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.pkobp.corpai.common.domain.SalesOpportunity;
import pl.pkobp.corpai.common.events.SalesOpportunityDetectedEvent;
import pl.pkobp.corpai.notification.domain.Notification;
import pl.pkobp.corpai.notification.domain.NotificationTrigger;
import pl.pkobp.corpai.notification.repository.NotificationRepository;
import pl.pkobp.corpai.notification.service.NotificationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final NotificationRepository notificationRepository;

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

                    Notification notification = Notification.builder()
                            .id(trigger.getId())
                            .advisorId(event.getAdvisorId())
                            .companyNip(event.getCompanyNip())
                            .title("Szansa sprzedażowa: " + (opp.getType() != null ? opp.getType().name() : "N/A"))
                            .message(opp.getDescription())
                            .priority(opp.getPriority())
                            .createdAt(LocalDateTime.now())
                            .read(false)
                            .build();
                    notificationRepository.save(notification);
                });
    }
}
