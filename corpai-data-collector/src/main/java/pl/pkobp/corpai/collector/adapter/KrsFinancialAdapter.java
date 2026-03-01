package pl.pkobp.corpai.collector.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * Fetches financial statements PDF from KRS API.
 */
@Component
@Slf4j
public class KrsFinancialAdapter {

    private final WebClient krsWebClient;

    public KrsFinancialAdapter(@Qualifier("krsWebClient") WebClient krsWebClient) {
        this.krsWebClient = krsWebClient;
    }

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
