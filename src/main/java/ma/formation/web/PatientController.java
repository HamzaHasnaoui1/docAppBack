package ma.formation.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.formation.dtos.PatientDTO;
import ma.formation.entities.Patient;
import ma.formation.service.PatientService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PatientController {

    private final PatientService patientService;

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/patients")
    public ResponseEntity<?> getPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Long medecinId) {

        Page<PatientDTO> pagePatients;
        if (medecinId != null) {
            pagePatients = patientService.searchPatientsByMedecin(medecinId, keyword, page, size);
        } else {
            pagePatients = patientService.globalSearchPatients(keyword, page, size);
        }

        var response = new HashMap<String, Object>();
        response.put("patients", pagePatients.getContent());
        response.put("totalPages", pagePatients.getTotalPages());
        response.put("currentPage", page);
        response.put("keyword", keyword);

        return ResponseEntity.ok(response);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/medecins/{medecinId}/patients")
    public ResponseEntity<?> getPatientsByMedecin(
            @PathVariable Long medecinId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<PatientDTO> pagePatients = patientService.getPatientsByMedecin(medecinId, page, size);

        var response = new HashMap<String, Object>();
        response.put("patients", pagePatients.getContent());
        response.put("totalPages", pagePatients.getTotalPages());
        response.put("currentPage", page);

        return ResponseEntity.ok(response);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PostMapping("/user/patients")
    public ResponseEntity<PatientDTO> savePatient(@Valid @RequestBody PatientDTO patient) {
        return ResponseEntity.ok(patientService.createPatientWithDossier(patient));
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/patients/{id}")
    public ResponseEntity<PatientDTO> getPatient(@PathVariable Long id) {
        return patientService.getPatient(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PutMapping("/admin/patients/{id}")
    public ResponseEntity<PatientDTO> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientDTO patientInput) {

        if (!patientService.exists(id)) {
            return ResponseEntity.notFound().build();
        }

        PatientDTO updatedPatient = patientService.update(id, patientInput);
        return ResponseEntity.ok(updatedPatient);
    }


    @Secured("ROLE_ADMIN")
    @DeleteMapping("/admin/patients/{id}")
    public ResponseEntity<?> deletePatient(@PathVariable Long id) {
        if (!patientService.exists(id)) return ResponseEntity.notFound().build();
        patientService.delete(id);
        return ResponseEntity.ok().build();
    }
}
