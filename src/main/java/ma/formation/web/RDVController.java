package ma.formation.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.formation.dtos.PatientDTO;
import ma.formation.dtos.RendezVousDTO;
import ma.formation.entities.Medecin;
import ma.formation.entities.Patient;
import ma.formation.repositories.MedecinRepository;
import ma.formation.repositories.PatientRepository;
import ma.formation.service.RendezVousService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin("*")
public class RDVController {
    private final RendezVousService rendezVousService;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/rdv/all")
    public ResponseEntity<List<RendezVousDTO>> getAllRendezVous() {
        return ResponseEntity.ok(rendezVousService.getAllRendezVous());
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/rdv")
    public ResponseEntity<Map<String, Object>> getRendezVous(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<RendezVousDTO> rdvPage = rendezVousService.getRendezVousPaginated(page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("rdvs", rdvPage.getContent());
        response.put("currentPage", rdvPage.getNumber());
        response.put("totalItems", rdvPage.getTotalElements());
        response.put("totalPages", rdvPage.getTotalPages());

        return ResponseEntity.ok(response);
    }



    /*@Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/rdv/{id}")
    public ResponseEntity<RendezVousDTO> getRendezVousById(@PathVariable Long id) {
        return ResponseEntity.ok(rendezVousService.getRendezVousById(id));
    }*/

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/rdv/{id}")
    public ResponseEntity<RendezVousDTO> getRendezVousById(@PathVariable Long id) {
        return rendezVousService.getRendezVousById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PostMapping("/admin/rdv")
    public ResponseEntity<RendezVousDTO> createRendezVous(@Valid @RequestBody RendezVousDTO rendezVousDTO) {
        return ResponseEntity.ok(rendezVousService.createRendezVous(rendezVousDTO));
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PutMapping("/admin/rdv/{id}")
    public ResponseEntity<RendezVousDTO> updateRendezVous(
            @PathVariable Long id,
            @Valid @RequestBody RendezVousDTO rendezVousDTO) {
        return ResponseEntity.ok(rendezVousService.updateRendezVous(id, rendezVousDTO));
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/admin/rdv/{id}")
    public ResponseEntity<Void> deleteRendezVous(@PathVariable Long id) {
        rendezVousService.deleteRendezVous(id);
        return ResponseEntity.ok().build();
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PutMapping("/admin/rdv/{id}/status")
    public ResponseEntity<RendezVousDTO> changeRendezVousStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(rendezVousService.changeRendezVousStatus(id, status));
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/patients/{patientId}/rdv")
    public ResponseEntity<List<RendezVousDTO>> getRendezVousByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(rendezVousService.getRendezVousByPatient(patientId));
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/medecins/{medecinId}/rdv")
    public ResponseEntity<List<RendezVousDTO>> getRendezVousByMedecin(@PathVariable Long medecinId) {
        return ResponseEntity.ok(rendezVousService.getRendezVousByMedecin(medecinId));
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/rdv/date")
    public ResponseEntity<List<RendezVousDTO>> getRendezVousByDate(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        return ResponseEntity.ok(rendezVousService.getRendezVousByDate(date));
    }
}