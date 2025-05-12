package ma.formation.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.formation.dtos.DossierMedicalDTO;
import ma.formation.service.DossierMedicalService;
import org.springframework.data.domain.Page;
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
    private final DossierMedicalService dossierMedicalService;

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/dossiers-medicaux")
    public ResponseEntity<Map<String, Object>> getAllDossiersMedicaux(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<DossierMedicalDTO> dossierPage = dossierMedicalService.getAllDossiersMedicaux(page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("dossiers", dossierPage.getContent());
        response.put("currentPage", dossierPage.getNumber());
        response.put("totalItems", dossierPage.getTotalElements());
        response.put("totalPages", dossierPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/dossiers-medicaux/{id}")
    public ResponseEntity<DossierMedicalDTO> getDossierMedical(@PathVariable Long id) {
        return ResponseEntity.ok(dossierMedicalService.getDossierMedical(id));
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/patients/{patientId}/dossier-medical")
    public ResponseEntity<DossierMedicalDTO> getDossierMedicalByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(dossierMedicalService.getDossierMedicalByPatient(patientId));
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/admin/patients/{patientId}/dossier-medical")
    public ResponseEntity<DossierMedicalDTO> createDossierMedical(
            @PathVariable Long patientId,
            @Valid @RequestBody DossierMedicalDTO dossierDTO) {

        return ResponseEntity.ok(dossierMedicalService.createDossierMedical(dossierDTO, patientId));
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/admin/dossiers-medicaux/{id}")
    public ResponseEntity<DossierMedicalDTO> updateDossierMedical(
            @PathVariable Long id,
            @Valid @RequestBody DossierMedicalDTO dossierDTO) {

        return ResponseEntity.ok(dossierMedicalService.updateDossierMedical(id, dossierDTO));
    }

    @Secured("ROLE_ADMIN")
    @PatchMapping("/admin/dossiers-medicaux/{id}/groupe-sanguin")
    public ResponseEntity<DossierMedicalDTO> updateGroupeSanguin(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        String groupeSanguin = request.get("groupeSanguin");
        return ResponseEntity.ok(dossierMedicalService.updateGroupeSanguin(id, groupeSanguin));
    }

    @Secured("ROLE_ADMIN")
    @PatchMapping("/admin/dossiers-medicaux/{id}/allergies")
    public ResponseEntity<DossierMedicalDTO> updateAllergies(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        String allergies = request.get("allergies");
        return ResponseEntity.ok(dossierMedicalService.updateAllergies(id, allergies));
    }

    @Secured("ROLE_ADMIN")
    @PatchMapping("/admin/dossiers-medicaux/{id}/antecedents")
    public ResponseEntity<DossierMedicalDTO> updateAntecedents(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        String antecedents = request.get("antecedents");
        return ResponseEntity.ok(dossierMedicalService.updateAntecedents(id, antecedents));
    }

    @Secured("ROLE_ADMIN")
    @PatchMapping("/admin/dossiers-medicaux/{id}/traitements")
    public ResponseEntity<DossierMedicalDTO> updateTraitementsChroniques(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        String traitements = request.get("traitements");
        return ResponseEntity.ok(dossierMedicalService.updateTraitementsChroniques(id, traitements));
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/admin/dossiers-medicaux/{id}")
    public ResponseEntity<Void> deleteDossierMedical(@PathVariable Long id) {
        dossierMedicalService.deleteDossierMedical(id);
        return ResponseEntity.ok().build();
    }
}