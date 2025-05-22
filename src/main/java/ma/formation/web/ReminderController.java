package ma.formation.web;

import lombok.RequiredArgsConstructor;
import ma.formation.service.AppointmentReminderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ReminderController {

    private final AppointmentReminderService appointmentReminderService;

    @Secured("ROLE_ADMIN")
    @PostMapping("/admin/reminders/send")
    public ResponseEntity<String> sendReminders() {
        try {
            appointmentReminderService.sendRemindersForTomorrowAppointments();
            return ResponseEntity.ok("Les rappels ont été envoyés avec succès");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur lors de l'envoi des rappels: " + e.getMessage());
        }
    }
} 