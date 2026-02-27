package pl.pkobp.corpai.company.port.out;

import pl.pkobp.corpai.common.domain.Company;

import java.util.List;

/**
 * Port for accessing the KRS (National Court Register) API.
 */
public interface KrsApiPort {
    /**
     * Fetches company data by NIP number.
     */
    Company fetchCompanyByNip(String nip);

    /**
     * Fetches company data by KRS number.
     */
    Company fetchCompanyByKrs(String krs);

    /**
     * Fetches references to financial statements for the given NIP.
     */
    List<FinancialStatementReference> fetchFinancialStatementRefs(String nip);

    record FinancialStatementReference(String year, String url, String type) {}
}
