package pl.pkobp.corpai.company.port.out;

import pl.pkobp.corpai.common.domain.Company;

import java.util.List;

/**
 * Port for accessing the KRS (National Court Register) API.
 * Note: the KRS API only supports lookup by KRS number, not by NIP.
 */
public interface KrsApiPort {

    /**
     * Fetches company data by KRS number.
     */
    Company fetchCompanyByKrs(String krs);

    /**
     * Fetches references to financial statements for the given KRS number.
     */
    List<FinancialStatementReference> fetchFinancialStatementRefs(String krs);

    record FinancialStatementReference(String year, String url, String type) {}
}
