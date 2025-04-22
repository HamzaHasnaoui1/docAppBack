package ma.formation.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.formation.entities.DisponibiliteMedecin;
import ma.formation.repositories.DisponibiliteMedecinRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
@RequiredArgsConstructor
public class DisponibiliteController {
    private final DisponibiliteMedecinRepository disponibiliteMedecinRepository;

    @Secured("ROLE_ADMIN")
    @PostMapping("/admin/dispoMedecin")
    public ResponseEntity<DisponibiliteMedecin> save(@RequestBody DisponibiliteMedecin d) {
        return ResponseEntity.ok(disponibiliteMedecinRepository.save(d));
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/dispoMedecin")
    public ResponseEntity<?> getMedecins(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "") String keyword) {

        Page<DisponibiliteMedecin> pageDispo = disponibiliteMedecinRepository.findByMedecinContains(keyword, PageRequest.of(page, size));

        var response = new java.util.HashMap<String, Object>();
        response.put("disponibiliteMedecins", pageDispo.getContent());
        response.put("totalPages", pageDispo.getTotalPages());
        response.put("currentPage", page);
        response.put("keyword", keyword);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/dispoMedecin/{id}")
    public List<DisponibiliteMedecin> byMedecin(@PathVariable Long id) {
        return disponibiliteMedecinRepository.findAll().stream()
                .filter(d -> d.getMedecin().getId().equals(id))
                .toList();
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/admin/dispoMedecin/{id}")
    public ResponseEntity<DisponibiliteMedecin> updateDispo(
            @PathVariable Long id,
            @Valid @RequestBody DisponibiliteMedecin dispo) {

        if (!disponibiliteMedecinRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        dispo.setId(id);
        DisponibiliteMedecin updated = disponibiliteMedecinRepository.save(dispo);
        return ResponseEntity.ok(updated);
    }


}