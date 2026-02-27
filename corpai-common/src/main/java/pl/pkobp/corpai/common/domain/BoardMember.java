package pl.pkobp.corpai.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardMember {
    private String firstName;
    private String lastName;
    private String position;
    private String email;
    private String phone;
    private String linkedInUrl;
    private LocalDate appointedDate;
    private boolean recentlyChanged;
}
