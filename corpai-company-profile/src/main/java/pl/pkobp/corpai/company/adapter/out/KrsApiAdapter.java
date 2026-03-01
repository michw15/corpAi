package pl.pkobp.corpai.company.adapter.out;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class KrsApiAdapter implements KrsApiPort {

    private final WebClient krsWebClient;

    public KrsApiAdapter(@Qualifier("krsWebClient") WebClient krsWebClient) {
        this.krsWebClient = krsWebClient;
    }

    @Override
    public Company fetchCompanyByNip(String nip) {
        log.info("Fetching company from KRS API by NIP: {}", nip);
        try {
            return krsWebClient
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
            return krsWebClient
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
