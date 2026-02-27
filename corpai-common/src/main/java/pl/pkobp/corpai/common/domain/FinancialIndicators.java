package pl.pkobp.corpai.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Financial indicators for a company in a given year.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialIndicators {
    private int year;

    // Revenues & Profitability
    private BigDecimal revenuePln;
    private BigDecimal ebitda;
    private BigDecimal netProfit;
    private BigDecimal netProfitMargin;
    private BigDecimal revenueGrowthYoY;
    private Integer employeeCount;

    // Liquidity
    private BigDecimal currentRatio;
    private BigDecimal quickRatio;
    private BigDecimal cashRatio;
    private boolean liquidityDeteriorating;

    // Debt
    private BigDecimal debtToEquity;
    private BigDecimal longTermDebt;
    private BigDecimal shortTermDebt;
    private boolean ltDebtDecreasingStDebtRising;
    private List<CreditMaturity> upcomingCreditMaturities;

    // Turnover
    private BigDecimal receivablesTurnoverDays;
    private BigDecimal payablesTurnoverDays;
    private BigDecimal inventoryTurnoverDays;

    // FX
    private boolean hasFxExposure;
    private BigDecimal fxDifferencesValue;
    private List<String> exportCountries;
    private List<String> importCountries;
    private BigDecimal exportRevenueSharePct;

    // Leasing & Factoring
    private boolean hasLeasing;
    private BigDecimal leasingValue;
    private boolean usesFactoring;
    private boolean usesSupplyChainFinance;
    private boolean plansSupplyChainFinance;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreditMaturity {
        private String bankName;
        private BigDecimal amount;
        private LocalDate maturityDate;
        private String creditType;
        private int daysToMaturity;
        private boolean isUrgent;
    }
}
