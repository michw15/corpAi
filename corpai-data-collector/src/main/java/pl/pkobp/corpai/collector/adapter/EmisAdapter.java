package pl.pkobp.corpai.collector.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

/**
 * Fetches company data from EMIS (Emerging Markets Information Service).
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class EmisAdapter {

    private final WebClient.Builder webClientBuilder;

    /**
     * Fetches company data from EMIS.
     *
     * @param nip company NIP
     * @return raw EMIS data as a map
     */
    public Map<String, Object> fetchData(String nip) {
        log.info("Fetching EMIS data for NIP: {}", nip);
        // In real implementation: call EMIS API with API key
        return Map.of();
    }
}
