package pl.pkobp.corpai.collector.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pl.pkobp.corpai.collector.domain.DateRange;
import pl.pkobp.corpai.collector.domain.RawDataPackage;

import java.util.List;

/**
 * Fetches tenders from public tender portals (e.g. ezamowienia.gov.pl).
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class TenderAdapter {

    private final WebClient.Builder webClientBuilder;

    /**
     * Fetches tenders published by or won by the company.
     *
     * @param nip   company NIP
     * @param range date range
     * @return list of tenders
     */
    public List<RawDataPackage.Tender> fetchTenders(String nip, DateRange range) {
        log.info("Fetching tenders for NIP: {}", nip);
        // In real implementation: call ezamowienia.gov.pl API
        return List.of();
    }
}
