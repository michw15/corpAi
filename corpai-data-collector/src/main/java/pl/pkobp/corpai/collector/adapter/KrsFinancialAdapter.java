package pl.pkobp.corpai.collector.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * Fetches financial statements PDF from KRS API.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class KrsFinancialAdapter {

    private static final String KRS_API_BASE_URL = "https://api.krs.gov.pl";

    private final WebClient.Builder webClientBuilder;

    /**
     * Fetches financial statement PDFs for a company.
     *
     * @param nip company NIP
     * @return list of PDF bytes
     */
    public List<byte[]> fetchFinancialStatements(String nip) {
        log.info("Fetching financial statements from KRS for NIP: {}", nip);
        // In real implementation: fetch list of financial statement refs, then download each PDF
        return List.of();
    }
}
