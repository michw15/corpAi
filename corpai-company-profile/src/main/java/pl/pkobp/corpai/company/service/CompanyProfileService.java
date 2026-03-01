package pl.pkobp.corpai.company.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import pl.pkobp.corpai.common.domain.AmlCheckResult;
import pl.pkobp.corpai.common.domain.BoardMember;
import pl.pkobp.corpai.common.domain.Company;
import pl.pkobp.corpai.common.domain.ContactInfo;
import pl.pkobp.corpai.company.port.in.CompanyProfileUseCase;
import pl.pkobp.corpai.company.port.out.CrbrApiPort;
import pl.pkobp.corpai.company.port.out.KrsApiPort;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Service implementing company profile use cases.
 * Caches results in Redis with 24h TTL.
 * Company profiles are fetched from the KRS API using the KRS number.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CompanyProfileService implements CompanyProfileUseCase {

    private static final String CACHE_PREFIX = "corpai:company:";
    private static final long CACHE_TTL_HOURS = 24;

    private final KrsApiPort krsApiPort;
    private final CrbrApiPort crbrApiPort;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Company getOrFetchProfile(String nip, String krs) {
        String cacheKey = CACHE_PREFIX + nip;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached instanceof Company company) {
            log.debug("Cache hit for company NIP: {}", nip);
            return company;
        }

        log.info("Fetching company profile for NIP: {}, KRS: {}", nip, krs);
        if (krs == null || krs.isBlank()) {
            log.warn("KRS number not provided for NIP: {}; skipping KRS API fetch", nip);
            return null;
        }
        Company company = krsApiPort.fetchCompanyByKrs(krs);
        if (company != null) {
            redisTemplate.opsForValue().set(cacheKey, company, CACHE_TTL_HOURS, TimeUnit.HOURS);
        }
        return company;
    }

    @Override
    public AmlCheckResult.OwnershipGraph getOwnershipStructure(String nip) {
        log.info("Building ownership structure for NIP: {}", nip);
        return crbrApiPort.buildOwnershipGraph(nip);
    }

    @Override
    public List<BoardMember> getBoardMembers(String nip, String krs) {
        Company company = getOrFetchProfile(nip, krs);
        if (company != null && company.getBoardMembers() != null) {
            return company.getBoardMembers();
        }
        return List.of();
    }

    @Override
    public ContactInfo findContactInfo(String nip, String krs) {
        Company company = getOrFetchProfile(nip, krs);
        if (company == null || company.getBoardMembers() == null || company.getBoardMembers().isEmpty()) {
            return null;
        }
        BoardMember firstMember = company.getBoardMembers().get(0);
        return ContactInfo.builder()
                .firstName(firstMember.getFirstName())
                .lastName(firstMember.getLastName())
                .position(firstMember.getPosition())
                .email(firstMember.getEmail())
                .phone(firstMember.getPhone())
                .linkedInUrl(firstMember.getLinkedInUrl())
                .source("KRS")
                .build();
    }
}
