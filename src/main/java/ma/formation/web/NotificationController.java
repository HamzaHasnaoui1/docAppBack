package ma.formation.web;

import lombok.RequiredArgsConstructor;
import ma.formation.entities.Notification;
import ma.formation.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/user/notification/{patientId}")
    public List<Notification> getNonLues(@PathVariable Long patientId) {
        return notificationService.notificationsNonLues(patientId);
    }

    @PostMapping("/admin/notification/envoyer")
    public Notification envoyer(@RequestBody Notification n) {
        return notificationService.envoyer(n);
    }
}
