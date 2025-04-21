package ma.formation.web;

import lombok.AllArgsConstructor;
import ma.formation.entities.Patient;
import ma.formation.repositories.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@CrossOrigin("*")
public class PatientController {
    private PatientRepository patientRepository;

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/patients")
    public ResponseEntity<?> getPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "") String keyword) {

        Page<Patient> pagePatients = patientRepository.findByNomContains(keyword, PageRequest.of(page, size));

        var response = new java.util.HashMap<String, Object>();
        response.put("patients", pagePatients.getContent());
        response.put("totalPages", pagePatients.getTotalPages());
        response.put("currentPage", page);
        response.put("keyword", keyword);

        return ResponseEntity.ok(response);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/admin/patients")
    public ResponseEntity<Patient> savePatient(@Valid @RequestBody Patient patient) {
        Patient savedPatient = patientRepository.save(patient);
        return ResponseEntity.ok(savedPatient);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/patients/{id}")
    public ResponseEntity<Patient> getPatient(@PathVariable Long id) {
        return patientRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/admin/patients/{id}")
    public ResponseEntity<Patient> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody Patient patient) {

        if (!patientRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        patient.setId(id);
        Patient updatedPatient = patientRepository.save(patient);
        return ResponseEntity.ok(updatedPatient);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/admin/patients/{id}")
    public ResponseEntity<?> deletePatient(@PathVariable Long id) {
        if (!patientRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        patientRepository.deleteById(id);
        return ResponseEntity.ok().build();

    }
}