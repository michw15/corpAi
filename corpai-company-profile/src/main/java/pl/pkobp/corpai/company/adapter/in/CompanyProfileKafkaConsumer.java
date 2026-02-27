package pl.pkobp.corpai.company.adapter.in;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.pkobp.corpai.common.events.AnalysisRequestedEvent;
import pl.pkobp.corpai.company.port.in.CompanyProfileUseCase;

/**
 * Kafka consumer for AnalysisRequestedEvent - triggers company profile fetch.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class CompanyProfileKafkaConsumer {

    private final CompanyProfileUseCase companyProfileUseCase;

    @KafkaListener(topics = AnalysisRequestedEvent.TOPIC, groupId = "corpai-company-profile")
    public void onAnalysisRequested(AnalysisRequestedEvent event) {
        log.info("Received AnalysisRequestedEvent for NIP: {}, correlationId: {}",
                event.getAnalysisRequest().getNip(),
                event.getAnalysisRequest().getCorrelationId());
        String nip = event.getAnalysisRequest().getNip();
        if (nip != null && !nip.isBlank()) {
            companyProfileUseCase.getOrFetchProfile(nip);
        }
    }
}
