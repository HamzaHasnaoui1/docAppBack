package ma.formation.web;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import ma.formation.entities.Medecin;
import ma.formation.repositories.MedecinRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@CrossOrigin("*")
public class MedecinController {
    private MedecinRepository medecinRepository;

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/medecins")
    public ResponseEntity<?> getMedecins(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "") String keyword) {

        Page<Medecin> pageMedecins = medecinRepository.findByNomContains(keyword, PageRequest.of(page, size));

        var response = new java.util.HashMap<String, Object>();
        response.put("medecins", pageMedecins.getContent());
        response.put("totalPages", pageMedecins.getTotalPages());
        response.put("currentPage", page);
        response.put("keyword", keyword);

        return ResponseEntity.ok(response);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/admin/medecins")
    public ResponseEntity<Medecin> saveMedecin(@Valid @RequestBody Medecin medecin) {
        Medecin savedMedecin = medecinRepository.save(medecin);
        return ResponseEntity.ok(savedMedecin);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/medecins/{id}")
    public ResponseEntity<Medecin> getMedecin(@PathVariable Long id) {
        return medecinRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/admin/medecins/{id}")
    public ResponseEntity<Medecin> updateMedecin(
            @PathVariable Long id,
            @Valid @RequestBody Medecin medecin) {

        if (!medecinRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        medecin.setId(id);
        Medecin updatedMedecin = medecinRepository.save(medecin);
        return ResponseEntity.ok(updatedMedecin);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/admin/medecins/{id}")
    public ResponseEntity<?> deleteMedecin(@PathVariable Long id) {
        medecinRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}