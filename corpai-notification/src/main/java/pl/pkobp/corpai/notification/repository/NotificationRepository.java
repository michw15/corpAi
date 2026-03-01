package pl.pkobp.corpai.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pkobp.corpai.notification.domain.Notification;

import java.util.List;

/**
 * Repository for advisor notifications.
 */
public interface NotificationRepository extends JpaRepository<Notification, String> {

    List<Notification> findAllByOrderByCreatedAtDesc();
}
