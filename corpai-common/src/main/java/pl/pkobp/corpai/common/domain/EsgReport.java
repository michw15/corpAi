package pl.pkobp.corpai.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EsgReport {
    private String companyNip;
    private boolean hasEsgInitiatives;
    private String esgProjectDescription;
    private boolean plansCapexForEsg;
    private boolean communicatedInAnnualReport;
    private boolean communicatedInSocialMedia;
    private List<String> energyTransformationProjects;
    private List<String> sustainabilityGoals;
}
