package pl.pkobp.corpai.collector.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pl.pkobp.corpai.collector.domain.DateRange;
import pl.pkobp.corpai.collector.domain.RawDataPackage;

import java.util.List;

/**
 * Fetches company LinkedIn posts.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class LinkedInAdapter {

    private final WebClient.Builder webClientBuilder;

    /**
     * Fetches recent posts for a company from LinkedIn.
     *
     * @param companyName company name or LinkedIn slug
     * @param range       date range for posts
     * @return list of LinkedIn posts
     */
    public List<RawDataPackage.LinkedInPost> fetchPosts(String companyName, DateRange range) {
        log.info("Fetching LinkedIn posts for company: {}", companyName);
        // In real implementation: use LinkedIn API or authorized scraper
        return List.of();
    }
}
