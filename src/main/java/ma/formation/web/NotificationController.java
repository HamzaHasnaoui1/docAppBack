package ma.formation.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.formation.dtos.NotificationDTO;
import ma.formation.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @Secured("ROLE_ADMIN")
    @PostMapping("/admin/notifications")
    public ResponseEntity<NotificationDTO> sendNotification(@Valid @RequestBody NotificationDTO notificationDTO) {
        return ResponseEntity.ok(notificationService.sendNotification(notificationDTO));
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/patients/{patientId}/notifications/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(@PathVariable Long patientId) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(patientId));
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/patients/{patientId}/notifications")
    public ResponseEntity<Map<String, Object>> getAllNotificationsForPatient(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<NotificationDTO> notificationsPage = notificationService.getAllNotificationsForPatient(patientId, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("notifications", notificationsPage.getContent());
        response.put("currentPage", notificationsPage.getNumber());
        response.put("totalItems", notificationsPage.getTotalElements());
        response.put("totalPages", notificationsPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PutMapping("/user/notifications/{id}/read")
    public ResponseEntity<NotificationDTO> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PutMapping("/user/patients/{patientId}/notifications/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long patientId) {
        notificationService.markAllAsRead(patientId);
        return ResponseEntity.ok().build();
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/admin/notifications/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok().build();
    }
}