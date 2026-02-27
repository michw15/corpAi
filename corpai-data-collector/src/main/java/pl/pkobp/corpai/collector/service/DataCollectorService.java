package pl.pkobp.corpai.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.pkobp.corpai.collector.adapter.*;
import pl.pkobp.corpai.collector.domain.DateRange;
import pl.pkobp.corpai.collector.domain.RawDataPackage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Collects data from all external sources in parallel.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DataCollectorService {

    private final KrsFinancialAdapter krsAdapter;
    private final EmisAdapter emisAdapter;
    private final WebCrawlerService crawlerService;
    private final LinkedInAdapter linkedInAdapter;
    private final TenderAdapter tenderAdapter;

    /**
     * Collects all external data for a company in parallel.
     *
     * @param nip         company NIP
     * @param companyName company name for search queries
     * @param range       date range for news/social media
     * @return CompletableFuture with packaged raw data
     */
    public CompletableFuture<RawDataPackage> collectAll(String nip, String companyName, DateRange range) {
        log.info("Starting parallel data collection for NIP: {}", nip);

        CompletableFuture<List<byte[]>> financialStatementsFuture =
                CompletableFuture.supplyAsync(() -> krsAdapter.fetchFinancialStatements(nip));

        CompletableFuture<List<RawDataPackage.NewsArticle>> bankierNewsFuture =
                CompletableFuture.supplyAsync(() -> crawlerService.crawl("bankier.pl", companyName, range));

        CompletableFuture<List<RawDataPackage.NewsArticle>> pbNewsFuture =
                CompletableFuture.supplyAsync(() -> crawlerService.crawl("pb.pl", companyName, range));

        CompletableFuture<List<RawDataPackage.NewsArticle>> forbesNewsFuture =
                CompletableFuture.supplyAsync(() -> crawlerService.crawl("forbes.pl", companyName, range));

        CompletableFuture<List<RawDataPackage.LinkedInPost>> linkedInFuture =
                CompletableFuture.supplyAsync(() -> linkedInAdapter.fetchPosts(companyName, range));

        CompletableFuture<List<RawDataPackage.Tender>> tendersFuture =
                CompletableFuture.supplyAsync(() -> tenderAdapter.fetchTenders(nip, range));

        return CompletableFuture.allOf(
                financialStatementsFuture, bankierNewsFuture, pbNewsFuture,
                forbesNewsFuture, linkedInFuture, tendersFuture
        ).thenApply(v -> {
            List<RawDataPackage.NewsArticle> allNews = new ArrayList<>();
            allNews.addAll(bankierNewsFuture.join());
            allNews.addAll(pbNewsFuture.join());
            allNews.addAll(forbesNewsFuture.join());

            return RawDataPackage.builder()
                    .companyNip(nip)
                    .financialStatements(financialStatementsFuture.join())
                    .newsArticles(allNews)
                    .linkedInPosts(linkedInFuture.join())
                    .tenders(tendersFuture.join())
                    .build();
        });
    }
}
