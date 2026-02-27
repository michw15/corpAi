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
     * Returns a cached or freshly fetched company profile by NIP.
     */
    Company getOrFetchProfile(String nip);

    /**
     * Returns the ownership structure for the given NIP.
     */
    AmlCheckResult.OwnershipGraph getOwnershipStructure(String nip);

    /**
     * Returns the board members for the given NIP.
     */
    List<BoardMember> getBoardMembers(String nip);

    /**
     * Finds contact info for the given NIP.
     */
    ContactInfo findContactInfo(String nip);
}
