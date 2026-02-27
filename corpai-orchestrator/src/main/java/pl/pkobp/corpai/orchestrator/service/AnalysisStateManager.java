package pl.pkobp.corpai.orchestrator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import pl.pkobp.corpai.orchestrator.domain.AggregatedAnalysis;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * Manages analysis state in Redis.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AnalysisStateManager {

    private static final String KEY_PREFIX = "corpai:analysis:";
    private static final long TTL_HOURS = 24;

    private final RedisTemplate<String, Object> redisTemplate;

    public void initializeAnalysis(String correlationId, String companyNip) {
        AggregatedAnalysis analysis = AggregatedAnalysis.builder()
                .correlationId(correlationId)
                .companyNip(companyNip)
                .analysisStartedAt(LocalDateTime.now())
                .status(AggregatedAnalysis.AnalysisStatus.IN_PROGRESS)
                .build();
        redisTemplate.opsForValue().set(KEY_PREFIX + correlationId, analysis, TTL_HOURS, TimeUnit.HOURS);
    }

    public AggregatedAnalysis getAnalysis(String correlationId) {
        Object value = redisTemplate.opsForValue().get(KEY_PREFIX + correlationId);
        if (value instanceof AggregatedAnalysis analysis) {
            return analysis;
        }
        return null;
    }

    public void updateAnalysis(String correlationId, AggregatedAnalysis analysis) {
        redisTemplate.opsForValue().set(KEY_PREFIX + correlationId, analysis, TTL_HOURS, TimeUnit.HOURS);
    }
}
