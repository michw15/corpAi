package pl.pkobp.corpai.notification.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pkobp.corpai.notification.domain.Notification;
import pl.pkobp.corpai.notification.repository.NotificationRepository;

import java.util.List;

/**
 * REST controller exposing advisor notifications.
 */
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;

    /**
     * Returns all notifications ordered by creation time (newest first).
     */
    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications() {
        return ResponseEntity.ok(notificationRepository.findAllByOrderByCreatedAtDesc());
    }
}
