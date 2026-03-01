package pl.pkobp.corpai.company.adapter.in;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.pkobp.corpai.common.domain.AnalysisRequest;
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
        AnalysisRequest request = event.getAnalysisRequest();
        log.info("Received AnalysisRequestedEvent for NIP: {}, KRS: {}, correlationId: {}",
                request.getNip(),
                request.getKrs(),
                request.getCorrelationId());
        String nip = request.getNip();
        String krs = request.getKrs();
        if (nip != null && !nip.isBlank()) {
            companyProfileUseCase.getOrFetchProfile(nip, krs);
        }
    }
}
