package pl.pkobp.corpai.company.port.out;

import pl.pkobp.corpai.common.domain.AmlCheckResult;

import java.util.List;

/**
 * Port for accessing the CRBR (Central Register of Beneficial Owners) API.
 */
public interface CrbrApiPort {
    /**
     * Fetches beneficial owners for the given NIP.
     */
    List<AmlCheckResult.BeneficialOwner> fetchBeneficialOwners(String nip);

    /**
     * Builds the ownership graph for the given NIP using CRBR data.
     */
    AmlCheckResult.OwnershipGraph buildOwnershipGraph(String nip);
}
