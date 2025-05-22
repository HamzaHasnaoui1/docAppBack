package ma.formation.batch;

import ma.formation.service.AppointmentReminderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AppointmentReminderBatch {
    
    private static final Logger logger = LoggerFactory.getLogger(AppointmentReminderBatch.class);
    
    @Autowired
    private AppointmentReminderService appointmentReminderService;

    @Scheduled(cron = "0 0 10 * * ?") // 10h00
    public void sendAppointmentReminders() {
        logger.info("Démarrage du batch de rappel des rendez-vous");
        try {
            appointmentReminderService.sendRemindersForTomorrowAppointments();
            logger.info("Batch de rappel des rendez-vous terminé avec succès");
        } catch (Exception e) {
            logger.error("Erreur lors de l'exécution du batch de rappel des rendez-vous", e);
        }
    }
} 