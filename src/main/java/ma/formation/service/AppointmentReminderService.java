package ma.formation.service;

import ma.formation.entities.RendezVous;
import ma.formation.repositories.RendezVousRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class AppointmentReminderService {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentReminderService.class);

    @Autowired
    private RendezVousRepository rendezVousRepository;

    @Autowired
    private EmailService emailService;

    public void sendRemindersForTomorrowAppointments() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date tomorrow = calendar.getTime();

        List<RendezVous> tomorrowAppointments = rendezVousRepository.findByDateOnly(tomorrow);
        logger.info("Nombre de rendez-vous trouvés pour demain: {}", tomorrowAppointments.size());
  
        for (RendezVous rendezVous : tomorrowAppointments) {
            try {
                String message = buildReminderMessage(rendezVous);
                emailService.sendEmail(
                    rendezVous.getPatient().getEmail(),
                    "Rappel de rendez-vous - " + rendezVous.getDate(),
                    message
                );
                logger.info("Rappel envoyé avec succès pour le rendez-vous ID: {}", rendezVous.getId());
            } catch (Exception e) {
                logger.error("Erreur lors de l'envoi du rappel pour le rendez-vous ID: " + rendezVous.getId(), e);
            }
        }
    }

    private String buildReminderMessage(RendezVous rendezVous) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(rendezVous.getDate());
        String heure = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

        return String.format(
            "Bonjour %s,\n\n" +
            "Nous vous rappelons que vous avez un rendez-vous demain à %s avec le Dr. %s.\n" +
            "Cordialement,\n" +
            "Votre cabinet médical",
            rendezVous.getPatient().getNom(),
            heure,
            rendezVous.getMedecin().getNom()
        );
    }
} 