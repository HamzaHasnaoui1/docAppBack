package ma.formation.service;

import lombok.AllArgsConstructor;
import ma.formation.entities.RendezVous;
import ma.formation.repositories.RendezVousRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TimeSlotService {

    private final RendezVousRepository rendezVousRepository;

    public record TimeSlot(LocalDateTime start, LocalDateTime end) {}

    /**
     * Génère les créneaux horaires d'une journée de travail selon des paramètres prédéfinis.
     */
    public List<TimeSlot> generateDailySlots(LocalDate date, LocalTime startHour, LocalTime endHour, int slotMinutes) {
        List<TimeSlot> slots = new ArrayList<>();
        LocalDateTime start = date.atTime(startHour);
        LocalDateTime end = date.atTime(endHour);

        while (!start.plusMinutes(slotMinutes).isAfter(end)) {
            slots.add(new TimeSlot(start, start.plusMinutes(slotMinutes)));
            start = start.plusMinutes(slotMinutes);
        }
        return slots;
    }

    /**
     * Exclut les créneaux déjà occupés par d'autres rendez-vous.
     */
    public List<TimeSlot> filterUnavailableSlots(List<TimeSlot> allSlots, List<LocalDateTime> takenTimes, int slotMinutes) {
        return allSlots.stream()
                .filter(slot -> takenTimes.stream().noneMatch(taken ->
                        !taken.isBefore(slot.start()) && taken.isBefore(slot.end())))
                .collect(Collectors.toList());
    }

    /**
     * Récupère les créneaux libres pour un médecin donné à une date donnée.
     */
    public List<TimeSlot> getAvailableSlotsForMedecin(Long medecinId, LocalDate date, LocalTime start, LocalTime end, int slotMinutes) {
        List<TimeSlot> allSlots = generateDailySlots(date, start, end, slotMinutes);
        List<RendezVous> rdvs = rendezVousRepository.findByMedecinIdAndDate(medecinId, java.sql.Date.valueOf(date));
        List<LocalDateTime> taken = rdvs.stream()
                .map(r -> r.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime())
                .collect(Collectors.toList());

        return filterUnavailableSlots(allSlots, taken, slotMinutes);
    }
}