package pl.pkobp.corpai.aml.port.in;

import pl.pkobp.corpai.common.domain.AmlCheckResult;

import java.util.List;

/**
 * Use case interface for AML/KYC checks.
 */
public interface AmlKycUseCase {
    /**
     * Performs a full AML/KYC check for a company.
     */
    AmlCheckResult performCheck(String nip);

    /**
     * Detects red flags for a company.
     */
    List<AmlCheckResult.RedFlag> detectRedFlags(String nip);

    /**
     * Builds the ownership graph for a company.
     */
    AmlCheckResult.OwnershipGraph buildOwnershipGraph(String nip);

    /**
     * Identifies Ultimate Beneficial Owners (UBO) for a company.
     */
    List<AmlCheckResult.BeneficialOwner> identifyUBO(String nip);
}
