package pl.pkobp.corpai.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Scheduler (V3) that monitors time-based triggers:
 * - Credit maturities approaching (< 180 days)
 * - Leasing expiries
 * - Anniversary triggers (jubileusz firmy)
 * - Instrument expiry dates from financial statements
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TriggerScheduler {

    private final NotificationService notificationService;

    /**
     * Checks for upcoming credit maturities daily at 8 AM.
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void checkCreditMaturities() {
        log.info("Checking upcoming credit maturities...");
        // In real implementation: query database for companies with upcoming maturities
        // and trigger notifications for advisors
    }

    /**
     * Checks for leasing expiries daily at 8 AM.
     */
    @Scheduled(cron = "0 15 8 * * ?")
    public void checkLeasingExpiries() {
        log.info("Checking upcoming leasing expiries...");
        // In real implementation: query database for companies with expiring leasing contracts
    }

    /**
     * Checks for company anniversaries weekly on Mondays.
     */
    @Scheduled(cron = "0 0 9 * * MON")
    public void checkAnniversaries() {
        log.info("Checking company anniversaries...");
        // In real implementation: query database for companies with anniversaries this week
        // Milestone anniversaries (10, 20, 25, 50 years) are especially important
    }

    /**
     * Checks for financial instrument expiry dates weekly.
     */
    @Scheduled(cron = "0 30 8 * * MON")
    public void checkInstrumentExpiries() {
        log.info("Checking financial instrument expiry dates...");
        // In real implementation: query database for FX instruments, bonds, etc. expiring soon
    }
}
