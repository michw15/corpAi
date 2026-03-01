package pl.pkobp.corpai.company.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient configuration for external API calls.
 * Configures a KRS-specific WebClient for the KRS API at https://api-krs.ms.gov.pl.
 */
@Configuration
public class WebClientConfig {

    @Value("${krs.api.base-url:https://api-krs.ms.gov.pl}")
    private String krsApiBaseUrl;

    @Bean
    public WebClient krsWebClient() {
        return WebClient.builder()
                .baseUrl(krsApiBaseUrl)
                .build();
    }
}
