package pl.pkobp.corpai.orchestrator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.pkobp.corpai.common.domain.AnalysisRequest;
import pl.pkobp.corpai.common.events.AnalysisCompletedEvent;
import pl.pkobp.corpai.common.events.AnalysisRequestedEvent;
import pl.pkobp.corpai.orchestrator.domain.AggregatedAnalysis;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Central orchestrator service that coordinates all analysis agents.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OrchestratorService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AnalysisStateManager stateManager;

    /**
     * Orchestrates a full company analysis by publishing events and running agents in parallel.
     *
     * @param request the analysis request
     * @return CompletableFuture containing the correlation ID
     */
    public CompletableFuture<String> orchestrate(AnalysisRequest request) {
        log.info("Starting orchestration for NIP: {}, correlationId: {}", request.getNip(), request.getCorrelationId());

        AnalysisRequestedEvent event = AnalysisRequestedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .occurredAt(LocalDateTime.now())
                .analysisRequest(request)
                .build();

        kafkaTemplate.send(AnalysisRequestedEvent.TOPIC, request.getCorrelationId(), event);
        stateManager.initializeAnalysis(request.getCorrelationId(), request.getNip());

        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("Waiting for agents to complete for correlationId: {}", request.getCorrelationId());
                // In real implementation, we would wait for all agents to publish their results
                // and aggregate them. For now, we simulate the orchestration.
                AggregatedAnalysis analysis = aggregateResults(request.getCorrelationId());
                publishCompletedEvent(request.getCorrelationId(), analysis);
                return request.getCorrelationId();
            } catch (Exception e) {
                log.error("Orchestration failed for correlationId: {}", request.getCorrelationId(), e);
                publishFailedEvent(request.getCorrelationId(), e.getMessage());
                throw new RuntimeException("Orchestration failed", e);
            }
        });
    }

    /**
     * Aggregates results from all agents for the given correlation ID.
     */
    private AggregatedAnalysis aggregateResults(String correlationId) {
        log.debug("Aggregating results for correlationId: {}", correlationId);
        return stateManager.getAnalysis(correlationId);
    }

    private void publishCompletedEvent(String correlationId, AggregatedAnalysis analysis) {
        AnalysisCompletedEvent event = AnalysisCompletedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .correlationId(correlationId)
                .occurredAt(LocalDateTime.now())
                .companyNip(analysis != null ? analysis.getCompanyNip() : null)
                .success(true)
                .build();
        kafkaTemplate.send(AnalysisCompletedEvent.TOPIC, correlationId, event);
    }

    private void publishFailedEvent(String correlationId, String errorMessage) {
        AnalysisCompletedEvent event = AnalysisCompletedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .correlationId(correlationId)
                .occurredAt(LocalDateTime.now())
                .success(false)
                .errorMessage(errorMessage)
                .build();
        kafkaTemplate.send(AnalysisCompletedEvent.TOPIC, correlationId, event);
    }
}
