package pl.pkobp.corpai.sales.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.pkobp.corpai.common.domain.EsgReport;
import pl.pkobp.corpai.common.domain.FinancialIndicators;
import pl.pkobp.corpai.common.domain.SalesOpportunity;
import pl.pkobp.corpai.common.events.SalesOpportunityDetectedEvent;
import pl.pkobp.corpai.sales.domain.AnalysisContext;
import pl.pkobp.corpai.sales.port.in.SalesInsightUseCase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Main service orchestrating sales opportunity detection.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SalesInsightService implements SalesInsightUseCase {

    private final OpportunityScoringEngine scoringEngine;
    private final PatternMatchingEngine patternMatchingEngine;
    private final KafkaTemplate<String, Object> kafkaTemplate;



    @Override
    public List<SalesOpportunity> detectOpportunities(String nip, AnalysisContext context) {
        log.info("Detecting sales opportunities for NIP: {}", nip);
        List<SalesOpportunity> opportunities = new ArrayList<>();

        if (context.getFinancialIndicators() != null) {
            for (FinancialIndicators indicators : context.getFinancialIndicators()) {
                SalesOpportunity creditOpp = detectCreditMaturityOpportunity(indicators);
                if (creditOpp != null) opportunities.add(creditOpp);

                SalesOpportunity fxOpp = detectFxOpportunity(indicators);
                if (fxOpp != null) opportunities.add(fxOpp);

                SalesOpportunity leasingOpp = detectLeasingOpportunity(indicators);
                if (leasingOpp != null) opportunities.add(leasingOpp);
            }
        }

        if (context.getEsgReport() != null) {
            SalesOpportunity esgOpp = detectEsgOpportunity(context.getEsgReport());
            if (esgOpp != null) opportunities.add(esgOpp);
        }

        if (context.getFinancialIndicators() != null) {
            opportunities.addAll(patternMatchingEngine.matchPatterns(nip, context.getFinancialIndicators()));
        }

        List<SalesOpportunity> rankedOpportunities = scoreAndRank(opportunities);
        publishOpportunitiesEvent(nip, rankedOpportunities);
        return rankedOpportunities;
    }

    @Override
    public List<SalesOpportunity> scoreAndRank(List<SalesOpportunity> opportunities) {
        return scoringEngine.scoreAndRank(opportunities);
    }

    @Override
    public SalesOpportunity detectCreditMaturityOpportunity(FinancialIndicators indicators) {
        if (indicators.getUpcomingCreditMaturities() == null || indicators.getUpcomingCreditMaturities().isEmpty()) {
            return null;
        }
        boolean hasUrgent = indicators.getUpcomingCreditMaturities().stream()
                .anyMatch(FinancialIndicators.CreditMaturity::isUrgent);
        if (!hasUrgent) return null;

        return SalesOpportunity.builder()
                .id(UUID.randomUUID().toString())
                .type(SalesOpportunity.SalesOpportunityType.CREDIT_MATURITY_REFINANCING)
                .priority(SalesOpportunity.Priority.CRITICAL)
                .description("Upcoming credit maturity requires refinancing")
                .recommendedProduct("Credit refinancing / new credit facility")
                .recommendedAction("Contact client ASAP to discuss refinancing options")
                .confidenceScore(0.95)
                .build();
    }

    @Override
    public SalesOpportunity detectFxOpportunity(FinancialIndicators indicators) {
        if (!indicators.isHasFxExposure()) return null;

        return SalesOpportunity.builder()
                .id(UUID.randomUUID().toString())
                .type(SalesOpportunity.SalesOpportunityType.FX_TREASURY)
                .priority(SalesOpportunity.Priority.HIGH)
                .description("Company has significant FX exposure without hedging")
                .recommendedProduct("FX forward / option / swap")
                .recommendedAction("Present FX hedging strategy proposal")
                .confidenceScore(0.85)
                .build();
    }

    @Override
    public SalesOpportunity detectLeasingOpportunity(FinancialIndicators indicators) {
        if (!indicators.isHasLeasing()) return null;

        return SalesOpportunity.builder()
                .id(UUID.randomUUID().toString())
                .type(SalesOpportunity.SalesOpportunityType.LEASING)
                .priority(SalesOpportunity.Priority.MEDIUM)
                .description("Company uses leasing - potential for PKO Leasing products")
                .recommendedProduct("Operational/financial leasing")
                .recommendedAction("Propose leasing consolidation with PKO Leasing")
                .confidenceScore(0.75)
                .build();
    }

    @Override
    public SalesOpportunity detectEsgOpportunity(EsgReport esgReport) {
        if (!esgReport.isHasEsgInitiatives() && !esgReport.isPlansCapexForEsg()) return null;

        return SalesOpportunity.builder()
                .id(UUID.randomUUID().toString())
                .type(SalesOpportunity.SalesOpportunityType.ESG_GREEN_FINANCING)
                .priority(SalesOpportunity.Priority.MEDIUM)
                .description("Company has ESG initiatives - potential for green financing")
                .recommendedProduct("Green Bond / Sustainability-linked loan")
                .recommendedAction("Present ESG financing options")
                .confidenceScore(0.70)
                .build();
    }

    private void publishOpportunitiesEvent(String nip, List<SalesOpportunity> opportunities) {
        if (opportunities.isEmpty()) return;
        SalesOpportunityDetectedEvent event = SalesOpportunityDetectedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .occurredAt(LocalDateTime.now())
                .companyNip(nip)
                .opportunities(opportunities)
                .build();
        kafkaTemplate.send(SalesOpportunityDetectedEvent.TOPIC, nip, event);
    }
}
