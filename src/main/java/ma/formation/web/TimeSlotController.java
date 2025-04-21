package ma.formation.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.formation.service.TimeSlotService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/medecins")
@AllArgsConstructor
@CrossOrigin("*")
public class TimeSlotController {

    private final TimeSlotService timeSlotService;@GetMapping("/{id}/slots")
    public ResponseEntity<List<TimeSlotService.TimeSlot>> getSlots(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "09:00") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime start,
            @RequestParam(defaultValue = "17:00") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime end,
            @RequestParam(defaultValue = "30") int slotMinutes) {

        List<TimeSlotService.TimeSlot> slots = timeSlotService.getAvailableSlotsForMedecin(id, date, start, end, slotMinutes);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(slots);
    }
}