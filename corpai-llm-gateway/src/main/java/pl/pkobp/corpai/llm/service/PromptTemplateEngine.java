package pl.pkobp.corpai.llm.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import pl.pkobp.corpai.llm.domain.PromptTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages and resolves prompt templates.
 * Fills templates with company-specific parameters (NIP, name).
 */
@Service
@Slf4j
public class PromptTemplateEngine {

    private final Map<PromptTemplate, String> templateCache = new ConcurrentHashMap<>();

    /**
     * Loads and fills a prompt template with the given parameters.
     *
     * @param template   prompt template type
     * @param parameters placeholder values to fill into the template
     * @return filled prompt string
     */
    public String fillTemplate(PromptTemplate template, Map<String, String> parameters) {
        String templateContent = loadTemplate(template);
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            templateContent = templateContent.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return templateContent;
    }

    private String loadTemplate(PromptTemplate template) {
        return templateCache.computeIfAbsent(template, t -> {
            try {
                ClassPathResource resource = new ClassPathResource("prompts/" + t.getTemplateFile());
                return resource.getContentAsString(StandardCharsets.UTF_8);
            } catch (IOException e) {
                log.warn("Could not load template file: {}, using fallback", t.getTemplateFile());
                return "Analyze the following company data [template '" + t.getTemplateFile() + "' failed to load]: {{data}}";
            }
        });
    }
}
