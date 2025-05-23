package ma.formation.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.formation.dtos.OrdonnanceDTO;
import ma.formation.dtos.OrdonnanceRequest;
import ma.formation.security.annotations.RequirePermission;
import ma.formation.service.OrdonnanceService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/user/ordonnances")
    @RequirePermission("ORDONNANCE_CONSULTER")
    public ResponseEntity<Page<OrdonnanceDTO>> getAllOrdonnances(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String keyword) {
        return ResponseEntity.ok(ordonnanceService.getAllOrdonnances(keyword, page, size));
    }

    @GetMapping("/user/ordonnances/{id}")
    @RequirePermission("ORDONNANCE_CONSULTER")
    public ResponseEntity<OrdonnanceDTO> getOrdonnanceById(@PathVariable Long id) {
        return ResponseEntity.ok(ordonnanceService.getOrdonnanceById(id));
    }

    @PostMapping("/admin/ordonnances/{rdvId}")
    @RequirePermission("ORDONNANCE_GENERER")
    public ResponseEntity<OrdonnanceDTO> createOrdonnance(
            @PathVariable Long rdvId,
            @RequestBody OrdonnanceRequest ordonnanceRequest) {
        return ResponseEntity.ok(ordonnanceService.createOrdonnance(rdvId, ordonnanceRequest));
    }

    @PutMapping("/admin/ordonnances/{id}")
    @RequirePermission("ORDONNANCE_MODIFIER")
    public ResponseEntity<OrdonnanceDTO> updateOrdonnance(
            @PathVariable Long id,
            @RequestBody OrdonnanceRequest ordonnanceRequest) {
        return ResponseEntity.ok(ordonnanceService.updateOrdonnance(id, ordonnanceRequest));
    }

    @DeleteMapping("/admin/ordonnances/{id}")
    @RequirePermission("ORDONNANCE_SUPPRIMER")
    public ResponseEntity<Void> deleteOrdonnance(@PathVariable Long id) {
        ordonnanceService.deleteOrdonnance(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/admin/ordonnances/{id}/archiver")
    @RequirePermission("ORDONNANCE_MODIFIER")
    public ResponseEntity<OrdonnanceDTO> archiverOrdonnance(@PathVariable Long id) {
        return ResponseEntity.ok(ordonnanceService.archiverOrdonnance(id));
    }

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

    @GetMapping("/user/patients/{patientId}/ordonnances/history")
    public ResponseEntity<List<OrdonnanceDTO>> getOrdonnanceHistoryByPatient(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(ordonnanceService.getOrdonnancesByPatient(patientId));
    }
}