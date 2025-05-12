package ma.formation.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.formation.dtos.OrdonnanceDTO;
import ma.formation.service.OrdonnanceService;
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
public class OrdonnanceController {
    private final OrdonnanceService ordonnanceService;

    @Secured("ROLE_ADMIN")
    @PostMapping("/admin/ordonnances/{consultationId}")
    public ResponseEntity<OrdonnanceDTO> createOrdonnance(
            @PathVariable Long consultationId,
            @Valid @RequestBody OrdonnanceDTO ordonnanceDTO) {
        return ResponseEntity.ok(ordonnanceService.createOrdonnance(ordonnanceDTO, consultationId));
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/ordonnances/{id}")
    public ResponseEntity<OrdonnanceDTO> getOrdonnance(@PathVariable Long id) {
        return ResponseEntity.ok(ordonnanceService.getOrdonnance(id));
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/ordonnances")
    public ResponseEntity<Map<String, Object>> getOrdonnances(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "") String keyword) {

        Page<OrdonnanceDTO> pageOrdonnances = ordonnanceService.searchOrdonnances(keyword, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("ordonnances", pageOrdonnances.getContent());
        response.put("totalPages", pageOrdonnances.getTotalPages());
        response.put("currentPage", page);
        response.put("totalItems", pageOrdonnances.getTotalElements());
        response.put("keyword", keyword);

        return ResponseEntity.ok(response);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/patients/{patientId}/ordonnances")
    public ResponseEntity<Map<String, Object>> getOrdonnancesByPatient(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<OrdonnanceDTO> pageOrdonnances = ordonnanceService.getOrdonnancesByPatientPageable(patientId, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("ordonnances", pageOrdonnances.getContent());
        response.put("totalPages", pageOrdonnances.getTotalPages());
        response.put("currentPage", page);
        response.put("totalItems", pageOrdonnances.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/admin/ordonnances/{id}")
    public ResponseEntity<OrdonnanceDTO> updateOrdonnance(
            @PathVariable Long id,
            @Valid @RequestBody OrdonnanceDTO ordonnanceDTO) {
        return ResponseEntity.ok(ordonnanceService.updateOrdonnance(id, ordonnanceDTO));
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/admin/ordonnances/{id}")
    public ResponseEntity<Void> deleteOrdonnance(@PathVariable Long id) {
        ordonnanceService.deleteOrdonnance(id);
        return ResponseEntity.ok().build();
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PostMapping("/admin/ordonnances/{id}/archiver")
    public ResponseEntity<OrdonnanceDTO> archiverOrdonnance(@PathVariable Long id) {
        return ResponseEntity.ok(ordonnanceService.archiverOrdonnance(id));
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/patients/{patientId}/ordonnances/history")
    public ResponseEntity<List<OrdonnanceDTO>> getOrdonnanceHistoryByPatient(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(ordonnanceService.getOrdonnancesByPatient(patientId));
    }
}