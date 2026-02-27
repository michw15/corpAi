package pl.pkobp.corpai.financial.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectorBenchmark {
    private String sectorPkd;
    private String sectorName;
    private BigDecimal avgRevenuePln;
    private BigDecimal avgEbitdaMargin;
    private BigDecimal avgNetProfitMargin;
    private BigDecimal avgDebtToEquity;
    private BigDecimal avgCurrentRatio;
    private String companyNip;
    private BigDecimal companyRevenueVsBenchmarkPct;
    private BigDecimal companyEbitdaVsBenchmarkPct;
}
