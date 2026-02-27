package pl.pkobp.corpai.company.adapter.out;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pl.pkobp.corpai.common.domain.AmlCheckResult;
import pl.pkobp.corpai.company.port.out.CrbrApiPort;

import java.util.List;

/**
 * HTTP adapter for the CRBR (Central Register of Beneficial Owners) API.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class CrbrApiAdapter implements CrbrApiPort {

    private static final String CRBR_API_BASE_URL = "https://crbr.podatki.gov.pl/adCrbr/api";

    private final WebClient.Builder webClientBuilder;

    private WebClient getClient() {
        return webClientBuilder.baseUrl(CRBR_API_BASE_URL).build();
    }

    @Override
    public List<AmlCheckResult.BeneficialOwner> fetchBeneficialOwners(String nip) {
        log.info("Fetching beneficial owners from CRBR for NIP: {}", nip);
        try {
            return getClient()
                    .get()
                    .uri("/beneficial-owners/{nip}", nip)
                    .retrieve()
                    .bodyToFlux(AmlCheckResult.BeneficialOwner.class)
                    .collectList()
                    .block();
        } catch (Exception e) {
            log.error("Failed to fetch beneficial owners for NIP {} from CRBR: {}", nip, e.getMessage());
            return List.of();
        }
    }

    @Override
    public AmlCheckResult.OwnershipGraph buildOwnershipGraph(String nip) {
        log.info("Building ownership graph from CRBR for NIP: {}", nip);
        List<AmlCheckResult.BeneficialOwner> owners = fetchBeneficialOwners(nip);
        List<AmlCheckResult.OwnershipGraph.Node> nodes = owners.stream()
                .map(o -> AmlCheckResult.OwnershipGraph.Node.builder()
                        .id(o.getPesel())
                        .name(o.getFirstName() + " " + o.getLastName())
                        .type("PERSON")
                        .country(o.getNationality())
                        .build())
                .toList();
        return AmlCheckResult.OwnershipGraph.builder()
                .nodes(nodes)
                .edges(List.of())
                .build();
    }
}
