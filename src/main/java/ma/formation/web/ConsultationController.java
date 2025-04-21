package ma.formation.web;

import lombok.AllArgsConstructor;
import ma.formation.entities.Consultation;
import ma.formation.entities.Medecin;
import ma.formation.entities.RendezVous;
import ma.formation.repositories.ConsultationRepository;
import ma.formation.repositories.RendezVousRepository;
import ma.formation.service.IHopitalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@CrossOrigin("*")
public class ConsultationController {
    private final ConsultationRepository consultationRepository;
    private final IHopitalService hopitalService;
    private final RendezVousRepository rendezVousRepository;

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/consultations")
    public ResponseEntity<?> getConsultations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<Consultation> pageConsultations = consultationRepository.findAll(PageRequest.of(page, size));

        var response = new java.util.HashMap<String, Object>();
        response.put("consultations", pageConsultations.getContent());
        response.put("totalPages", pageConsultations.getTotalPages());
        response.put("currentPage", page);

        return ResponseEntity.ok(response);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/admin/consultations/{id}")
    public ResponseEntity<?> deleteConsultation(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page) {

        consultationRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/admin/consultations")
    public ResponseEntity<Consultation> saveConsultation(
            @Valid @RequestBody Consultation consultation,
            @RequestParam(name = "rendezVousId") Long rendezVousId) {

        RendezVous rendezVous = rendezVousRepository.findById(rendezVousId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid rendez-vous Id: " + rendezVousId));

        consultation.setRendezVous(rendezVous);
        Consultation savedConsultation = hopitalService.saveConsultation(consultation);

        return ResponseEntity.ok(savedConsultation);
    }
    @Secured("ROLE_ADMIN")
    @PutMapping("/admin/consultations/{id}")
    public ResponseEntity<Consultation> updateConsultation(
            @PathVariable Long id,
            @Valid @RequestBody Consultation consultation) {

        if (!consultationRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        consultation.setId(id);
        Consultation updatedConsultation = consultationRepository.save(consultation);
        return ResponseEntity.ok(updatedConsultation);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/consultations/form-data")
    public ResponseEntity<?> getFormData() {
        var response = new java.util.HashMap<String, Object>();
        response.put("rendezvous", rendezVousRepository.findAll());
        return ResponseEntity.ok(response);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/consultations/{id}")
    public ResponseEntity<Consultation> getConsultationForEdit(@PathVariable Long id) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consultation introuvable"));

        return ResponseEntity.ok(consultation);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/consultations/{id}/facture")
    public ResponseEntity<Consultation> getConsultationForFacture(@PathVariable Long id) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consultation introuvable"));

        return ResponseEntity.ok(consultation);
    }
}