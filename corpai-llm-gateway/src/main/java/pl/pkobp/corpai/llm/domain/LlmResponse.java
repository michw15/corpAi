package pl.pkobp.corpai.llm.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LlmResponse {
    private String content;
    private String modelUsed;
    private int tokensUsed;
    private boolean cached;
    private long latencyMs;
}
