package pl.pkobp.corpai.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Result of an AML/KYC check for a company.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmlCheckResult {
    private String companyNip;
    private GoNoGoDecision decision;
    private List<RedFlag> redFlags;
    private List<BeneficialOwner> beneficialOwners;
    private OwnershipGraph ownershipGraph;
    private String summary;

    public enum GoNoGoDecision {
        GO,
        NO_GO,
        REQUIRES_MANUAL_REVIEW
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RedFlag {
        private RedFlagType type;
        private String description;
        private String source;
        private boolean confirmed;

        public enum RedFlagType {
            SANCTIONS_LIST,
            PEP_POLITICALLY_EXPOSED,
            NEGATIVE_MEDIA,
            COMPLEX_OWNERSHIP_STRUCTURE,
            TAX_HAVEN_JURISDICTION,
            CRIMINAL_PROCEEDINGS,
            BANKRUPTCY_PROCEEDINGS,
            MISSING_FINANCIAL_STATEMENTS
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BeneficialOwner {
        private String firstName;
        private String lastName;
        private String pesel;
        private double ownershipPercentage;
        private boolean isPep;
        private String nationality;
        private String residenceCountry;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OwnershipGraph {
        private List<Node> nodes;
        private List<Edge> edges;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Node {
            private String id;
            private String name;
            private String type; // PERSON, COMPANY
            private String country;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Edge {
            private String fromId;
            private String toId;
            private double ownershipPercentage;
        }
    }
}
