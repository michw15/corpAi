package pl.pkobp.corpai.report.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.pkobp.corpai.report.domain.AggregatedAnalysis;

/**
 * Builds the Full Report HTML with all sections:
 * 1. Company profile
 * 2. Detailed financial analysis
 * 3. Ecosystem (suppliers/buyers)
 * 4. ESG/Energy transformation
 * 5. Competitive position
 * 6. Social media & communications
 * 7. AML/KYC
 * 8. All sales opportunities
 * 9. Email draft
 */
@Service
@Slf4j
public class FullReportBuilder {

    /**
     * Builds the Full Report HTML content.
     */
    public String build(AggregatedAnalysis analysis) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang='pl'><head><meta charset='UTF-8'>");
        html.append("<title>CorpAI Full Report - ").append(escape(analysis.getCompanyNip())).append("</title>");
        html.append("</head><body>");

        appendHeader(html, analysis);
        appendCompanyProfile(html, analysis);
        appendFinancialAnalysis(html, analysis);
        appendEcosystem(html, analysis);
        appendEsg(html, analysis);
        appendAmlKyc(html, analysis);
        appendSalesOpportunities(html, analysis);

        html.append("</body></html>");
        return html.toString();
    }

    private void appendHeader(StringBuilder html, AggregatedAnalysis analysis) {
        html.append("<header><h1>CorpAI - Pełny Raport</h1>");
        if (analysis.getCompany() != null) {
            html.append("<h2>").append(escape(analysis.getCompany().getFullName())).append("</h2>");
        }
        html.append("</header>");
    }

    private void appendCompanyProfile(StringBuilder html, AggregatedAnalysis analysis) {
        html.append("<section id='profile'><h2>1. Profil firmy</h2>");
        if (analysis.getCompany() != null) {
            html.append("<p>NIP: ").append(escape(analysis.getCompany().getNip())).append("</p>");
            html.append("<p>KRS: ").append(escape(analysis.getCompany().getKrs())).append("</p>");
            html.append("<p>Zatrudnienie: ").append(analysis.getCompany().getEmployeeCount()).append("</p>");
        }
        html.append("</section>");
    }

    private void appendFinancialAnalysis(StringBuilder html, AggregatedAnalysis analysis) {
        html.append("<section id='financial'><h2>2. Analiza finansowa</h2>");
        if (analysis.getFinancialIndicators() != null) {
            analysis.getFinancialIndicators().forEach(fi -> {
                html.append("<h3>Rok ").append(fi.getYear()).append("</h3>");
                if (fi.getRevenuePln() != null) html.append("<p>Przychody: ").append(fi.getRevenuePln()).append(" PLN</p>");
                if (fi.getNetProfit() != null) html.append("<p>Zysk netto: ").append(fi.getNetProfit()).append(" PLN</p>");
                if (fi.getCurrentRatio() != null) html.append("<p>Wskaźnik płynności: ").append(fi.getCurrentRatio()).append("</p>");
            });
        }
        html.append("</section>");
    }

    private void appendEcosystem(StringBuilder html, AggregatedAnalysis analysis) {
        html.append("<section id='ecosystem'><h2>3. Ekosystem (dostawcy/odbiorcy)</h2>");
        if (analysis.getEcosystem() != null) {
            html.append("<p>Liczba dostawców: ").append(
                    analysis.getEcosystem().getTop10Suppliers() != null ?
                    analysis.getEcosystem().getTop10Suppliers().size() : 0).append("</p>");
        }
        html.append("</section>");
    }

    private void appendEsg(StringBuilder html, AggregatedAnalysis analysis) {
        html.append("<section id='esg'><h2>4. ESG / Transformacja energetyczna</h2>");
        if (analysis.getEsgReport() != null) {
            html.append("<p>Inicjatywy ESG: ").append(analysis.getEsgReport().isHasEsgInitiatives()).append("</p>");
            html.append("<p>Opis: ").append(escape(analysis.getEsgReport().getEsgProjectDescription())).append("</p>");
        }
        html.append("</section>");
    }

    private void appendAmlKyc(StringBuilder html, AggregatedAnalysis analysis) {
        html.append("<section id='aml'><h2>7. AML/KYC</h2>");
        if (analysis.getAmlCheckResult() != null) {
            html.append("<p>Decyzja: ").append(analysis.getAmlCheckResult().getDecision()).append("</p>");
            html.append("<p>Podsumowanie: ").append(escape(analysis.getAmlCheckResult().getSummary())).append("</p>");
        }
        html.append("</section>");
    }

    private void appendSalesOpportunities(StringBuilder html, AggregatedAnalysis analysis) {
        html.append("<section id='sales'><h2>8. Szanse sprzedażowe</h2>");
        if (analysis.getSalesOpportunities() != null) {
            analysis.getSalesOpportunities().forEach(opp -> {
                html.append("<div class='opportunity'>");
                html.append("<p><strong>").append(escape(opp.getType().name())).append("</strong> - Priorytet: ").append(opp.getPriority()).append("</p>");
                html.append("<p>").append(escape(opp.getDescription())).append("</p>");
                html.append("<p>Rekomendacja: ").append(escape(opp.getRecommendedAction())).append("</p>");
                html.append("</div>");
            });
        }
        html.append("</section>");
    }

    private String escape(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
