package ma.formation.web;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import ma.formation.entities.Medecin;
import ma.formation.entities.Patient;
import ma.formation.entities.RendezVous;
import ma.formation.repositories.MedecinRepository;
import ma.formation.repositories.PatientRepository;
import ma.formation.repositories.RendezVousRepository;
import ma.formation.service.IHopitalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@CrossOrigin("*")
public class RDVController {
    private final RendezVousRepository rendezVousRepository;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;
    private final IHopitalService hopitalService;

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/rdv")
    public ResponseEntity<?> getRendezVous(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<RendezVous> pageRDV = rendezVousRepository.findAll(PageRequest.of(page, size));

        var response = new java.util.HashMap<String, Object>();
        response.put("rdvs", pageRDV.getContent());
        response.put("totalPages", pageRDV.getTotalPages());
        response.put("currentPage", page);

        return ResponseEntity.ok(response);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/rdv/form-data")
    public ResponseEntity<?> getFormData() {
        var response = new java.util.HashMap<String, Object>();
        response.put("patients", patientRepository.findAll());
        response.put("medecins", medecinRepository.findAll());
        return ResponseEntity.ok(response);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/admin/rdv")
    public ResponseEntity<RendezVous> saveRDV(
            @Valid @RequestBody RendezVous rendezVous) {

        Patient patient = patientRepository.findById(rendezVous.getPatient().getId())
                .orElseThrow(() -> new RuntimeException("Patient introuvable"));
        Medecin medecin = medecinRepository.findById(rendezVous.getMedecin().getId())
                .orElseThrow(() -> new RuntimeException("Médecin introuvable"));

        rendezVous.setPatient(patient);
        rendezVous.setMedecin(medecin);
        RendezVous savedRDV = hopitalService.saveRendezVous(rendezVous);

        return ResponseEntity.ok(savedRDV);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/admin/rdv/{id}")
    public ResponseEntity<?> deleteRDV(@PathVariable Long id) {
        rendezVousRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/rdv/{id}")
    public ResponseEntity<?> getRDVForEdit(@PathVariable Long id) {
        RendezVous rdv = rendezVousRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rendez-vous introuvable"));

        var response = new java.util.HashMap<String, Object>();
        response.put("rdv", rdv);
        response.put("patients", patientRepository.findAll());
        response.put("medecins", medecinRepository.findAll());

        return ResponseEntity.ok(response);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/admin/rdv/{id}")
    public ResponseEntity<RendezVous> updateRDV(
            @PathVariable Long id,
            @Valid @RequestBody RendezVous rendezVous) {

        if (!rendezVousRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        Patient patient = patientRepository.findById(rendezVous.getPatient().getId())
                .orElseThrow(() -> new RuntimeException("Patient introuvable"));
        Medecin medecin = medecinRepository.findById(rendezVous.getMedecin().getId())
                .orElseThrow(() -> new RuntimeException("Médecin introuvable"));

        rendezVous.setId(id);
        rendezVous.setPatient(patient);
        rendezVous.setMedecin(medecin);
        RendezVous updatedRDV = hopitalService.saveRendezVous(rendezVous);

        return ResponseEntity.ok(updatedRDV);
    }
}