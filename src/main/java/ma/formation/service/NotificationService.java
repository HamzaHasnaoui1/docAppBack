package ma.formation.service;

import lombok.RequiredArgsConstructor;
import ma.formation.dtos.NotificationDTO;
import ma.formation.entities.Notification;
import ma.formation.entities.Patient;
import ma.formation.entities.RendezVous;
import ma.formation.enums.StatusRDV;
import ma.formation.exceptions.ResourceNotFoundException;
import ma.formation.mappers.NotificationMapper;
import ma.formation.repositories.NotificationRepository;
import ma.formation.repositories.PatientRepository;
import ma.formation.repositories.RendezVousRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final PatientRepository patientRepository;
    private final RendezVousRepository rendezVousRepository;
    private final NotificationMapper notificationMapper;

    @Transactional
    public NotificationDTO sendNotification(NotificationDTO notificationDTO) {
        Patient patient = patientRepository.findById(notificationDTO.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", notificationDTO.getPatientId()));

        Notification notification = new Notification();
        notification.setMessage(notificationDTO.getMessage());
        notification.setLu(false);
        notification.setDateEnvoi(LocalDateTime.now());
        notification.setPatient(patient);

        Notification savedNotification = notificationRepository.save(notification);
        return notificationMapper.toDTO(savedNotification);
    }

    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnreadNotifications(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient", "id", patientId);
        }

        List<Notification> notifications = notificationRepository.findByPatientIdAndLuFalseOrderByDateEnvoiDesc(patientId);
        return notifications.stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<NotificationDTO> getAllNotificationsForPatient(Long patientId, int page, int size) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient", "id", patientId);
        }

        Page<Notification> notificationsPage = notificationRepository.findByPatientIdOrderByDateEnvoiDesc(patientId, PageRequest.of(page, size));
        List<NotificationDTO> notificationDTOs = notificationsPage.getContent().stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(notificationDTOs, notificationsPage.getPageable(), notificationsPage.getTotalElements());
    }

    @Transactional
    public NotificationDTO markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        notification.setLu(true);
        Notification updatedNotification = notificationRepository.save(notification);
        return notificationMapper.toDTO(updatedNotification);
    }

    @Transactional
    public void markAllAsRead(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient", "id", patientId);
        }

        notificationRepository.markAllAsRead(patientId);
    }

    @Transactional
    public void deleteNotification(Long notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new ResourceNotFoundException("Notification", "id", notificationId);
        }

        notificationRepository.deleteById(notificationId);
    }

    /**
     * Tâche planifiée pour envoyer des rappels de rendez-vous
     * S'exécute tous les jours à minuit
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void sendAppointmentReminders() {
        // Récupérer les rendez-vous de demain
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1); // demain
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date tomorrowStart = calendar.getTime();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date tomorrowEnd = calendar.getTime();

        List<RendezVous> tomorrowAppointments = rendezVousRepository.findByDateBetween(tomorrowStart, tomorrowEnd);

        // Filtrer seulement les RDV en attente ou confirmés
        List<RendezVous> validAppointments = tomorrowAppointments.stream()
                .filter(rdv -> rdv.getStatusRDV() == StatusRDV.PENDING ||
                        rdv.getStatusRDV() == StatusRDV.CONFIRMED ||
                        rdv.getStatusRDV() == StatusRDV.CANCELLED)
                .collect(Collectors.toList());

        // Envoyer des notifications pour chaque RDV
        for (RendezVous rdv : validAppointments) {
            Patient patient = rdv.getPatient();
            if (patient != null) {
                // Créer la notification
                Notification notification = new Notification();
                notification.setPatient(patient);
                notification.setLu(false);
                notification.setDateEnvoi(LocalDateTime.now());

                // Formater l'heure du RDV
                Calendar rdvTime = Calendar.getInstance();
                rdvTime.setTime(rdv.getDate());
                String formattedTime = String.format("%02d:%02d", rdvTime.get(Calendar.HOUR_OF_DAY), rdvTime.get(Calendar.MINUTE));

                // Message de la notification
                notification.setMessage("Rappel: Vous avez un rendez-vous demain à " + formattedTime +
                        " avec Dr. " + rdv.getMedecin().getNom());

                notificationRepository.save(notification);
            }
        }
    }
}