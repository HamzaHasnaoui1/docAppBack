package ma.formation.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.formation.entities.Ordonnance;
import ma.formation.repositories.OrdonnanceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
@RequiredArgsConstructor
public class OrdonnanceController {
    private final OrdonnanceRepository ordonnanceRepository;

    @Secured("ROLE_ADMIN")
    @PostMapping("/admin/ordonnances")
    public ResponseEntity<Ordonnance> create(@Valid @RequestBody Ordonnance ordonnance) {
        Ordonnance saved = ordonnanceRepository.save(ordonnance);
        return ResponseEntity.ok(saved);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/ordonnances/{id}")
    public ResponseEntity<Ordonnance> get(@PathVariable Long id) {
        return ordonnanceRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/ordonnances")
    public ResponseEntity<?> getOrdonnances(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "") String keyword) {

        Page<Ordonnance> pageOrdonnances =
                ordonnanceRepository.findByConsultation_RendezVous_Patient_NomContains(keyword, PageRequest.of(page, size));

        HashMap<String, Object> response = new HashMap<>();
        response.put("ordonnances", pageOrdonnances.getContent());
        response.put("totalPages", pageOrdonnances.getTotalPages());
        response.put("currentPage", page);
        response.put("keyword", keyword);

        return ResponseEntity.ok(response);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/admin/ordonnances/{id}")
    public ResponseEntity<Ordonnance> update(
            @PathVariable Long id,
            @Valid @RequestBody Ordonnance ordonnance) {

        if (!ordonnanceRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        ordonnance.setId(id);
        Ordonnance updated = ordonnanceRepository.save(ordonnance);
        return ResponseEntity.ok(updated);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/admin/ordonnances/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        ordonnanceRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
