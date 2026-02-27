package pl.pkobp.corpai.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactInfo {
    private String firstName;
    private String lastName;
    private String position;
    private String email;
    private String phone;
    private String linkedInUrl;
    private String source;
}
