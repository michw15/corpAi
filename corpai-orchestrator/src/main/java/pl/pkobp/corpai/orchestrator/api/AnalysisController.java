package pl.pkobp.corpai.orchestrator.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pkobp.corpai.common.domain.AnalysisRequest;
import pl.pkobp.corpai.orchestrator.domain.AggregatedAnalysis;
import pl.pkobp.corpai.orchestrator.service.AnalysisStateManager;
import pl.pkobp.corpai.orchestrator.service.OrchestratorService;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * REST controller for analysis orchestration.
 */
@RestController
@RequestMapping("/api/v1/analysis")
@Slf4j
@RequiredArgsConstructor
public class AnalysisController {

    private final OrchestratorService orchestratorService;
    private final AnalysisStateManager stateManager;

    /**
     * Submit a new company analysis request.
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> submitAnalysis(@RequestBody AnalysisRequest request) {
        log.info("Received analysis request for NIP: {}", request.getNip());
        CompletableFuture<String> future = orchestratorService.orchestrate(request);
        return ResponseEntity.accepted()
                .body(Map.of("correlationId", request.getCorrelationId(), "status", "ACCEPTED"));
    }

    /**
     * Get the status of an analysis by correlation ID.
     */
    @GetMapping("/{correlationId}/status")
    public ResponseEntity<Map<String, Object>> getStatus(@PathVariable String correlationId) {
        AggregatedAnalysis analysis = stateManager.getAnalysis(correlationId);
        if (analysis == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of(
                "correlationId", correlationId,
                "status", analysis.getStatus(),
                "companyNip", analysis.getCompanyNip() != null ? analysis.getCompanyNip() : ""
        ));
    }

    /**
     * Get the full analysis report by correlation ID.
     */
    @GetMapping("/{correlationId}/report")
    public ResponseEntity<AggregatedAnalysis> getReport(@PathVariable String correlationId) {
        AggregatedAnalysis analysis = stateManager.getAnalysis(correlationId);
        if (analysis == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(analysis);
    }
}
