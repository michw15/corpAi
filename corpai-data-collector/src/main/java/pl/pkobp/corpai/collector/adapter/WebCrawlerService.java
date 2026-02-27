package pl.pkobp.corpai.collector.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pl.pkobp.corpai.collector.domain.DateRange;
import pl.pkobp.corpai.collector.domain.RawDataPackage;

import java.util.List;

/**
 * Crawls news portals for company mentions.
 * Supported portals: Bankier.pl, Puls Biznesu (pb.pl), Forbes.pl, rp.pl
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class WebCrawlerService {

    private final WebClient.Builder webClientBuilder;

    /**
     * Crawls a news portal for articles mentioning the company.
     *
     * @param portal      news portal domain (e.g. "bankier.pl")
     * @param companyName company name to search for
     * @param range       date range for articles
     * @return list of news articles
     */
    public List<RawDataPackage.NewsArticle> crawl(String portal, String companyName, DateRange range) {
        log.info("Crawling {} for company: {}", portal, companyName);
        // In real implementation: use web scraping with Jsoup or Playwright
        return List.of();
    }
}
