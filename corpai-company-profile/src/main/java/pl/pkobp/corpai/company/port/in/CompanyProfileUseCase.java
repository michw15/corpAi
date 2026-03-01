package pl.pkobp.corpai.company.port.in;

import pl.pkobp.corpai.common.domain.AmlCheckResult;
import pl.pkobp.corpai.common.domain.BoardMember;
import pl.pkobp.corpai.common.domain.Company;
import pl.pkobp.corpai.common.domain.ContactInfo;

import java.util.List;

/**
 * Use case interface for fetching and managing company profiles.
 */
public interface CompanyProfileUseCase {
    /**
     * Returns a cached or freshly fetched company profile.
     * Uses the KRS number to fetch data from the KRS API (NIP is used as cache key).
     *
     * @param nip company NIP (used as cache key)
     * @param krs company KRS number (required for KRS API lookup)
     * @return the company profile, or {@code null} if not found or krs is blank
     */
    Company getOrFetchProfile(String nip, String krs);

    /**
     * Returns the ownership structure for the given NIP.
     */
    AmlCheckResult.OwnershipGraph getOwnershipStructure(String nip);

    /**
     * Returns the board members for the given NIP and KRS number.
     */
    List<BoardMember> getBoardMembers(String nip, String krs);

    /**
     * Finds contact info for the given NIP and KRS number.
     */
    ContactInfo findContactInfo(String nip, String krs);
}
