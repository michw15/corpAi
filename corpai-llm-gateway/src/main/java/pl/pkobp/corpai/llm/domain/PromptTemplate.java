package pl.pkobp.corpai.llm.domain;

/**
 * Predefined prompt templates for LLM interactions.
 */
public enum PromptTemplate {
    COMPANY_ANALYSIS("company_analysis.txt"),
    ONE_PAGER_GENERATION("one_pager.txt"),
    EMAIL_DRAFT("email_draft.txt"),
    SALES_SIGNALS("sales_signals.txt"),
    AML_SIGNALS("aml_signals.txt");

    private final String templateFile;

    PromptTemplate(String templateFile) {
        this.templateFile = templateFile;
    }

    public String getTemplateFile() {
        return templateFile;
    }
}
