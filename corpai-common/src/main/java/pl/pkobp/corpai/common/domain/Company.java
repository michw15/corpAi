package pl.pkobp.corpai.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Core domain object representing a company analyzed by CorpAI.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company {
    private String nip;
    private String krs;
    private String regon;
    private String fullName;
    private String shortName;
    private String legalForm;
    private String sectorPkd;
    private String sectorName;
    private String registeredAddress;
    private String city;
    private String voivodeship;
    private CompanyStatus status;
    private Integer employeeCount;
    private String websiteUrl;
    private String linkedInUrl;
    private List<BoardMember> boardMembers;
    private List<String> subsidiaries;
    private List<String> shareholders;
    private LocalDate foundedDate;
    private LocalDate lastUpdated;
}
