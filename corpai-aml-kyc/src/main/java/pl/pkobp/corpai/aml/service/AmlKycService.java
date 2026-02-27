package pl.pkobp.corpai.aml.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.pkobp.corpai.aml.port.in.AmlKycUseCase;
import pl.pkobp.corpai.common.domain.AmlCheckResult;
import pl.pkobp.corpai.common.events.AmlCheckCompletedEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service implementing AML/KYC use cases.
 * Checks red flags, builds ownership graph, and returns Go/No Go decision.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AmlKycService implements AmlKycUseCase {

    private final RedFlagDetector redFlagDetector;
    private final OwnershipGraphBuilder ownershipGraphBuilder;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public AmlCheckResult performCheck(String nip) {
        log.info("Performing AML/KYC check for NIP: {}", nip);

        List<AmlCheckResult.RedFlag> redFlags = detectRedFlags(nip);
        List<AmlCheckResult.BeneficialOwner> ubos = identifyUBO(nip);
        AmlCheckResult.OwnershipGraph graph = buildOwnershipGraph(nip);

        AmlCheckResult.GoNoGoDecision decision = determineDecision(redFlags);

        AmlCheckResult result = AmlCheckResult.builder()
                .companyNip(nip)
                .decision(decision)
                .redFlags(redFlags)
                .beneficialOwners(ubos)
                .ownershipGraph(graph)
                .summary(buildSummary(decision, redFlags))
                .build();

        publishCompletedEvent(nip, result);
        return result;
    }

    @Override
    public List<AmlCheckResult.RedFlag> detectRedFlags(String nip) {
        return redFlagDetector.detect(nip);
    }

    @Override
    public AmlCheckResult.OwnershipGraph buildOwnershipGraph(String nip) {
        return ownershipGraphBuilder.build(nip);
    }

    @Override
    public List<AmlCheckResult.BeneficialOwner> identifyUBO(String nip) {
        return ownershipGraphBuilder.identifyUBO(nip);
    }

    private AmlCheckResult.GoNoGoDecision determineDecision(List<AmlCheckResult.RedFlag> redFlags) {
        if (redFlags == null || redFlags.isEmpty()) {
            return AmlCheckResult.GoNoGoDecision.GO;
        }
        boolean hasConfirmedCritical = redFlags.stream()
                .filter(AmlCheckResult.RedFlag::isConfirmed)
                .anyMatch(f -> f.getType() == AmlCheckResult.RedFlag.RedFlagType.SANCTIONS_LIST
                        || f.getType() == AmlCheckResult.RedFlag.RedFlagType.CRIMINAL_PROCEEDINGS);
        if (hasConfirmedCritical) {
            return AmlCheckResult.GoNoGoDecision.NO_GO;
        }
        return AmlCheckResult.GoNoGoDecision.REQUIRES_MANUAL_REVIEW;
    }

    private String buildSummary(AmlCheckResult.GoNoGoDecision decision, List<AmlCheckResult.RedFlag> redFlags) {
        int flagCount = redFlags != null ? redFlags.size() : 0;
        return String.format("Decision: %s. Red flags detected: %d.", decision, flagCount);
    }

    private void publishCompletedEvent(String nip, AmlCheckResult result) {
        AmlCheckCompletedEvent event = AmlCheckCompletedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .occurredAt(LocalDateTime.now())
                .companyNip(nip)
                .decision(result.getDecision())
                .hasRedFlags(result.getRedFlags() != null && !result.getRedFlags().isEmpty())
                .build();
        kafkaTemplate.send(AmlCheckCompletedEvent.TOPIC, nip, event);
    }
}
