package pl.pkobp.corpai.notification.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pkobp.corpai.common.domain.SalesOpportunity;

import java.time.LocalDateTime;

/**
 * JPA entity representing a persisted advisor notification.
 */
@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    private String id;

    private String advisorId;
    private String companyNip;
    private String companyName;
    private String title;

    @Column(length = 2000)
    private String message;

    @Enumerated(EnumType.STRING)
    private SalesOpportunity.Priority priority;

    private LocalDateTime createdAt;

    @Column(name = "is_read")
    private boolean read;
}
