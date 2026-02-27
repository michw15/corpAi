package pl.pkobp.corpai.financial.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditRating {
    private String companyNip;
    private String rating; // AAA, AA, A, BBB, BB, B, CCC, CC, C, D
    private int score; // 0-100
    private String rationale;
    private boolean eligibleForAutomaticApproval;
}
