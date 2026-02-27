package pl.pkobp.corpai.report.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.pkobp.corpai.common.domain.Company;
import pl.pkobp.corpai.common.domain.FinancialIndicators;
import pl.pkobp.corpai.common.domain.SalesOpportunity;
import pl.pkobp.corpai.report.domain.AggregatedAnalysis;

import java.util.List;

/**
 * Builds the One Pager HTML report with key sections:
 * 1. Company basic data
 * 2. Top financial indicators (3 years)
 * 3. Key events (last quarter)
 * 4. Top 3 sales opportunities
 * 5. AML status
 * 6. Contact info
 */
@Service
@Slf4j
public class OnePagerBuilder {

    /**
     * Builds the One Pager HTML content.
     */
    public String build(AggregatedAnalysis analysis) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang='pl'><head><meta charset='UTF-8'>");
        html.append("<title>CorpAI One Pager</title></head><body>");

        appendCompanySection(html, analysis.getCompany());
        appendFinancialSection(html, analysis.getFinancialIndicators());
        appendSalesOpportunitiesSection(html, analysis.getSalesOpportunities());
        appendAmlSection(html, analysis);

        html.append("</body></html>");
        return html.toString();
    }

    private void appendCompanySection(StringBuilder html, Company company) {
        html.append("<section id='company'><h2>Dane firmy</h2>");
        if (company != null) {
            html.append("<p><strong>Nazwa:</strong> ").append(escape(company.getFullName())).append("</p>");
            html.append("<p><strong>NIP:</strong> ").append(escape(company.getNip())).append("</p>");
            html.append("<p><strong>Forma prawna:</strong> ").append(escape(company.getLegalForm())).append("</p>");
            html.append("<p><strong>Sektor PKD:</strong> ").append(escape(company.getSectorPkd())).append("</p>");
            html.append("<p><strong>Miasto:</strong> ").append(escape(company.getCity())).append("</p>");
        }
        html.append("</section>");
    }

    private void appendFinancialSection(StringBuilder html, List<FinancialIndicators> indicators) {
        html.append("<section id='financials'><h2>Wskaźniki finansowe</h2>");
        if (indicators != null) {
            for (FinancialIndicators fi : indicators) {
                html.append("<h3>Rok ").append(fi.getYear()).append("</h3>");
                if (fi.getRevenuePln() != null) {
                    html.append("<p>Przychody: ").append(fi.getRevenuePln()).append(" PLN</p>");
                }
                if (fi.getEbitda() != null) {
                    html.append("<p>EBITDA: ").append(fi.getEbitda()).append(" PLN</p>");
                }
            }
        }
        html.append("</section>");
    }

    private void appendSalesOpportunitiesSection(StringBuilder html, List<SalesOpportunity> opportunities) {
        html.append("<section id='opportunities'><h2>Top szanse sprzedażowe</h2>");
        if (opportunities != null) {
            opportunities.stream().limit(3).forEach(opp -> {
                html.append("<div class='opportunity'>");
                html.append("<p><strong>").append(escape(opp.getType().name())).append("</strong></p>");
                html.append("<p>").append(escape(opp.getDescription())).append("</p>");
                html.append("<p>Priorytet: ").append(opp.getPriority()).append("</p>");
                html.append("</div>");
            });
        }
        html.append("</section>");
    }

    private void appendAmlSection(StringBuilder html, AggregatedAnalysis analysis) {
        html.append("<section id='aml'><h2>Status AML/KYC</h2>");
        if (analysis.getAmlCheckResult() != null) {
            html.append("<p>Decyzja: ").append(analysis.getAmlCheckResult().getDecision()).append("</p>");
        }
        html.append("</section>");
    }

    private String escape(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
