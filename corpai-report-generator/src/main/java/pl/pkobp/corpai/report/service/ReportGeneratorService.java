package pl.pkobp.corpai.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.pkobp.corpai.common.domain.GeneratedReport;
import pl.pkobp.corpai.common.domain.ReportType;
import pl.pkobp.corpai.common.events.ReportGeneratedEvent;
import pl.pkobp.corpai.report.domain.AdvisorPreferences;
import pl.pkobp.corpai.report.domain.AggregatedAnalysis;
import pl.pkobp.corpai.report.port.in.ReportGeneratorUseCase;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Orchestrates report generation using builders and LLM.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ReportGeneratorService implements ReportGeneratorUseCase {

    private final OnePagerBuilder onePagerBuilder;
    private final FullReportBuilder fullReportBuilder;
    private final EmailDraftGenerator emailDraftGenerator;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public GeneratedReport generateOnePager(AggregatedAnalysis analysis) {
        log.info("Generating One Pager for NIP: {}", analysis.getCompanyNip());
        String html = onePagerBuilder.build(analysis);
        GeneratedReport report = GeneratedReport.builder()
                .reportId(UUID.randomUUID().toString())
                .companyNip(analysis.getCompanyNip())
                .companyName(analysis.getCompany() != null ? analysis.getCompany().getFullName() : null)
                .reportType(ReportType.ONE_PAGER)
                .generatedAt(LocalDateTime.now())
                .onePagerHtml(html)
                .build();
        publishReportGeneratedEvent(analysis, report, ReportType.ONE_PAGER);
        return report;
    }

    @Override
    public GeneratedReport generateFullReport(AggregatedAnalysis analysis) {
        log.info("Generating Full Report for NIP: {}", analysis.getCompanyNip());
        String html = fullReportBuilder.build(analysis);
        GeneratedReport report = GeneratedReport.builder()
                .reportId(UUID.randomUUID().toString())
                .companyNip(analysis.getCompanyNip())
                .companyName(analysis.getCompany() != null ? analysis.getCompany().getFullName() : null)
                .reportType(ReportType.FULL_REPORT)
                .generatedAt(LocalDateTime.now())
                .fullReportHtml(html)
                .build();
        publishReportGeneratedEvent(analysis, report, ReportType.FULL_REPORT);
        return report;
    }

    @Override
    public String generateEmailDraft(AggregatedAnalysis analysis, AdvisorPreferences preferences) {
        log.info("Generating email draft for NIP: {}", analysis.getCompanyNip());
        return emailDraftGenerator.generate(analysis, preferences);
    }

    private void publishReportGeneratedEvent(AggregatedAnalysis analysis, GeneratedReport report, ReportType type) {
        ReportGeneratedEvent event = ReportGeneratedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .correlationId(analysis.getCorrelationId())
                .occurredAt(LocalDateTime.now())
                .companyNip(analysis.getCompanyNip())
                .reportId(report.getReportId())
                .reportType(type)
                .build();
        kafkaTemplate.send(ReportGeneratedEvent.TOPIC, analysis.getCompanyNip(), event);
    }
}
