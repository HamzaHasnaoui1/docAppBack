package ma.formation.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.formation.entities.DossierMedical;
import ma.formation.repositories.DossierMedicalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
@RequiredArgsConstructor
public class DossierMedicalController {
    private final DossierMedicalRepository dossierMedicalRepository;

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/dossierMedical")
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<DossierMedical> pageDossiers = dossierMedicalRepository.findAll(PageRequest.of(page, size));

        Map<String, Object> response = new HashMap<>();
        response.put("dossiers", pageDossiers.getContent());
        response.put("currentPage", pageDossiers.getNumber());
        response.put("totalPages", pageDossiers.getTotalPages());
        response.put("totalElements", pageDossiers.getTotalElements());
        response.put("pageSize", pageDossiers.getSize());

        return ResponseEntity.ok(response);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/admin/dossierMedical")
    public ResponseEntity<DossierMedical> create(@RequestBody DossierMedical dossier) {
        DossierMedical savedDossier = dossierMedicalRepository.save(dossier);
        return ResponseEntity.ok(savedDossier);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/admin/dossierMedical/{id}")
    public ResponseEntity<DossierMedical> get(@PathVariable Long id) {
        return dossierMedicalRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/dossierMedical/patient/{patientId}")
    public ResponseEntity<Map<String, Object>> findByPatient(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<DossierMedical> pageDossiers = dossierMedicalRepository
                .findByPatientId(patientId, PageRequest.of(page, size));

        Map<String, Object> response = new HashMap<>();
        response.put("dossiers", pageDossiers.getContent());
        response.put("currentPage", pageDossiers.getNumber());
        response.put("totalPages", pageDossiers.getTotalPages());
        response.put("totalElements", pageDossiers.getTotalElements());
        response.put("pageSize", pageDossiers.getSize());

        return ResponseEntity.ok(response);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/admin/dossierMedical/{id}")
    public ResponseEntity<DossierMedical> updateDossierMedical(
            @PathVariable Long id,
            @Valid @RequestBody DossierMedical dossierMedical) {

        if (!dossierMedicalRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        dossierMedical.setId(id);
        DossierMedical updated = dossierMedicalRepository.save(dossierMedical);
        return ResponseEntity.ok(updated);
    }


}