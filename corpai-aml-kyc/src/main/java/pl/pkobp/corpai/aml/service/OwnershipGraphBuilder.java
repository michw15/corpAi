package pl.pkobp.corpai.aml.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.pkobp.corpai.common.domain.AmlCheckResult;

import java.util.List;

/**
 * Builds ownership graph from KRS + CRBR data.
 */
@Service
@Slf4j
public class OwnershipGraphBuilder {

    /**
     * Builds ownership graph for a company.
     *
     * @param nip company NIP
     * @return ownership graph with nodes (persons/companies) and edges (ownership percentages)
     */
    public AmlCheckResult.OwnershipGraph build(String nip) {
        log.info("Building ownership graph for NIP: {}", nip);
        // In real implementation: combine KRS shareholders data + CRBR beneficial owners
        return AmlCheckResult.OwnershipGraph.builder()
                .nodes(List.of())
                .edges(List.of())
                .build();
    }

    /**
     * Identifies Ultimate Beneficial Owners (UBO) with >= 25% ownership.
     *
     * @param nip company NIP
     * @return list of beneficial owners
     */
    public List<AmlCheckResult.BeneficialOwner> identifyUBO(String nip) {
        log.info("Identifying UBOs for NIP: {}", nip);
        // In real implementation: traverse ownership graph to find UBOs
        return List.of();
    }
}
