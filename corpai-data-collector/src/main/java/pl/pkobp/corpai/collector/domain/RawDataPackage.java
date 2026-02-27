package pl.pkobp.corpai.collector.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawDataPackage {
    private String companyNip;
    private List<byte[]> financialStatements;
    private List<Map<String, Object>> beneficiaries;
    private List<NewsArticle> newsArticles;
    private List<LinkedInPost> linkedInPosts;
    private List<Tender> tenders;
    private Map<String, Object> emisData;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewsArticle {
        private String title;
        private String url;
        private String content;
        private String source;
        private String publishedDate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LinkedInPost {
        private String content;
        private String publishedDate;
        private int likes;
        private String url;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tender {
        private String tenderId;
        private String title;
        private String description;
        private String publishedDate;
        private String deadline;
        private String value;
        private String url;
    }
}
