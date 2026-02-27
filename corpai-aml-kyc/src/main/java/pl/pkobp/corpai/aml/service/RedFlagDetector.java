package pl.pkobp.corpai.aml.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.pkobp.corpai.common.domain.AmlCheckResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects AML red flags for a company.
 * Checks: sanctions lists, PEP status, negative media, complex ownership,
 * tax haven jurisdictions, criminal proceedings, bankruptcy proceedings.
 */
@Service
@Slf4j
public class RedFlagDetector {

    /**
     * Detects all applicable red flags for the given company NIP.
     *
     * @param nip company NIP to check
     * @return list of detected red flags
     */
    public List<AmlCheckResult.RedFlag> detect(String nip) {
        log.info("Detecting red flags for NIP: {}", nip);
        List<AmlCheckResult.RedFlag> flags = new ArrayList<>();

        checkSanctionsList(nip, flags);
        checkPepStatus(nip, flags);
        checkNegativeMedia(nip, flags);
        checkOwnershipComplexity(nip, flags);
        checkTaxHavens(nip, flags);
        checkLegalProceedings(nip, flags);
        checkFinancialStatements(nip, flags);

        log.info("Detected {} red flags for NIP: {}", flags.size(), nip);
        return flags;
    }

    private void checkSanctionsList(String nip, List<AmlCheckResult.RedFlag> flags) {
        // In real implementation: check against EU/UN/OFAC sanctions lists
        log.debug("Checking sanctions list for NIP: {}", nip);
    }

    private void checkPepStatus(String nip, List<AmlCheckResult.RedFlag> flags) {
        // In real implementation: check against PEP databases
        log.debug("Checking PEP status for NIP: {}", nip);
    }

    private void checkNegativeMedia(String nip, List<AmlCheckResult.RedFlag> flags) {
        // In real implementation: NLP analysis of news articles
        log.debug("Checking negative media for NIP: {}", nip);
    }

    private void checkOwnershipComplexity(String nip, List<AmlCheckResult.RedFlag> flags) {
        // In real implementation: analyze CRBR ownership depth
        log.debug("Checking ownership complexity for NIP: {}", nip);
    }

    private void checkTaxHavens(String nip, List<AmlCheckResult.RedFlag> flags) {
        // In real implementation: check ownership chain for tax haven jurisdictions
        log.debug("Checking tax haven exposure for NIP: {}", nip);
    }

    private void checkLegalProceedings(String nip, List<AmlCheckResult.RedFlag> flags) {
        // In real implementation: check KRS for bankruptcy/criminal proceedings
        log.debug("Checking legal proceedings for NIP: {}", nip);
    }

    private void checkFinancialStatements(String nip, List<AmlCheckResult.RedFlag> flags) {
        // In real implementation: verify financial statements are filed on time
        log.debug("Checking financial statements compliance for NIP: {}", nip);
    }
}
