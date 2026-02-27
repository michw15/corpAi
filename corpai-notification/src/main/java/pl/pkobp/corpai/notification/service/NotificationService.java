package pl.pkobp.corpai.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import pl.pkobp.corpai.notification.domain.NotificationTrigger;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Service for sending notifications to advisors via multiple channels.
 * Channels: CRM_PUSH, EMAIL, MS_TEAMS
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;

    /**
     * Sends an opportunity alert to the advisor via configured channel.
     *
     * @param trigger notification trigger with channel and recipient details
     */
    public void sendOpportunityAlert(NotificationTrigger trigger) {
        log.info("Sending opportunity alert to advisor: {}, channel: {}", trigger.getAdvisorId(), trigger.getChannel());
        switch (trigger.getChannel()) {
            case EMAIL -> sendEmailNotification(trigger);
            case CRM_PUSH -> sendCrmPushNotification(trigger);
            case MS_TEAMS -> sendTeamsNotification(trigger);
        }
    }

    /**
     * Schedules a follow-up reminder for the advisor.
     *
     * @param advisorId    advisor identifier
     * @param companyNip   company NIP
     * @param followUpDate date for the follow-up
     */
    public void scheduleFollowUp(String advisorId, String companyNip, LocalDate followUpDate) {
        log.info("Scheduling follow-up for advisor: {}, company: {}, date: {}", advisorId, companyNip, followUpDate);
        // In real implementation: persist to database and trigger via TriggerScheduler
    }

    private void sendEmailNotification(NotificationTrigger trigger) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(trigger.getAdvisorId() + "@pkobp.pl");
            message.setSubject("CorpAI Alert: Szansa sprzedażowa - " + trigger.getCompanyName());
            message.setText(buildEmailBody(trigger));
            message.setSentDate(java.sql.Timestamp.valueOf(LocalDateTime.now()));
            mailSender.send(message);
            log.info("Email notification sent to advisor: {}", trigger.getAdvisorId());
        } catch (Exception e) {
            log.error("Failed to send email notification to advisor: {}", trigger.getAdvisorId(), e);
        }
    }

    private void sendCrmPushNotification(NotificationTrigger trigger) {
        log.info("Sending CRM push notification for advisor: {}, company: {}", trigger.getAdvisorId(), trigger.getCompanyNip());
        // In real implementation: call CRM API to push notification
    }

    private void sendTeamsNotification(NotificationTrigger trigger) {
        log.info("Sending MS Teams notification for advisor: {}, company: {}", trigger.getAdvisorId(), trigger.getCompanyNip());
        // In real implementation: call Teams webhook
    }

    private String buildEmailBody(NotificationTrigger trigger) {
        return String.format(
                "Wykryto nową szansę sprzedażową dla klienta %s (NIP: %s).\n\n" +
                "Typ: %s\n" +
                "Rekomendowana akcja: %s\n\n" +
                "Zaloguj się do systemu CorpAI, aby zobaczyć szczegóły.\n\n" +
                "Wygenerowano automatycznie przez CorpAI Banking Intelligence Platform.",
                trigger.getCompanyName(),
                trigger.getCompanyNip(),
                trigger.getOpportunity() != null ? trigger.getOpportunity().getType() : "N/A",
                trigger.getActionSuggestion()
        );
    }
}
