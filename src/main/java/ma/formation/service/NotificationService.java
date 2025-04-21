package ma.formation.service;

import lombok.RequiredArgsConstructor;
import ma.formation.entities.Notification;
import ma.formation.repositories.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public Notification envoyer(Notification notif) {
        notif.setDateEnvoi(LocalDateTime.now());
        notif.setLu(false);
        return notificationRepository.save(notif);
    }

    public List<Notification> notificationsNonLues(Long patientId) {
        return notificationRepository.findAll().stream()
                .filter(n -> n.getPatient().getId().equals(patientId) && !n.isLu())
                .toList();
    }
}
