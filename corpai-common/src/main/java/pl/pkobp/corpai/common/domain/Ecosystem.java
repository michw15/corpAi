package pl.pkobp.corpai.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ecosystem {
    private String companyNip;
    private List<ContractorInfo> top10Suppliers;
    private List<ContractorInfo> top10Buyers;
    private List<String> exportPartners;
    private List<String> importPartners;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContractorInfo {
        private String nip;
        private String name;
        private BigDecimal estimatedVolume;
        private boolean isAlreadyPkoBpClient;
        private String advisorName;
        private Double nps;
    }
}
