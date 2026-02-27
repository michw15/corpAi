package pl.pkobp.corpai.company.adapter.out;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pl.pkobp.corpai.common.domain.Company;
import pl.pkobp.corpai.company.port.out.KrsApiPort;

import java.util.List;

/**
 * HTTP adapter for the KRS (National Court Register) API.
 * Calls the real KRS API at https://api.krs.gov.pl
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class KrsApiAdapter implements KrsApiPort {

    private static final String KRS_API_BASE_URL = "https://api.krs.gov.pl";

    private final WebClient.Builder webClientBuilder;

    private WebClient getClient() {
        return webClientBuilder.baseUrl(KRS_API_BASE_URL).build();
    }

    @Override
    public Company fetchCompanyByNip(String nip) {
        log.info("Fetching company from KRS API by NIP: {}", nip);
        try {
            return getClient()
                    .get()
                    .uri("/api/krs/OdpisAktualny/{nip}?rejestr=P&format=json", nip)
                    .retrieve()
                    .bodyToMono(Company.class)
                    .block();
        } catch (Exception e) {
            log.error("Failed to fetch company by NIP {} from KRS API: {}", nip, e.getMessage());
            return null;
        }
    }

    @Override
    public Company fetchCompanyByKrs(String krs) {
        log.info("Fetching company from KRS API by KRS: {}", krs);
        try {
            return getClient()
                    .get()
                    .uri("/api/krs/OdpisAktualny/{krs}?rejestr=P&format=json", krs)
                    .retrieve()
                    .bodyToMono(Company.class)
                    .block();
        } catch (Exception e) {
            log.error("Failed to fetch company by KRS {} from KRS API: {}", krs, e.getMessage());
            return null;
        }
    }

    @Override
    public List<FinancialStatementReference> fetchFinancialStatementRefs(String nip) {
        log.info("Fetching financial statement refs from KRS API for NIP: {}", nip);
        return List.of();
    }
}
