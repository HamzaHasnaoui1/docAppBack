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
            @RequestParam(defaultValue = "") String keyword) {

        Page<PatientDTO> pagePatients = patientService.searchPatients(keyword, page, size);

        var response = new HashMap<String, Object>();
        response.put("patients", pagePatients.getContent());
        response.put("totalPages", pagePatients.getTotalPages());
        response.put("currentPage", page);
        response.put("keyword", keyword);

        return ResponseEntity.ok(response);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/admin/patients")
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

    @Secured("ROLE_ADMIN")
    @PutMapping("/admin/patients/{id}")
    public ResponseEntity<PatientDTO> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody Patient patient) {

        if (!patientService.exists(id)) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(patientService.update(id, patient));
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/admin/patients/{id}")
    public ResponseEntity<?> deletePatient(@PathVariable Long id) {
        if (!patientService.exists(id)) return ResponseEntity.notFound().build();
        patientService.delete(id);
        return ResponseEntity.ok().build();
    }
}
